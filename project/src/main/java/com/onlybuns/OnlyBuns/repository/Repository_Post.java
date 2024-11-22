package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface Repository_Post extends JpaRepository<Post, Long> {
    List<Post> findAll();
    List<Post> findAll(Sort sort);
    //List<Post> findByParentPostIsNull();
    Page<Post> findByParentPostIsNull(Pageable pageable);
    //List<Post> findAllByAccount(Account account);
    Page<Post> findAllByAccount(Account account, Pageable pageable);







    // Count posts after a certain date
    Long countByCreatedDateAfter(LocalDateTime date);

    // Find top 5 most liked posts from last week
    @Query("SELECT p FROM Post p " +
            "WHERE p.createdDate > :weekAgo " +
            "GROUP BY p.id " +
            "ORDER BY SIZE(p.likes) DESC " +
            "LIMIT 5")
    List<Post> findTop5ByCreatedDateAfterOrderByLikesSizeDesc(@Param("weekAgo") LocalDateTime weekAgo);

    // Find top 10 most liked posts of all time
    @Query("SELECT p FROM Post p " +
            "GROUP BY p.id " +
            "ORDER BY SIZE(p.likes) DESC " +
            "LIMIT 10")
    List<Post> findTop10ByOrderByLikesSizeDesc();
}
