package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Chat;
import com.onlybuns.OnlyBuns.model.Message;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Chat;
import com.onlybuns.OnlyBuns.repository.Repository_Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class Service_Chat {
    @Autowired
    private Repository_Chat repository_chat;

    @Autowired
    private Repository_Message repository_message;

    @Autowired
    private Repository_Account repository_account;

    public Chat createChat(List<Account> participants) {
        Chat chat = new Chat();
        chat.setParticipants(participants);
        chat.setCreatedDate(LocalDateTime.now());
        return repository_chat.save(chat);
    }

    public Message sendMessage(Long chatId, Long senderId, String content) {
        Chat chat = repository_chat.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        Account sender = repository_account.findById(senderId).orElseThrow(() -> new RuntimeException("Account not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setSentDate(LocalDateTime.now());

        return repository_message.save(message);
    }

}
