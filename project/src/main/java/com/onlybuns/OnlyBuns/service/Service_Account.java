package com.onlybuns.OnlyBuns.service;
import com.onlybuns.OnlyBuns.dto.*;
import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.*;
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
    private Service_Email serviceEmail;

    public ResponseEntity<List<DTO_View_Account>> api_accounts() {
        List<Account> accounts = repository_account.findAll();
        List<DTO_View_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) { accountDTOS.add(new DTO_View_Account(account)); }
        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }

    public ResponseEntity<List<DTO_Admin_View_Account>> api_adminaccounts() {
        List<Account> accounts = repository_account.findAll();
        List<DTO_Admin_View_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) { accountDTOS.add(new DTO_Admin_View_Account(account)); }
        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Account> api_accounts_id(@PathVariable(name = "id") Integer id) {
        Optional<Account> foundAccount = repository_account.findById(id);
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_View_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Account> api_myaccount(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<Account> foundAccount = repository_account.findById(sessionAccount.getId());
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

        // already logged in
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST); }

        // validate input
        String message = dto_post_accountLogin.validate();
        if (message != null) { return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST); }

        // Rate limiter check
        String clientIp = request.getRemoteAddr(); // Get the client's IP address
        if (isRateLimited(clientIp)) {
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
            serviceEmail.sendVerificationEmail(account);
            return new ResponseEntity<>("Please verify email.", HttpStatus.UNAUTHORIZED);
        }
        else {
            AccountActivation accountActivation = opt_accountActivation.get();
            if (accountActivation.getStatus() == AccountActivationStatus.WAITING) {
                return new ResponseEntity<>("Please verify email.", HttpStatus.UNAUTHORIZED);
            }
        }

        // check if password matches
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

        Optional<Role> role = repository_role.findByName("USER");
        if (role.isEmpty()) {

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
                "..."
        );
        repository_account.save(newAccount);

        // send verification email
        serviceEmail.sendVerificationEmail(newAccount);

        // session.setAttribute("account", newAccount); // can't login need to verify
        return new ResponseEntity<>("Registered. Please verify email to login.", HttpStatus.OK);
    }

    public ResponseEntity<String> logout(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("Already logged out.", HttpStatus.BAD_REQUEST); }

        session.invalidate();
        return new ResponseEntity<>("Logged out: " + sessionAccount.getUserName(), HttpStatus.OK);
    }

    public ResponseEntity<List<DTO_View_Post>> api_accounts_id_posts(@PathVariable(name = "id") Integer id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        // return account posts
        Account account = optional_account.get();
        List<Post> posts = repository_post.findAllByAccount(account);
        List<DTO_View_Post> dtos = new ArrayList<>();
        for (Post post : posts) { dtos.add(new DTO_View_Post(post)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    public ResponseEntity<List<DTO_View_Like>> api_accounts_id_likes(@PathVariable(name = "id") Integer id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        // return account likes
        Account account = optional_account.get();
        List<Like> likes = repository_likes.findAllByAccount(account);
        List<DTO_View_Like> dtos = new ArrayList<>();
        for (Like like : likes) { dtos.add(new DTO_View_Like(like)); }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
