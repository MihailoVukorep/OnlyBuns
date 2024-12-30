package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Chat;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Message;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Chat;
import com.onlybuns.OnlyBuns.model.Message;
import com.onlybuns.OnlyBuns.model.Message_Type;
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

    public Optional<Chat> findById(Long id) {
        return repository_chat.findById(id);
    }

    // create chat (user & account id)
    public ResponseEntity<String> get_api_accounts_id_chat(HttpSession session, Long id) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Can't find account.", HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        Chat chat = new Chat(user, account);
        repository_chat.save(chat);
        repository_message.save(new Message(chat, user, "", Message_Type.JOINED));
        repository_message.save(new Message(chat, account, "", Message_Type.JOINED));

        return new ResponseEntity<>("Chat created.", HttpStatus.NOT_FOUND);
    }

    // get current user's chats
    public ResponseEntity<List<DTO_Get_Chat>> get_api_chats(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }
        List<Chat> chats = repository_chat.findByMembersContains(user);
        List<DTO_Get_Chat> dto_chats = chats.stream().map(i -> new DTO_Get_Chat(i, user)).toList();
        return new ResponseEntity<>(dto_chats, HttpStatus.OK);
    }

    // get chat info
    public ResponseEntity<DTO_Get_Chat> get_api_chats_id(HttpSession session, Long id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }
        List<Chat> chats = repository_chat.findByMembersContains(user);
        for (Chat i : chats) {
            if (i.getId() == id) {
                return new ResponseEntity<>(new DTO_Get_Chat(i, user), HttpStatus.OK);
            }
        }
        return null;
    }

    // get chat's messages
    public ResponseEntity<List<DTO_Get_Message>> get_api_chats_id_messages(HttpSession session, Long id) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<Chat> optional_chat = repository_chat.findById(id);
        if (optional_chat.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        Chat chat = optional_chat.get();
        if (chat.getMembers().stream().noneMatch(member -> member.getId().equals(user.getId()))) { return new ResponseEntity<>(null, HttpStatus.FORBIDDEN); }

        List<DTO_Get_Message> messages = chat.getMessages().stream().map(DTO_Get_Message::new).toList();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // send message to chats
    public ResponseEntity<DTO_Get_Message> post_api_chats_id_messages(HttpSession session, Long id, String text) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); } // Not logged in.

        Optional<Chat> optional_chat = repository_chat.findById(id);
        if (optional_chat.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); } // Can't find chat.

        Chat chat = optional_chat.get();
        if (chat.getMembers().stream().noneMatch(member -> member.getId().equals(user.getId()))) { return new ResponseEntity<>(null, HttpStatus.FORBIDDEN); } // Not your chat.

        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); } // Can't find account.

        Message message = new Message();
        message.setType(Message_Type.MESSAGE);
        message.setChat(chat);
        message.setAccount(user);
        message.setContent(text);
        repository_message.save(message);
        return new ResponseEntity<>(new DTO_Get_Message(message), HttpStatus.OK);
    }

    public ResponseEntity<DTO_Get_Message> post_api_chats_id_messages(String userName, Long id, String text) {

        Optional<Account> optional_user = repository_account.findByUserName(userName);
        Account user = optional_user.get();

        Optional<Chat> optional_chat = repository_chat.findById(id);
        if (optional_chat.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); } // Can't find chat.

        Chat chat = optional_chat.get();
        if (chat.getMembers().stream().noneMatch(member -> member.getId().equals(user.getId()))) { return new ResponseEntity<>(null, HttpStatus.FORBIDDEN); } // Not your chat.

        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); } // Can't find account.

        Message message = new Message();
        message.setChat(chat);
        message.setAccount(user);
        message.setContent(text);
        repository_message.save(message);
        return new ResponseEntity<>(new DTO_Get_Message(message), HttpStatus.OK);
    }
}
