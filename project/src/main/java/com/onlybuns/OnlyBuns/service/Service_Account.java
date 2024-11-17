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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    private final VarConverter varConverter = new VarConverter();

    @Transactional
    public ResponseEntity<List<DTO_Get_Account>> get_api_admin_accounts(HttpSession session, String firstName, String lastName, String userName, String email, String address, Integer minPostCount, Integer maxPostCount) {
        return new ResponseEntity<>(get_api_admin_accounts_raw(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount), HttpStatus.OK);
    }

    @Transactional
    public List<DTO_Get_Account> get_api_admin_accounts_raw(HttpSession session, String firstName, String lastName, String userName, String email, String address, Integer minPostCount, Integer maxPostCount) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return null;
        }

        List<Account> accounts = repository_account.findAccountsByAttributesLike(firstName, lastName, userName, email, address, minPostCount, maxPostCount);
        List<DTO_Get_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) {
            accountDTOS.add(new DTO_Get_Account(account));
        }
        return accountDTOS;
    }

    @Transactional
    public ResponseEntity<DTO_Get_Account> get_api_accounts_id(Long id) {
        Optional<Account> foundAccount = repository_account.findById(id);
        if (foundAccount.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new DTO_Get_Account(foundAccount.get()), HttpStatus.OK);
    }

    @Transactional
    public DTO_Get_Account get_api_accounts_id_raw(Long id) {
        Optional<Account> foundAccount = repository_account.findById(id);
        return foundAccount.isEmpty() ? null : new DTO_Get_Account(foundAccount.get());
    }

    @Transactional
    public ResponseEntity<DTO_Get_Account> get_api_user(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        Optional<Account> foundAccount = repository_account.findById(sessionAccount.getId());
        if (foundAccount.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new DTO_Get_Account(foundAccount.get()), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> get_api_login(DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session) {

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
            service_email.sendVerificationEmail(account);
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

    @Transactional
    public ResponseEntity<String> get_api_register(DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
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
        service_email.sendVerificationEmail(newAccount);

        // session.setAttribute("user", newAccount); // can't login need to verify
        return new ResponseEntity<>("Registered. Please verify email to login.", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> get_api_logout(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("user");
        if (sessionAccount == null) {
            return new ResponseEntity<>("Already logged out.", HttpStatus.BAD_REQUEST);
        }

        session.invalidate();
        return new ResponseEntity<>("Logged out: " + sessionAccount.getUserName(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<List<DTO_Get_Post>> get_api_accounts_id_posts(Long id, HttpSession session) {
        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Account account = optional_account.get();

        Account sessionAccount = (Account) session.getAttribute("user");
        return new ResponseEntity<>(service_post.getPostsForUser(repository_post.findAllByAccount(account), sessionAccount), HttpStatus.OK);
    }

    @Transactional
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

    @Transactional
    public List<Account> getSortedAccounts(HttpSession session, String sortOption) {
        Sort sort = switch (sortOption) {
            case "follow_count,asc" -> Sort.by(Sort.Direction.ASC, "followCount");
            case "follow_count,desc" -> Sort.by(Sort.Direction.DESC, "followCount");
            case "email,asc" -> Sort.by(Sort.Direction.ASC, "email");
            case "email,desc" -> Sort.by(Sort.Direction.DESC, "email");
            default -> Sort.unsorted();
        };

        return repository_account.findAll(sort);
    }

    @Transactional
    public List<DTO_Get_Account> getFilteredAndSortedAccounts(HttpSession session, String firstName, String lastName, String userName, String email, String address, Integer minPostCount, Integer maxPostCount, String sortOption) {
        List<DTO_Get_Account> accounts = get_api_admin_accounts_raw(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount);

        if (accounts == null || accounts.isEmpty()) {
            return accounts;
        }
        if (sortOption == null) {
            return accounts;
        }

        Comparator<DTO_Get_Account> comparator = null;

        switch (sortOption) {
            case "follow_count,asc":
//                comparator = Comparator.comparingInt(DTO_Get_Account::getFollowCount);
                comparator = Comparator.comparing(DTO_Get_Account::getEmail);
                break;
            case "follow_count,desc":
//                comparator = Comparator.comparingInt(DTO_Get_Account::getFollowCount).reversed();
                comparator = Comparator.comparing(DTO_Get_Account::getEmail);
                break;
            case "email,asc":
                comparator = Comparator.comparing(DTO_Get_Account::getEmail);
                break;
            case "email,desc":
                comparator = Comparator.comparing(DTO_Get_Account::getEmail).reversed();
                break;
            default:
                return accounts;
        }

        accounts.sort(comparator);

        return accounts;
    }

    @Transactional
    public Account findAccountById(Long id) {
        return repository_account.findById(id).orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
    }

    @Transactional
    public void followAccount(Long followerId, Long followeeId) {
        Account follower = findAccountById(followerId);
        Account followee = findAccountById(followeeId);

        if (follower.equals(followee)) {
            throw new IllegalArgumentException("Account cannot follow itself.");
        }

        follower.follow(followee);
        repository_account.save(follower);
        repository_account.save(followee);
    }

    @Transactional
    public void unfollowAccount(Long followerId, Long followeeId) {
        Account follower = findAccountById(followerId);
        Account followee = findAccountById(followeeId);

        follower.unfollow(followee);
        repository_account.save(follower);
        repository_account.save(followee);
    }

    @Transactional
    public Set<Account> getFollowers(Long accountId) {
        Account account = findAccountById(accountId);
        return account.getFollowers();
    }

    @Transactional
    public Set<Account> getFollowing(Long accountId) {
        Account account = findAccountById(accountId);
        return account.getFollowing();
    }

}
