package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.model.Trend;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import com.onlybuns.OnlyBuns.repository.Repository_Trend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class Service_Trend {

    @Autowired
    private Repository_Trend repository_trend;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_Account repository_account;

    @Cacheable("trends")
    public Trend getCurrentTrends() {
        return repository_trend.findFirstByOrderByLastUpdatedDesc();
    }

    @Scheduled(fixedRate = 3600000) // Update every hour
    //@Scheduled(fixedRate = 5000) // Update every 5 sec
    @Transactional
    public void updateTrends() {

        Trend currentTrend = getCurrentTrends();
        if (currentTrend != null) { repository_trend.delete(currentTrend); } // remove last trend -- this needs fixing unique constraint

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

        Trend trend = new Trend(totalPosts, postsLastMonth, topWeeklyPosts, topAllTimePosts, mostActiveLikers);

        try {
            repository_trend.save(trend);
            System.out.println("NEW TREND: " + trend);
        }
        catch (Exception e) { }

    }
}