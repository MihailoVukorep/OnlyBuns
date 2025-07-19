package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Repository_Post extends JpaRepository<Post, Long> {
    List<Post> findAll();
    List<Post> findAll(Sort sort);
    //List<Post> findByParentPostIsNull();
    Page<Post> findByParentPostIsNull(Pageable pageable);
    //List<Post> findAllByAccount(Account account);
    Page<Post> findAllByAccount(Account account, Pageable pageable);


    @Query("SELECT p FROM Post p WHERE p.account IN (:followedAccounts)")
    Page<Post> findPostsByFollowedAccounts(@Param("followedAccounts") Set<Account> followedAccounts, Pageable pageable);


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

    @Transactional
    @Modifying
    @Query("DELETE FROM Post p WHERE p.account.id = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COUNT(*) FROM Post p " +
            "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
            "AND p.parentPost IS NULL")
    int getNumberOfPosts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(*) FROM Post p " +
            "WHERE p.createdDate BETWEEN :startDate AND :endDate " +
            "AND p.parentPost IS NOT NULL")
    int getNumberOfComments(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT p.account) AS accounts_with_posts FROM Post p " +
            "WHERE p.parentPost IS NULL")
    int getNumberOfUsersPosted();

    @Query("SELECT COUNT(*) AS accounts_with_only_replies FROM Account a " +
            "WHERE EXISTS " +
            " (SELECT 1 FROM Post p WHERE p.account.id = a.id AND p.parentPost IS NOT NULL) " +
            "AND NOT EXISTS " +
            "(SELECT 1 FROM Post p WHERE p.account.id = a.id AND p.parentPost IS NULL)")
    int getNumberOfUsersCommented();

    @Query("SELECT COUNT(*) AS account_without_posts FROM Account a " +
            "LEFT JOIN Post p ON a.id = p.account.id " +
            "WHERE p.id IS NULL")
    int getNumberOfNoActivityUsers();
}
