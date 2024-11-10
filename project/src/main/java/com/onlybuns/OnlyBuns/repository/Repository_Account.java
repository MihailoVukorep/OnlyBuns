package com.onlybuns.OnlyBuns.repository;
import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
