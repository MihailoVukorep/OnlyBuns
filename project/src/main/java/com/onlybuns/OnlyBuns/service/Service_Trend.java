package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class Service_Trend {

    @Autowired
    private Repository_Trend repository_trend;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Follow repository_follow;

    @Autowired
    private Service_Post service_post;

    @Cacheable("trends")
    public Trend getCurrentTrend() {
        return repository_trend.findFirstByOrderByLastUpdatedDesc();
    }

    private final String CSV_DELIMITER = ",";

    List<Post> csv2posts(String csv) {
        String[] ids = csv.split(CSV_DELIMITER);
        List<Post> posts = new ArrayList<>();
        for (String id : ids) {
            Optional<Post> post = repository_post.findById(Long.parseLong(id));
            if (post.isEmpty()) {   continue; }
            posts.add(post.get());
        }
        return posts;
    }
    List<Account> csv2accounts(String csv) {
        String[] ids = csv.split(CSV_DELIMITER);
        List<Account> accounts = new ArrayList<>();
        for (String id : ids) {
            Optional<Account> account = repository_account.findById(Long.parseLong(id));
            if (account.isEmpty()) {   continue; }
            accounts.add(account.get());
        }
        return accounts;
    }
    public ResponseEntity<List<DTO_Get_Post>> get_api_trends_weekly(HttpSession session) {
        Trend currentTrend = getCurrentTrend();
        Account account = (Account) session.getAttribute("user");
        if (account == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }
        return new ResponseEntity<>(service_post.getPostsForUser(csv2posts(currentTrend.getTopWeeklyPostsCsv()), account), HttpStatus.OK);
    }
    public ResponseEntity<List<DTO_Get_Post>> get_api_trends_top(HttpSession session) {
        Trend currentTrend = getCurrentTrend();
        Account account = (Account) session.getAttribute("user");
        if (account == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }
        return new ResponseEntity<>(service_post.getPostsForUser(csv2posts(currentTrend.getTopAllTimePosts()), account), HttpStatus.OK);
    }
    public ResponseEntity<List<DTO_Get_Account>> get_api_trends_likers(HttpSession session) {
        Trend currentTrend = getCurrentTrend();
        Account account = (Account) session.getAttribute("user");
        if (account == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }
        List<DTO_Get_Account> dtoAccounts = csv2accounts(currentTrend.getMostActiveLikers())
                .stream()
                .map(a -> new DTO_Get_Account(a, repository_follow))
                .collect(Collectors.toList());

        return new ResponseEntity<>(dtoAccounts, HttpStatus.OK);
    }

    @Scheduled(fixedRate = 3600000) // Update every hour
    //@Scheduled(fixedRate = 5000) // Update every 5 sec
    @Transactional
    public void updateTrends() {

        // delete last trend ; used for debug ; remove this so we have history
        Trend currentTrend = getCurrentTrend();
        if (currentTrend != null) { repository_trend.delete(currentTrend); }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusMonths(1);
        LocalDateTime weekAgo = now.minusDays(7);

        // Get total posts and posts in last month
        Long totalPosts = repository_post.count();
        Long postsLastMonth = repository_post.countByCreatedDateAfter(monthAgo);

        // Get top 5 posts from last 7 days
        List<Post> topWeeklyPosts = repository_post.findTop5ByCreatedDateAfterOrderByLikesSizeDesc(weekAgo);

        // Get top 10 posts of all time
        List<Post> topAllTimePosts = repository_post.findTop10ByOrderByLikesSizeDesc();

        // Get top 10 users who gave most likes in last 7 days
        List<Account> mostActiveLikers = repository_account.findTopAccountsByLikes(weekAgo, 10);


        String weekly = String.join(CSV_DELIMITER, topWeeklyPosts.stream().map(i -> i.getId().toString()).toList());
        String top = String.join(CSV_DELIMITER, topAllTimePosts.stream().map(i -> i.getId().toString()).toList());
        String likers = String.join(CSV_DELIMITER, mostActiveLikers.stream().map(i -> i.getId().toString()).toList());

        Trend trend = new Trend(totalPosts, postsLastMonth, weekly, top, likers);
        repository_trend.save(trend);


        System.out.println("NEW TREND: " + trend);
        System.out.println("=== NEW TREND WEEKLY POSTS");
        for (Post i : topWeeklyPosts) {
            System.out.println(
                    "Post{" +
                    "id=" + i.getId() +
                    ", title='" + i.getTitle() + '\'' +
                    ", text='" + i.getText() + '\'' +
                    ", likes=" + i.getLikes().size() +
                    '}'
            );
        }
        System.out.println();
        System.out.println("=== NEW TREND ALL TIME POSTS");
        for (Post i : topAllTimePosts) {
            System.out.println(
                    "Post{" +
                            "id=" + i.getId() +
                            ", title='" + i.getTitle() + '\'' +
                            ", text='" + i.getText() + '\'' +
                            ", likes=" + i.getLikes().size() +
                            '}'
            );
        }
        System.out.println();
        System.out.println("=== NEW TREND ACCOUNTS");
        for (Account i : mostActiveLikers) {
            System.out.println(
                    "Acccount{" +
                            "id=" + i.getId() +
                            ", username='" + i.getUserName() + '\'' +
                            ", likes=" + i.getLikes().size() +
                            '}'
            );
        }
        System.out.println();
    }
}