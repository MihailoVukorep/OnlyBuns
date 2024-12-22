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
import jakarta.validation.constraints.Null;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.onlybuns.OnlyBuns.util.SimpleBloomFilter;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    private Repository_Follow repository_follow;

    @Autowired
    private Service_Email service_email;

    @Autowired
    private Service_Post service_post;

    public Account eager(Long accountId) {
        Account account = repository_account.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Hibernate.initialize(account.getPosts());
        Hibernate.initialize(account.getLikes());
        Hibernate.initialize(repository_follow.findFollowersByFollowee(account));
        Hibernate.initialize(repository_follow.findFolloweesByFollower(account));
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
    public ResponseEntity<DTO_Get_Account> get_api_accounts_id(HttpSession session, Long id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Account l_user = (Account) session.getAttribute("user"); // Lazy-loaded user
        boolean isMyAccount = false;
        boolean isFollowing = false;

        if (l_user != null) {
            Account user = eager(l_user.getId()); // Fetch eagerly loaded account
            isMyAccount = user.getId().equals(id);
            isFollowing = repository_follow.existsByFollowerAndFollowee(user, id);
        }

        DTO_Get_Account dto = new DTO_Get_Account(optional_account.get(), isMyAccount, isFollowing, repository_follow);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // /user
    public ResponseEntity<DTO_Get_Account> get_api_user(HttpSession session) {
        Account account = (Account) session.getAttribute("user");
        if (account == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return get_api_accounts_id(session, account.getId());
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
            try {
                service_email.sendVerificationEmail(account);
            } catch (Exception e) {
                new ResponseEntity<>("mail error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

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
        account.setLastActivityDate(LocalDateTime.now());
        repository_account.save(account);
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

        newAccount.setLastActivityDate(LocalDateTime.now());
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

    // follow / unfollow
    public ResponseEntity<String> post_api_accounts_id_follow(HttpSession session, Long id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED);
        }

        if (!canFollow(user.getId())) {
            return new ResponseEntity<>("Follow limit exceeded. Try again later.", HttpStatus.TOO_MANY_REQUESTS);
        }

        Account follower = repository_account.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Follower not found with ID: " + user.getId()));
        Account followee = repository_account.findById(id)
                .orElseThrow(() -> new RuntimeException("Followee not found with ID: " + id));

        if (follower.equals(followee)) {
            return new ResponseEntity<>("Account cannot follow itself.", HttpStatus.CONFLICT);
        }

        Optional<Follow> existingFollow = repository_follow.findByFollowerAndFollowee(follower, followee);

        if (existingFollow.isPresent()) {
            // Unfollow
            repository_follow.delete(existingFollow.get());
            return new ResponseEntity<>("Unfollowed.", HttpStatus.OK);
        } else {
            // Follow
            followTransactional(follower, followee);
            return new ResponseEntity<>("Followed.", HttpStatus.OK);
        }
    }

    @Transactional
    public void followTransactional(Account follower, Account followee) {
        Follow follow = new Follow(follower, followee, LocalDateTime.now());
        repository_follow.save(follow);
        repository_account.save(followee);
    }

    private final Map<Long, Deque<LocalDateTime>> followRequests = new ConcurrentHashMap<>();

    public synchronized boolean canFollow(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        followRequests.putIfAbsent(userId, new LinkedList<>());
        Deque<LocalDateTime> requests = followRequests.get(userId);

        //ne racunamo zahteve starije od minut
        while (!requests.isEmpty() && requests.peekFirst().isBefore(now.minusMinutes(1))) {
            requests.pollFirst();
        }

        if (requests.size() >= 50) {
            return false;
        }

        requests.addLast(now);
        return true;
    }

//    public ResponseEntity<DTO_Get_Account> get_api_accounts_id(HttpSession session, Long id) {
//        Optional<Account> optional_account = repository_account.findById(id);
//        if (optional_account.isEmpty()) {
//            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//        }
//
//        Account account = optional_account.get();
//
//        // Koristite repozitorijum za brojanje
//        int followersCount = repository_follow.countFollowersByAccountId(id);
//        int followingCount = repository_follow.findFollowersByFollowee(account).size();
//
//        // Proverite da li je u pitanju moj nalog i da li korisnik prati taj nalog
//        Account sessionUser = (Account) session.getAttribute("user");
//        boolean isMyAccount = sessionUser != null && sessionUser.getId().equals(id);
//        boolean isFollowing = sessionUser != null &&
//                repository_follow.findByFollowerAndFollowee(sessionUser, account).isPresent();
//
//        // Napravite DTO koristeći sve podatke
//        DTO_Get_Account dto = new DTO_Get_Account(
//                account,
//                account.getPosts().size(),
//                account.getLikes().size(),
//                followersCount,
//                followingCount,
//                isMyAccount,
//                isFollowing
//        );
//
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }

    // account's followers / following
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_followers(Long id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Account account = optional_account.get();
        List<DTO_Get_Account> accounts = repository_follow.findFollowersByFollowee(account)
                .stream()
                .map(follower -> new DTO_Get_Account(follower, repository_follow))
                .collect(Collectors.toList());

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Fetch users a user is following
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_following(Long id) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Account account = optional_account.get();
        List<DTO_Get_Account> accounts = repository_follow.findFolloweesByFollower(account)
                .stream()
                .map(following -> new DTO_Get_Account(following, repository_follow))
                .collect(Collectors.toList());

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    public List<Account> findUnactivatedAccounts(LocalDateTime thresholdDate) {
        List<Account> all_acc = repository_account.findAll();
        List<Account> unactive_acc = new ArrayList<>();
        for(Account account : all_acc){
            if(account.getLastActivityDate() == null || account.getLastActivityDate().isBefore(thresholdDate)){
                    unactive_acc.add(account);
            }
        }

        return unactive_acc;

    }
    // TODO: delete account cron job after some time
}
