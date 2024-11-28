package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.*;
import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.*;
import com.onlybuns.OnlyBuns.util.RateLimiter;
import com.onlybuns.OnlyBuns.util.VarConverter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.onlybuns.OnlyBuns.util.SimpleBloomFilter;

import java.util.*;

@Service
@Transactional // TODO: change this to only be on top of the functions that actually need it (create things / have lazy fetching)
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

    public Account eager(Long accountId) {
        Account account = repository_account.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Hibernate.initialize(account.getPosts());
        Hibernate.initialize(account.getLikes());
        Hibernate.initialize(account.getFollowers());
        Hibernate.initialize(account.getFollowing());
        Hibernate.initialize(account.getRoles());

        return account;
    }

    public Account lazy(Long accountId) {
        Account account = repository_account.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return account;
    }

    private final RateLimiter rateLimiter = new RateLimiter();
    private final VarConverter varConverter = new VarConverter();

    // /accounts/{id}
    public ResponseEntity<DTO_Get_Account> get_api_accounts_id(Long id) {
        Optional<Account> foundAccount = repository_account.findById(id);
        if (foundAccount.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new DTO_Get_Account(foundAccount.get()), HttpStatus.OK);
    }

    // /user
    public ResponseEntity<DTO_Get_Account> get_api_user(HttpSession session) {
        Account account = (Account) session.getAttribute("user");
        if (account == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        Optional<Account> optional_account = repository_account.findById(account.getId());
        if (optional_account.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new DTO_Get_Account(optional_account.get()), HttpStatus.OK);
    }

    // login / register / logout
    public ResponseEntity<String> post_api_login(DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session) {

        // already logged in
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount != null) {
            return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST);
        }

        // validate input
        String message = dto_post_accountLogin.validate();
        if (message != null) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        // Rate limiter check
        String clientIp = request.getRemoteAddr(); // Get the client's IP address
        if (rateLimiter.isRateLimited(clientIp)) {
            return new ResponseEntity<>("Too many login attempts. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
        }

        // find account
        Optional<Account> opt_account = repository_account.findByEmail(dto_post_accountLogin.getEmail());
        if (opt_account.isEmpty()) {
            opt_account = repository_account.findByUserName(dto_post_accountLogin.getEmail());
        }
        if (opt_account.isEmpty()) {
            return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND);
        }

        // account
        Account account = opt_account.get();

        // check if account is activated
        Optional<AccountActivation> opt_accountActivation = repository_accountActivation.findByAccount(account);
        if (opt_accountActivation.isEmpty()) {
            // missing account activation in db -- creating...
            try { service_email.sendVerificationEmail(account); }
            catch (Exception e) { new ResponseEntity<>("mail error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }

            return new ResponseEntity<>("Please verify email.", HttpStatus.UNAUTHORIZED);
        } else {
            AccountActivation accountActivation = opt_accountActivation.get();
            if (accountActivation.getStatus() == AccountActivationStatus.WAITING) {
                return new ResponseEntity<>("Please verify email.", HttpStatus.UNAUTHORIZED);
            }
        }

        if (!account.isPassword(dto_post_accountLogin.password)) {
            return new ResponseEntity<>("Wrong password.", HttpStatus.UNAUTHORIZED);
        }

        session.setAttribute("user", account);
        return new ResponseEntity<>("Logged in as: " + account.getUserName(), HttpStatus.OK);
    }
    public ResponseEntity<String> post_api_register(DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount != null) {
            return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST);
        }

        String message = dto_post_accountRegister.validate();
        if (message != null) {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        // Lock to prevent concurrent issues
        synchronized (this) {

            // Check if the email is already in the Bloom filter
            if (bloomFilter_email.mightContain(dto_post_accountRegister.getEmail())) {
                // If it might be in the Bloom filter, check the database to confirm
                Optional<Account> foundAccount = repository_account.findByEmail(dto_post_accountRegister.getEmail());
                if (foundAccount.isPresent()) {
                    return new ResponseEntity<>("Email exists: " + dto_post_accountRegister.getEmail(), HttpStatus.CONFLICT);
                }
            } else {
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
            } else {
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
        try { service_email.sendVerificationEmail(newAccount); }
        catch (Exception e) { new ResponseEntity<>("mail error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }

        // session.setAttribute("user", newAccount); // can't login need to verify
        return new ResponseEntity<>("Registered. Please verify email to login.", HttpStatus.OK);
    }
    public ResponseEntity<String> post_api_logout(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Already logged out.", HttpStatus.BAD_REQUEST);
        }

        session.invalidate();
        return new ResponseEntity<>("Logged out: " + sessionAccount.getUserName(), HttpStatus.OK);
    }

    // /accounts/{id}/likes
    public ResponseEntity<List<DTO_Get_Like>> get_api_accounts_id_likes(Long id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // return account likes
        Account account = optional_account.get();
        List<Like> likes = repository_likes.findAllByAccount(account);
        List<DTO_Get_Like> dtos = new ArrayList<>();
        for (Like like : likes) {
            dtos.add(new DTO_Get_Like(like));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    public Repository_Account getRepository_account() {
        return repository_account;
    }

    public void setRepository_account(Repository_Account repository_account) {
        this.repository_account = repository_account;
    }

    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_following(Long id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        List<DTO_Get_Account> accounts = new ArrayList<>();
        for (Account i : account.getFollowing()) { accounts.add(new DTO_Get_Account(i)); }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // follow / unfollow
    public ResponseEntity<String> post_api_accounts_id_follow(HttpSession session, Long id) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND); }

        long followerId = user.getId();
        long followeeId = id;

        Account follower = eager(followerId);
        Account followee = eager(followeeId);

        //unfollow
        if (follower.getFollowing().contains(followee)) {
            follower.unfollow(followee);
            repository_account.save(follower);
            repository_account.save(followee);
            return new ResponseEntity<>("Unfollowed.", HttpStatus.OK);
        }

        if (follower.equals(followee)) {
            throw new IllegalArgumentException("Account cannot follow itself.");
        }

        follower.follow(followee);
        repository_account.save(follower);
        repository_account.save(followee);

        return new ResponseEntity<>("Followed.", HttpStatus.OK);
    }

    // account's followers / following
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_followers(Long id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        List<DTO_Get_Account> accounts = new ArrayList<>();
        for (Account i : account.getFollowers()) { accounts.add(new DTO_Get_Account(i)); }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // TODO: delete account cron job after some time
}
