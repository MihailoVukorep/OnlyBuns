package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Chat;
import com.onlybuns.OnlyBuns.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface Repository_ChatMember extends JpaRepository<ChatMember, Long> {

    boolean existsByToken(String token);

    Optional<ChatMember> findByToken(String token);

    Optional<ChatMember> findByChatAndAccountId(Chat chat, Long accountId);

    @Modifying
    @Query("DELETE FROM ChatMember cm WHERE cm.account.id = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);
}