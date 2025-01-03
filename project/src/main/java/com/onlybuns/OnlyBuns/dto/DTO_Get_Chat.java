package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Chat;
import com.onlybuns.OnlyBuns.model.ChatMember;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DTO_Get_Chat {

    public Long id;
    public String token;
    public String name;
    public Long adminId;
    public List<DTO_Get_Chat_Account> members;
    public LocalDateTime createdDate;
    public String createdDateStr;
    public Boolean isMyChat;
    public String userToken;

    public DTO_Get_Chat(ChatMember chatMember) {
        Chat chat = chatMember.getChat();
        this.id = chat.getId();
        this.token = chat.getToken();
        this.name = chat.getName();
        this.adminId = chat.getAdmin().getId();
        this.userToken = chatMember.getToken();
        this.isMyChat = chatMember.getAccount().getId().equals(adminId);
        this.members = chat.getMembers().stream().map(i -> new DTO_Get_Chat_Account(i, i.getAccount().getId().equals(adminId))).toList();
        this.createdDate = chat.getCreatedDate();
        this.createdDateStr = this.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
