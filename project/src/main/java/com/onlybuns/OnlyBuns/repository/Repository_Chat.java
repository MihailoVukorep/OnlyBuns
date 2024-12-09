package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Repository_Chat extends JpaRepository<Chat, Long> {

    // Derived Query Method
    List<Chat> findByMembersContains(Account account);

//    // Alternative: Custom Query using JPQL
//    @Query("SELECT c FROM Chat c JOIN c.members m WHERE m = :account")
//    List<Chat> findAllByAccount(@Param("account") Account account);
}