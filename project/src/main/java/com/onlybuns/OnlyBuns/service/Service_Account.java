package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountLogin;
import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountRegister;
import com.onlybuns.OnlyBuns.dto.DTO_View_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.onlybuns.OnlyBuns.util.SimpleBloomFilter;


import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class Service_Account {

    private SimpleBloomFilter usernameBloomFilter;

    @PostConstruct
    public void init() {
        // Initialize the Bloom filter with a size of 1000 bits and 5 hash functions
        usernameBloomFilter = new SimpleBloomFilter(1000, 5);
    }

    @Autowired
    private Repository_Account repositoryAccount;

    public ResponseEntity<List<DTO_View_Account>> api_accounts() {
        List<Account> accounts = repositoryAccount.findAll();
        List<DTO_View_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) { accountDTOS.add(new DTO_View_Account(account)); }
        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Account> api_accounts_id(@PathVariable(name = "id") Integer id, HttpSession session) {
        Optional<Account> foundAccount = repositoryAccount.findById(id);
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_View_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Account> api_myaccount(HttpSession session){
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<Account> foundAccount = repositoryAccount.findById(sessionAccount.getId());
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_View_Account(foundAccount.get()), HttpStatus.OK);
    }

    private static final int MAX_ATTEMPTS = 5;
    private static final long TIME_WINDOW = 60 * 1000; // 1 minute in milliseconds

    // Rate limiter map: ip -> queue of attempt timestamps
    private Map<String, Queue<Long>> loginAttempts = new ConcurrentHashMap<>();

    private boolean isRateLimited(String ip) {
        long currentTime = Instant.now().toEpochMilli();

        // Retrieve or initialize the login attempts queue for the user
        loginAttempts.putIfAbsent(ip, new LinkedList<>());
        Queue<Long> attempts = loginAttempts.get(ip);

        // Remove attempts that are outside the time window
        while (!attempts.isEmpty() && currentTime - attempts.peek() > TIME_WINDOW) { attempts.poll(); }

        // Check if the user has reached the max attempts within the time window
        if (attempts.size() >= MAX_ATTEMPTS) { return true; }

        // Record the current attempt and proceed
        attempts.offer(currentTime);
        return false;
    }

    public ResponseEntity<String> api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session){
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST); }

        if (dto_post_accountLogin.getEmail().isEmpty() || dto_post_accountLogin.getPassword().isEmpty()) { return new ResponseEntity<>("Invalid login data.", HttpStatus.BAD_REQUEST); }

        // Rate limiter check
        String clientIp = request.getRemoteAddr(); // Get the client's IP address
        if (isRateLimited(clientIp)) {
            return new ResponseEntity<>("Too many login attempts. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
        }

        Optional<Account> foundAccount = repositoryAccount.findByEmail(dto_post_accountLogin.getEmail());
        if (foundAccount.isEmpty()) { foundAccount = repositoryAccount.findByUserName(dto_post_accountLogin.getEmail()); }

        if (foundAccount.isEmpty()) { return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND); }

        Account account = foundAccount.get();
        if (!account.getPassword().equals(dto_post_accountLogin.getPassword())) { return new ResponseEntity<>("Wrong password.", HttpStatus.UNAUTHORIZED); }

        session.setAttribute("account", account);
        return new ResponseEntity<>("Logged in as: " + account.getUserName(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST); }

        String message = dto_post_accountRegister.validate();
        if (message != null) { return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST); }

        // Lock to prevent concurrent issues
        synchronized (this) {
            // Check if the username is already in the Bloom filter
            if (usernameBloomFilter.mightContain(dto_post_accountRegister.getUserName())) {
                // If it might be in the Bloom filter, check the database to confirm
                Optional<Account> foundAccount = repositoryAccount.findByUserName(dto_post_accountRegister.getUserName());
                if (foundAccount.isPresent()) {
                    return new ResponseEntity<>("Username exists: " + dto_post_accountRegister.getUserName(), HttpStatus.CONFLICT);
                }
            }

            // Add the username to the Bloom filter after confirming it is new
            usernameBloomFilter.add(dto_post_accountRegister.getUserName());
        }

        Account newAccount = new Account(
                dto_post_accountRegister.getEmail(),
                dto_post_accountRegister.getUserName(),
                dto_post_accountRegister.getPassword(),
                dto_post_accountRegister.getFirstName(),
                dto_post_accountRegister.getLastName(),
                dto_post_accountRegister.getAddress(),
                "/avatars/default.jpg",
                "...",
                AccountRole.USER
        );
        repositoryAccount.save(newAccount);
        session.setAttribute("account", newAccount);
        return new ResponseEntity<>("Registered.", HttpStatus.OK);
    }

    public ResponseEntity<String> logout(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("Already logged out.", HttpStatus.BAD_REQUEST); }

        session.invalidate();
        return new ResponseEntity<>("Logged out: " + sessionAccount.getUserName(), HttpStatus.OK);
    }
}
