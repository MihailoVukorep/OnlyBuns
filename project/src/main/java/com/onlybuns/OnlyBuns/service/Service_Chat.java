package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Chat;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Chat;
import com.onlybuns.OnlyBuns.model.Message;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Chat;
import com.onlybuns.OnlyBuns.repository.Repository_Message;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Service_Chat {

    @Autowired
    private Repository_Chat repository_chat;

    @Autowired
    private Repository_Message repository_message;

    @Autowired
    private Repository_Account repository_account;

    // create chat
    public ResponseEntity<String> get_api_accounts_id_chat(HttpSession session, Long id) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Can't find account.", HttpStatus.NOT_FOUND); }

        Chat chat = new Chat(user, optional_account.get());
        repository_chat.save(chat);
        return new ResponseEntity<>("Chat created.", HttpStatus.NOT_FOUND);
    }

    // get current user's chats
    public ResponseEntity<List<DTO_Get_Chat>> get_api_chats(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }
        List<Chat> chats = repository_chat.findByMembersContains(user);
        List<DTO_Get_Chat> dto_chats = chats.stream().map(DTO_Get_Chat::new).toList();
        return new ResponseEntity<>(dto_chats, HttpStatus.OK);
    }

    // send message to chats
    public ResponseEntity<String> post_api_chats_id_message(HttpSession session, Long id, String text) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        Optional<Chat> optional_chat = repository_chat.findById(id);
        if (optional_chat.isEmpty()) { return new ResponseEntity<>("Can't find chat.", HttpStatus.NOT_FOUND); }

        Chat chat = optional_chat.get();
        if (!chat.getMembers().contains(user)) {  return new ResponseEntity<>("Not your chat.", HttpStatus.FORBIDDEN); }

        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Can't find account.", HttpStatus.NOT_FOUND); }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(user);
        message.setContent(text);
        repository_message.save(message);
        return new ResponseEntity<>("Message sent.", HttpStatus.OK);
    }
}
