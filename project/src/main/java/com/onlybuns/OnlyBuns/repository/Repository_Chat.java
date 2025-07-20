package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Chat;
import com.onlybuns.OnlyBuns.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface Repository_Chat extends JpaRepository<Chat, Long> {

    boolean existsByToken(String token);

    Optional<Chat> findByToken(String token);

    @Query("SELECT c FROM Chat c JOIN c.members m WHERE m.account = :account")
    List<Chat> findByMembersContains(@Param("account") Account account);

    @Query("SELECT c FROM Chat c WHERE  c.name = :chatName")
    List<Chat> findDuplicateChat(@Param("chatName") String chatName);
}