package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Chat;
import com.onlybuns.OnlyBuns.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Repository_ChatMember extends JpaRepository<ChatMember, Long> {

    boolean existsByToken(String token);

    Optional<ChatMember> findByToken(String token);

    Optional<ChatMember> findByChatAndAccountId(Chat chat, Long accountId);
}