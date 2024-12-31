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
        Optional<Chat> optional_chat = chats.stream().filter(i -> i.getId().equals(id)).findFirst();
        if (optional_chat.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Chat chat = optional_chat.get();
        return new ResponseEntity<>(new DTO_Get_Chat(chat, user), HttpStatus.OK);
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

    // add user to chat
    public ResponseEntity<String> get_api_chats_id_add_id(HttpSession session, Long id, Long account_id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        List<Chat> chats = repository_chat.findByMembersContains(user);
        Optional<Chat> optional_chat = chats.stream().filter(i -> i.getId().equals(id)).findFirst();
        if (optional_chat.isEmpty()) { return new ResponseEntity<>("Chat not found.", HttpStatus.NOT_FOUND); }
        Chat chat = optional_chat.get();

        if (!chat.getAdmin().getId().equals(user.getId())) {
            return new ResponseEntity<>("You're not the admin of the chat.", HttpStatus.FORBIDDEN);
        }

        // add account to chat and save chat
        Optional<Account> optional_account = repository_account.findById(account_id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        if (chat.getMembers().contains(account)) {
            return new ResponseEntity<>("Account already added to chat.", HttpStatus.CONFLICT);
        }

        chat.getMembers().add(account);
        repository_chat.save(chat);
        repository_message.save(new Message(chat, account, "", Message_Type.ADDED));
        return new ResponseEntity<>("Account added to chat.", HttpStatus.OK);
    }

    public ResponseEntity<String> get_api_chats_id_remove_id(HttpSession session, Long id, Long account_id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        List<Chat> chats = repository_chat.findByMembersContains(user);
        Optional<Chat> optional_chat = chats.stream().filter(i -> i.getId().equals(id)).findFirst();
        if (optional_chat.isEmpty()) { return new ResponseEntity<>("Chat not found.", HttpStatus.NOT_FOUND); }
        Chat chat = optional_chat.get();

        if (!chat.getAdmin().getId().equals(user.getId())) {
            return new ResponseEntity<>("You're not the admin of the chat.", HttpStatus.FORBIDDEN);
        }

        // add account to chat and save chat
        Optional<Account> optional_account = repository_account.findById(account_id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        if (!chat.getMembers().contains(account)) {
            return new ResponseEntity<>("Can't find account in chat.", HttpStatus.NOT_FOUND);
        }

        chat.getMembers().remove(account);
        repository_chat.save(chat);
        repository_message.save(new Message(chat, account, "", Message_Type.REMOVED));
        return new ResponseEntity<>("Account removed from chat.", HttpStatus.OK);
    }

    public ResponseEntity<String> get_api_chats_id_leave(HttpSession session, Long id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }
        List<Chat> chats = repository_chat.findByMembersContains(user);

        Optional<Chat> optional_chat = chats.stream().filter(i -> i.getId().equals(id)).findFirst();
        if (optional_chat.isEmpty()) { return new ResponseEntity<>("Chat not found.", HttpStatus.NOT_FOUND); }
        Chat chat = optional_chat.get();

        Optional<Account> optional_account = repository_account.findById(user.getId());
        if (optional_account.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        chat.getMembers().remove(account);
        repository_chat.save(chat);
        repository_message.save(new Message(chat, user, "", Message_Type.LEFT));
        return new ResponseEntity<>("Left from chat.", HttpStatus.OK);
    }

}
