package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Follow;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface Repository_Follow extends JpaRepository<Follow, Long> {
    List<Follow> findAll();
    int countByFollowee(Account followee);
    void deleteByFollowee(Account followee);
    @Query("SELECT f.follower FROM Follow f WHERE f.followee = :followee")
    Set<Account> findFollowersByFollowee(@Param("followee") Account followee);
    //followee je acc cije followere dobavljam
    //nalazim pratioce accounta koji saljem

    @Query("SELECT f.followee FROM Follow f WHERE f.follower = :follower")
    Set<Account> findFolloweesByFollower(@Param("follower") Account follower);
    //nalazim koga account koji saljem prati
    @Query("SELECT f FROM Follow f WHERE f.follower = :follower AND f.followee = :followee")
    Optional<Follow> findByFollowerAndFollowee(@Param("follower") Account follower, @Param("followee") Account followee);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Follow f WHERE f.follower = :follower AND f.followee.id = :followeeId")
    boolean existsByFollowerAndFollowee(@Param("follower") Account follower, @Param("followeeId") Long followeeId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followee = :account")
    Integer countFollowers(@Param("account") Account account);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower = :account")
    Integer countFollowees(@Param("account") Account account);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followee.id = :accountId AND f.createdDate > :sinceDate")
    Long countNewFollowers(@Param("accountId") Long accountId, @Param("sinceDate") LocalDateTime sinceDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM Follow f WHERE f.followee.id = :accountId OR f.follower.id = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);
}
