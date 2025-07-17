package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface Repository_Account extends JpaRepository<Account, Long> {
    List<Account> findAll();

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUserName(String userName);

    @Query("SELECT a FROM Account a WHERE " +
            "(COALESCE(LOWER(a.firstName), '') LIKE LOWER(CONCAT('%', COALESCE(:firstName, ''), '%'))) AND " +
            "(COALESCE(LOWER(a.lastName), '') LIKE LOWER(CONCAT('%', COALESCE(:lastName, ''), '%'))) AND " +
            "(COALESCE(LOWER(a.userName), '') LIKE LOWER(CONCAT('%', COALESCE(:userName, ''), '%'))) AND " +
            "(COALESCE(LOWER(a.email), '') LIKE LOWER(CONCAT('%', COALESCE(:email, ''), '%'))) AND " +
            "(COALESCE(LOWER(a.address), '') LIKE LOWER(CONCAT('%', COALESCE(:address, ''), '%'))) AND " +
            "((:minPostCount IS NULL OR SIZE(a.posts) >= :minPostCount) AND " +
            "(:maxPostCount IS NULL OR SIZE(a.posts) <= :maxPostCount))")
    List<Account> findAccountsByAttributesLike(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("userName") String userName,
            @Param("email") String email,
            @Param("address") String address,
            @Param("minPostCount") Integer minPostCount,
            @Param("maxPostCount") Integer maxPostCount
    );

    @Query(value = "SELECT a.* FROM accounts a " +
            "JOIN likes l ON a.id = l.account_id " +
            "WHERE l.created_date >= :startDate " +
            "GROUP BY a.id " +
            "ORDER BY COUNT(l.id) DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Account> findTopAccountsByLikes(@Param("startDate") LocalDateTime startDate, @Param("limit") int limit);

    @Query("SELECT a FROM Account a WHERE a.lastActivityDate < :thresholdDate")
    List<Account> findInactiveAccounts(@Param("thresholdDate") LocalDateTime thresholdDate);

//    @Query("SELECT COUNT(f) FROM Account a JOIN a.followers f WHERE a.id = :accountId AND f.lastActivityDate > :lastActivityDate")
//    long countNewFollowers(@Param("accountId") Long accountId, @Param("lastActivityDate")LocalDateTime lastActivityDate);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.account.id = :accountId AND l.createdDate > :lastActivityDate")
    long countLikesOnUsersPosts(@Param("accountId") Long accountId, @Param("lastActivityDate") LocalDateTime lastActivityDate);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.account.id != :accountId AND p.createdDate > :lastActivityDate")
    long countOtherUsersPosts(@Param("accountId") Long accountId, @Param("lastActivityDate") LocalDateTime lastActivityDate);


    @Query("SELECT a FROM Account a WHERE " +
            "(COALESCE(LOWER(a.firstName), '') LIKE LOWER(CONCAT('%', COALESCE(:firstName, ''), '%'))) AND " +
            "(COALESCE(LOWER(a.lastName), '') LIKE LOWER(CONCAT('%', COALESCE(:lastName, ''), '%'))) AND " +
            "(COALESCE(LOWER(a.userName), '') LIKE LOWER(CONCAT('%', COALESCE(:userName, ''), '%'))) AND " +
            "(COALESCE(LOWER(a.email), '') LIKE LOWER(CONCAT('%', COALESCE(:email, ''), '%'))) AND " +
            "(COALESCE(LOWER(a.address), '') LIKE LOWER(CONCAT('%', COALESCE(:address, ''), '%'))) AND " +
            "((:minPostCount IS NULL OR SIZE(a.posts) >= :minPostCount) AND " +
            "(:maxPostCount IS NULL OR SIZE(a.posts) <= :maxPostCount))")
    Page<Account> findAllAccountsByAttributesLike(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("userName") String userName,
            @Param("email") String email,
            @Param("address") String address,
            @Param("minPostCount") Integer minPostCount,
            @Param("maxPostCount") Integer maxPostCount,
            Pageable pageable);

    @Query(value = """
    SELECT date_trunc('hour', last_activity_date) AS hour, COUNT(*) 
    FROM accounts 
    WHERE last_activity_date >= :from 
    GROUP BY hour 
    ORDER BY hour
    """, nativeQuery = true)
    List<Object[]> countActiveUsersByHour(@Param("from") LocalDateTime from);

    @Query("SELECT a FROM Account a WHERE " +
            "(COALESCE(:firstName, '') = '' OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(COALESCE(:lastName, '') = '' OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(COALESCE(:userName, '') = '' OR LOWER(a.userName) LIKE LOWER(CONCAT('%', :userName, '%'))) AND " +
            "(COALESCE(:email, '') = '' OR LOWER(a.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(COALESCE(:address, '') = '' OR LOWER(a.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND " +
            "((:minPostCount IS NULL OR SIZE(a.posts) >= :minPostCount) AND " +
            "(:maxPostCount IS NULL OR SIZE(a.posts) <= :maxPostCount))")
    List<Account> findAllAccountsByAttributesLike(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("userName") String userName,
            @Param("email") String email,
            @Param("address") String address,
            @Param("minPostCount") Integer minPostCount,
            @Param("maxPostCount") Integer maxPostCount);
}
