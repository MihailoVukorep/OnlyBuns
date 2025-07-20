package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Chat;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Message;
import com.onlybuns.OnlyBuns.model.*;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Chat;
import com.onlybuns.OnlyBuns.repository.Repository_ChatMember;
import com.onlybuns.OnlyBuns.repository.Repository_Message;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class Service_Chat {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Repository_Chat repository_chat;

    @Autowired
    private Repository_Message repository_message;

    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_ChatMember repository_chatMember;

    public void BroadcastMessage(Message message) {

        repository_message.save(message);

        // Notify clients about the new message
        messagingTemplate.convertAndSend(
                "/topic/messages/" + message.getChat().getToken(),
                new DTO_Get_Message(message)
        );
    }

    public Chat ChangeChatToken(Chat chat) {

        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (repository_chat.existsByToken(token));

        chat.setToken(token);
        repository_chat.save(chat);
        return chat;
    }

    public Optional<ChatMember> findChatByChatAndAccountId(Chat chat, Long account_id) {
        return repository_chatMember.findByChatAndAccountId(chat, account_id);
    }

    public ChatMember CreateChatMember(Chat chat, Account account) {

        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (repository_chatMember.existsByToken(token));

        ChatMember accountMember = new ChatMember(chat, account, token);
        repository_chatMember.save(accountMember);
        chat.addMember(accountMember);
        return accountMember;
    }

    public Chat CreateChat(Account user, Account account) {

        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (repository_chat.existsByToken(token));

        Chat chat = new Chat(token, user, user.getFirstName() + " & " + account.getUserName());
        repository_chat.save(chat);

        CreateChatMember(chat, user);
        CreateChatMember(chat, account);
        repository_message.save(new Message(chat, user, "", Message_Type.JOINED));
        repository_message.save(new Message(chat, account, "", Message_Type.JOINED));
        return chat;
    }

    public DTO_Get_Chat CreateGroupChat(Account user, List<Long> accountIds, String chatName) {
        List<Account> accounts = new ArrayList<>();
        for(Long id : accountIds){
            Optional<Account> optional_account = repository_account.findById(id);
            if (optional_account.isEmpty()) { return null; }
            Account account = optional_account.get();
            accounts.add(account);
        }

        String token;

        do {
            token = UUID.randomUUID().toString();
        } while (repository_chat.existsByToken(token));

        List<String> usernames = accounts.stream()
                .map(a -> a.getUserName())
                .collect(Collectors.toList());

        usernames.add(0, user.getUserName());

        String finalChatName = (chatName != null && !chatName.isBlank())
                ? chatName
                : generateDefaultName(usernames);

        Chat chat = new Chat(token, user, finalChatName);
        repository_chat.save(chat);
        CreateChatMember(chat, user);
        repository_message.save(new Message(chat, user, "", Message_Type.JOINED));

        for(Account account : accounts) {
            CreateChatMember(chat, account);
            repository_message.save(new Message(chat, account, "", Message_Type.JOINED));
        }

        DTO_Get_Chat chatDto = new DTO_Get_Chat(chat.getMembers().get(0));
        return chatDto;
    }

    private String generateDefaultName(List<String> participantNames){
        if (participantNames == null) {
            return "Untitled chat";
        }

        String joined = "";

        if (participantNames.size() == 2){
            joined = participantNames.get(0) + " & " + participantNames.get(1);

        } else {
            joined = participantNames.stream()
                    .filter(Objects::nonNull)               // skip nulls
                    .map(String::trim)                      // remove leading/trailing spaces
                    .filter(s -> !s.isEmpty())              // skip blanks
                    .collect(Collectors.joining(", "));     // "Mark, Anna, Joe"
        }

        return joined.isEmpty() ? "Untitled chat" : joined;
    }

    // create chat (user & account id)
    public ResponseEntity<String> get_api_accounts_id_chat(HttpSession session, Long id) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("Not logged in.", HttpStatus.UNAUTHORIZED); }

        Optional<Account> optional_account = repository_account.findById(id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Can't find account.", HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        CreateChat(user, account);
        return new ResponseEntity<>("Chat created.", HttpStatus.OK);
    }

    // get current user's chats
    public ResponseEntity<List<DTO_Get_Chat>> get_api_chats(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }
        List<Chat> chats = repository_chat.findByMembersContains(user);
        List<DTO_Get_Chat> dto_chats = chats.stream().map(i -> new DTO_Get_Chat(repository_chatMember.findByChatAndAccountId(i, user.getId()).get())).toList();
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

        Optional<ChatMember> optional_chatMember = repository_chatMember.findByChatAndAccountId(chat, user.getId());
        if (optional_chatMember.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_Get_Chat(optional_chatMember.get()), HttpStatus.OK);
    }

    // get chat's messages
    public ResponseEntity<List<DTO_Get_Message>> get_api_chats_id_messages(HttpSession session, Long id) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<Chat> optional_chat = repository_chat.findById(id);

        if (optional_chat.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Chat chat = optional_chat.get();

        Optional<ChatMember> memberOpt = chat.getMembers().stream()
                .filter(m -> m.getAccount().getId().equals(user.getId()))
                .findFirst();

        if (memberOpt.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        // admin sees everything
        if (chat.getAdmin().getId().equals(user.getId())) {
            List<DTO_Get_Message> allMessages = chat.getMessages().stream()
                    .map(DTO_Get_Message::new)
                    .toList();
            return new ResponseEntity<>(allMessages, HttpStatus.OK);
        }

        LocalDateTime joinTime = memberOpt.get().getJoinedDate();
        List<Message> allMessages = chat.getMessages();

        // Split messages into before/after join
        List<Message> messagesAfterJoin = allMessages.stream()
                .filter(m -> m.getCreatedDate().isAfter(joinTime))
                .collect(Collectors.toList());

        List<Message> messagesBeforeJoin = allMessages.stream()
                .filter(m -> m.getCreatedDate().isBefore(joinTime))
                .sorted(Comparator.comparing(Message::getCreatedDate).reversed()) // Newest first
                .limit(10) // Take up to 10 most recent before join
                .sorted(Comparator.comparing(Message::getCreatedDate)) // Re-sort chronologically
                .collect(Collectors.toList());
        
        List<Message> visibleMessages = new ArrayList<>();
        visibleMessages.addAll(messagesBeforeJoin);
        visibleMessages.addAll(messagesAfterJoin);

        List<DTO_Get_Message> result = visibleMessages.stream()
                .map(DTO_Get_Message::new)
                .toList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // send message to chats
    public ResponseEntity<DTO_Get_Message> post_api_chats_id_messages(HttpSession session, Long id, String text) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); } // Not logged in.

        Optional<Chat> optional_chat = repository_chat.findById(id);
        if (optional_chat.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); } // Can't find chat.

        Chat chat = optional_chat.get();
        if (chat.getMembers().stream().noneMatch(member -> member.getAccount().getId().equals(user.getId()))) { return new ResponseEntity<>(null, HttpStatus.FORBIDDEN); } // Not your chat.

        Message message = new Message();
        message.setType(Message_Type.MESSAGE);
        message.setChat(chat);
        message.setAccount(user);
        message.setContent(text);
        repository_message.save(message);
        return new ResponseEntity<>(new DTO_Get_Message(message), HttpStatus.OK);
    }
    public ResponseEntity<DTO_Get_Message> post_api_chats_id_messages(String token, String userToken, String text) {

        Optional<Chat> optional_chat = repository_chat.findByToken(token);
        if (optional_chat.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        Optional<ChatMember> optional_chatMember = repository_chatMember.findByToken(userToken);
        if (optional_chatMember.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }
        ChatMember chatMember = optional_chatMember.get();

        Message message = new Message();
        message.setChat(optional_chat.get());
        message.setAccount(chatMember.getAccount());
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

        Optional<Account> optional_account = repository_account.findById(account_id);
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        Optional<ChatMember> optional_chatMember = repository_chatMember.findByChatAndAccountId(chat, account_id);
        if (optional_chatMember.isPresent()) { return new ResponseEntity<>("Account already added to chat.", HttpStatus.CONFLICT); }

        CreateChatMember(chat, account);

        BroadcastMessage(new Message(chat, account, "", Message_Type.ADDED));
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
        if (optional_account.isEmpty()) { return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND); }
        Account account = optional_account.get();

        Optional<ChatMember> optional_chatMember = repository_chatMember.findByChatAndAccountId(chat, account_id);
        if (optional_chatMember.isEmpty()) { return new ResponseEntity<>("Member not found in chat.", HttpStatus.NOT_FOUND); }

        ChatMember member = optional_chatMember.get();
        repository_chatMember.delete(member);

        BroadcastMessage(new Message(chat, account, "", Message_Type.REMOVED));

        ChangeChatToken(chat); // change chat token when someone gets removed so we nuke lurkers

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

        Optional<ChatMember> optional_chatMember = repository_chatMember.findByChatAndAccountId(chat, user.getId());
        if (optional_chatMember.isEmpty()) { return new ResponseEntity<>("Member not found in chat.", HttpStatus.NOT_FOUND); }

        ChatMember member = optional_chatMember.get();
        repository_chatMember.delete(member);

        BroadcastMessage(new Message(chat, user, "", Message_Type.LEFT));

        ChangeChatToken(chat); // change chat token when someone gets removed so we nuke lurkers

        return new ResponseEntity<>("Left from chat.", HttpStatus.OK);
    }

}
