package com.onlybuns.OnlyBuns.service;
import com.onlybuns.OnlyBuns.dto.*;
import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.*;
import com.onlybuns.OnlyBuns.util.RateLimiter;
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

import java.util.*;

@Service
public class Service_Account {

    private SimpleBloomFilter bloomFilter_userName;
    private SimpleBloomFilter bloomFilter_email;

    @PostConstruct
    public void init() {
        // Initialize the Bloom filter with a size of 1000 bits and 5 hash functions
        bloomFilter_userName = new SimpleBloomFilter(1000, 5);
        bloomFilter_email = new SimpleBloomFilter(1000, 5);
    }

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Role repository_role;

    @Autowired
    private Repository_AccountActivation repository_accountActivation;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Like repository_likes;

    @Autowired
    private Service_Email service_email;

    @Autowired
    private Service_Post service_post;

    private final RateLimiter rateLimiter = new RateLimiter();

    public ResponseEntity<List<DTO_Get_Account>> get_api_admin_accounts(HttpSession session) {
        Account user = (Account) session.getAttribute("account");
        if (user == null || !user.isAdmin(user)) { return new ResponseEntity<>(null, HttpStatus.FORBIDDEN); }

        List<Account> accounts = repository_account.findAll();
        List<DTO_Get_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) { accountDTOS.add(new DTO_Get_Account(account)); }
        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }

    public ResponseEntity<DTO_Get_Account> get_api_accounts_id(@PathVariable(name = "id") Long id) {
        Optional<Account> foundAccount = repository_account.findById(id);
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_Get_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<DTO_Get_Account> get_api_myaccount(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<Account> foundAccount = repository_account.findById(sessionAccount.getId());
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_Get_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<String> get_api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session){

        // already logged in
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST); }

        // validate input
        String message = dto_post_accountLogin.validate();
        if (message != null) { return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST); }

        // Rate limiter check
        String clientIp = request.getRemoteAddr(); // Get the client's IP address
        if (rateLimiter.isRateLimited(clientIp)) {
            return new ResponseEntity<>("Too many login attempts. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
        }

        // find account
        Optional<Account> opt_account = repository_account.findByEmail(dto_post_accountLogin.getEmail());
        if (opt_account.isEmpty()) { opt_account = repository_account.findByUserName(dto_post_accountLogin.getEmail()); }
        if (opt_account.isEmpty()) { return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND); }

        // account
        Account account = opt_account.get();

        // check if account is activated
        Optional<AccountActivation> opt_accountActivation = repository_accountActivation.findByAccount(account);
        if (opt_accountActivation.isEmpty()) {
            // missing account activation in db -- creating...
            service_email.sendVerificationEmail(account);
            return new ResponseEntity<>("Please verify email.", HttpStatus.UNAUTHORIZED);
        }
        else {
            AccountActivation accountActivation = opt_accountActivation.get();
            if (accountActivation.getStatus() == AccountActivationStatus.WAITING) {
                return new ResponseEntity<>("Please verify email.", HttpStatus.UNAUTHORIZED);
            }
        }

        if (!account.isPassword(dto_post_accountLogin.password)) {
            return new ResponseEntity<>("Wrong password.", HttpStatus.UNAUTHORIZED);
        }

        session.setAttribute("account", account);
        return new ResponseEntity<>("Logged in as: " + account.getUserName(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> get_api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST); }

        String message = dto_post_accountRegister.validate();
        if (message != null) { return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST); }

        // Lock to prevent concurrent issues
        synchronized (this) {

            // Check if the email is already in the Bloom filter
            if (bloomFilter_email.mightContain(dto_post_accountRegister.getEmail())) {
                // If it might be in the Bloom filter, check the database to confirm
                Optional<Account> foundAccount = repository_account.findByEmail(dto_post_accountRegister.getEmail());
                if (foundAccount.isPresent()) {
                    return new ResponseEntity<>("Email exists: " + dto_post_accountRegister.getEmail(), HttpStatus.CONFLICT);
                }
            }
            else {
                // Add the email to the Bloom filter after confirming it is new
                bloomFilter_email.add(dto_post_accountRegister.getEmail());
            }

            // Check if the username is already in the Bloom filter
            if (bloomFilter_userName.mightContain(dto_post_accountRegister.getUserName())) {
                // If it might be in the Bloom filter, check the database to confirm
                Optional<Account> foundAccount = repository_account.findByUserName(dto_post_accountRegister.getUserName());
                if (foundAccount.isPresent()) {
                    return new ResponseEntity<>("Username exists: " + dto_post_accountRegister.getUserName(), HttpStatus.CONFLICT);
                }
            }
            else {
                // Add the username to the Bloom filter after confirming it is new
                bloomFilter_userName.add(dto_post_accountRegister.getUserName());
            }
        }

        // find user role / if not exist create one
        Optional<Role> optional_role = repository_role.findByName("USER");
        Role role = null;
        if (optional_role.isEmpty()) {
            role = optional_role.get();
            repository_role.save(new Role("USER"));
        }




        // create account
        Account newAccount = new Account(
                dto_post_accountRegister.getEmail(),
                dto_post_accountRegister.getUserName(),
                dto_post_accountRegister.getPassword(),
                dto_post_accountRegister.getFirstName(),
                dto_post_accountRegister.getLastName(),
                dto_post_accountRegister.getAddress(),
                "/avatars/default.jpg",
                "...",
                role
        );
        repository_account.save(newAccount);

        // send verification email
        service_email.sendVerificationEmail(newAccount);

        // session.setAttribute("account", newAccount); // can't login need to verify
        return new ResponseEntity<>("Registered. Please verify email to login.", HttpStatus.OK);
    }

    public ResponseEntity<String> get_api_logout(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("Already logged out.", HttpStatus.BAD_REQUEST); }

        session.invalidate();
        return new ResponseEntity<>("Logged out: " + sessionAccount.getUserName(), HttpStatus.OK);
    }

    public ResponseEntity<List<DTO_Get_Post>> get_api_accounts_id_posts(@PathVariable(name = "id") Long id, HttpSession session) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        Account sessionAccount = (Account) session.getAttribute("account");
        return new ResponseEntity<>(service_post.getPostsForUser(repository_post.findAllByAccount(account), sessionAccount), HttpStatus.OK);
    }

    public ResponseEntity<List<DTO_Get_Like>> get_api_accounts_id_likes(@PathVariable(name = "id") Long id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        // return account likes
        Account account = optional_account.get();
        List<Like> likes = repository_likes.findAllByAccount(account);
        List<DTO_Get_Like> dtos = new ArrayList<>();
        for (Like like : likes) { dtos.add(new DTO_Get_Like(like)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
