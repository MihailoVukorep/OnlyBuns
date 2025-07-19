package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.*;
import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.*;
import com.onlybuns.OnlyBuns.util.ActiveUserMetrics;
import com.onlybuns.OnlyBuns.util.RateLimiter;
import com.onlybuns.OnlyBuns.util.VarConverter;
import com.onlybuns.OnlyBuns.util.FollowRateLimiter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
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

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

        simulateFakeLogins();
    }

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:00");

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Role repository_role;

    @Autowired
    private Repository_AccountActivation repository_accountActivation;

    @Autowired
    private ActiveUserMetrics activeUserMetrics;

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

    @Autowired
    private MeterRegistry meterRegistry;

    private final Map<Long, RateLimiter> userRateLimiters = new ConcurrentHashMap<>();

    @Autowired
    private FollowRateLimiter followRateLimiter;

    public Optional<Account> findByUsername(String username) {
        return repository_account.findByUserName(username);
    }

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

//        meterRegistry.counter("user_login_total", "user", dto_post_accountLogin.email).increment();

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

        if (!followRateLimiter.canFollow(user.getId())) {
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
    public synchronized void followTransactional(Account follower, Account followee) {
        long startTime = System.currentTimeMillis();

        System.out.printf("[%d] %s starting follow at %d%n",
                Thread.currentThread().getId(),
                follower.getUserName(),
                startTime);

        if (!repository_follow.existsByFollowerAndFollowee(follower, followee.getId())) {
            Follow follow = new Follow(follower, followee, LocalDateTime.now());

            repository_follow.save(follow);

            System.out.printf("[%d] %s completed follow at %d (took %dms)%n",
                    Thread.currentThread().getId(),
                    follower.getUserName(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis() - startTime);
            int currentCount = repository_follow.countByFollowee(followee);
            followee.setFollowerCount(currentCount);
            repository_account.save(followee);
        }
    }
    private final Map<Long, Deque<LocalDateTime>> followRequests = new ConcurrentHashMap<>();

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

    public List<Account> findNonActiveAccounts(LocalDateTime thresholdDate) {
        List<Account> all_acc = repository_account.findAll();
        List<Account> unactive_acc = new ArrayList<>();
        for(Account account : all_acc){
            if(account.getLastActivityDate() == null || account.getLastActivityDate().isBefore(thresholdDate)){
                    unactive_acc.add(account);
            }
        }

        return unactive_acc;

    }

    public List<Account> findUnactivatedAccounts(LocalDateTime thresholdDate) {
        List<Account> all_acc = repository_account.findAll();
        List<Account> unactive_acc = new ArrayList<>();

        for (Account account : all_acc) {

            if (account.getCreatedDate().isAfter(thresholdDate)) {
                continue;
            }

            Optional<AccountActivation> activation = repository_accountActivation.findByAccount(account);

            if (activation.isEmpty() ||
                    activation.get().getStatus() != AccountActivationStatus.APPROVED) {
                unactive_acc.add(account);
            }
        }
        return unactive_acc;
    }

    public void simulateFakeLogins() {
        List<Account> all = repository_account.findAll();
        if (all.size() < 20) {
            throw new RuntimeException("Nema dovoljno korisnika u bazi. Potrebno je najmanje 20.");
        }

        Map<Integer, Integer> loginSchedule = new HashMap<>();
        loginSchedule.put(9, 4);
        loginSchedule.put(11, 2);
        loginSchedule.put(12, 3);
        loginSchedule.put(15, 4);
        loginSchedule.put(16, 2);
        loginSchedule.put(18, 5);

        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        int userIndex = 0;

        for (Map.Entry<Integer, Integer> entry : loginSchedule.entrySet()) {
            int hour = entry.getKey();
            int count = entry.getValue();
            LocalDateTime fakeLoginTime = now.withHour(hour);

            for (int i = 0; i < count; i++) {
                Account acc = all.get(userIndex++);
                acc.setLastActivityDate(fakeLoginTime);
                repository_account.save(acc);

                meterRegistry.counter("user_login_total", "user", acc.getEmail()).increment();
            }
        }
        System.out.println("FAKE LOGIN DONE");
    }

    public Map<String, Long> getActiveUsersLast24hPerHour() {
        LocalDateTime from = LocalDateTime.now().minusHours(24);
        List<Object[]> results = repository_account.countActiveUsersByHour(from);

        Map<String, Long> countsByHour = new LinkedHashMap<>();
        for (int i = 0; i <= 24; i++) {
            LocalDateTime hour = LocalDateTime.now().minusHours(24 - i).truncatedTo(ChronoUnit.HOURS);
            String formattedHour = hour.format(HOUR_FORMATTER);
            countsByHour.put(formattedHour, 0L);
        }

        for (Object[] row : results) {
            LocalDateTime hour = ((Timestamp) row[0]).toLocalDateTime().truncatedTo(ChronoUnit.HOURS);
            String formattedHour = hour.format(HOUR_FORMATTER);
            Long count = ((Number) row[1]).longValue();
            countsByHour.put(formattedHour, count);
        }

        activeUserMetrics.updateHourlyCounts(countsByHour);
        return countsByHour;
    }

    // vracam koga prati prosledjeni account
    public List<Account> getFollowees(Account follower) {
        return new ArrayList<>(repository_follow.findFolloweesByFollower(follower));
    }

    // vracam followere prosledjenog accounta
    public List<Account> getFollowers(Account followee) {
        return new ArrayList<>(repository_follow.findFollowersByFollowee(followee));
    }
    // TODO: delete account cron job after some time
}
