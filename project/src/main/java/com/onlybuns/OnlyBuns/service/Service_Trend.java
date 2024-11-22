package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Post;
import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class Service_Trend {

    @Autowired
    private Repository_Trend repository_trend;

    @Autowired
    private Repository_TrendingWeeklyPost repository_trendingWeeklyPost;

    @Autowired
    private Repository_TrendingActiveUser repository_trendingActiveUser;

    @Autowired
    private Repository_TrendingAllTimePost repository_trendingAllTimePost;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Service_Post service_post;

    @Cacheable("trends")
    public Trend getCurrentTrend() {
        return repository_trend.findFirstByOrderByLastUpdatedDesc();
    }

    public List<DTO_Get_Post> getWeekly(HttpSession session) {
        Trend currentTrend = getCurrentTrend();
        Account account = (Account) session.getAttribute("user");
        List<Post> weekly = currentTrend.getTopWeeklyPosts().stream().map(TrendingWeeklyPost::getPost).toList();
        return service_post.getPostsForUser(weekly, account);
    }

    public List<DTO_Get_Post> getTop(HttpSession session) {
        Trend currentTrend = getCurrentTrend();
        Account account = (Account) session.getAttribute("user");
        List<Post> top = currentTrend.getTopAllTimePosts().stream().map(TrendingAllTimePost::getPost).toList();
        return service_post.getPostsForUser(top, account);
    }

    public List<DTO_Get_Account> getLikers(HttpSession session) {
        Trend currentTrend = getCurrentTrend();
        Account account = (Account) session.getAttribute("user");
        return currentTrend.getMostActiveLikers().stream().map(i -> { return new DTO_Get_Account(i.getAccount()); }).toList();
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

        Trend trend = new Trend(totalPosts, postsLastMonth);
        repository_trend.save(trend);

        List<TrendingWeeklyPost> trendingWeeklyPosts = topWeeklyPosts.stream().map(post -> { return new TrendingWeeklyPost(trend.getId(), post); }).toList();
        repository_trendingWeeklyPost.saveAll(trendingWeeklyPosts);
        trend.setTopWeeklyPosts(trendingWeeklyPosts);

        List<TrendingAllTimePost> trendingAllTimePosts = topAllTimePosts.stream().map(post -> { return new TrendingAllTimePost(trend.getId(), post); }).toList();
        repository_trendingAllTimePost.saveAll(trendingAllTimePosts);
        trend.setTopAllTimePosts(trendingAllTimePosts);

        List<TrendingActiveUser> activeUsersEntities = mostActiveLikers.stream().map(account -> { return new TrendingActiveUser(trend.getId(), account); }).toList();
        repository_trendingActiveUser.saveAll(activeUsersEntities);
        trend.setMostActiveLikers(activeUsersEntities);

        System.out.println("NEW TREND: " + trend);
        System.out.println("=== NEW TREND WEEKLY POSTS");
        for (TrendingWeeklyPost i : trend.getTopWeeklyPosts()) {
            Post post = i.getPost();
            System.out.println(
                    "Post{" +
                    "id=" + post.getId() +
                    ", title='" + post.getTitle() + '\'' +
                    ", text='" + post.getText() + '\'' +
                    ", likes=" + post.getLikes().size() +
                    '}'
            );
        }
        System.out.println();
        System.out.println("=== NEW TREND ALL TIME POSTS");
        for (TrendingAllTimePost i : trend.getTopAllTimePosts()) {
            Post post = i.getPost();
            System.out.println(
                    "Post{" +
                            "id=" + post.getId() +
                            ", title='" + post.getTitle() + '\'' +
                            ", text='" + post.getText() + '\'' +
                            ", likes=" + post.getLikes().size() +
                            '}'
            );
        }
        System.out.println();
        System.out.println("=== NEW TREND ACCOUNTS");
        for (TrendingActiveUser i : trend.getMostActiveLikers()) {
            Account account = i.getAccount();
            System.out.println(
                    "Acccount{" +
                            "id=" + account.getId() +
                            ", username='" + account.getUserName() + '\'' +
                            ", likes=" + account.getLikes().size() +
                            '}'
            );
        }
        System.out.println();
    }
}