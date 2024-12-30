package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DTO_Get_Chat {

    public Long id;
    public String name;
    public DTO_Get_Chat_Account admin;
    public List<DTO_Get_Chat_Account> members;
    public LocalDateTime createdDate;
    public String createdDateStr;
    public Boolean isMyChat;

    public DTO_Get_Chat(Chat chat, Account user) {
        this.id = chat.getId();
        this.name = chat.getName();
        this.admin = new DTO_Get_Chat_Account(chat.getAdmin(), true);
        this.members = chat.getMembers().stream().map(i -> new DTO_Get_Chat_Account(i, i.getId().equals(admin.getId()))).toList();
        this.createdDate = chat.getCreatedDate();
        this.createdDateStr = this.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.isMyChat = user.getId().equals(admin.getId());
    }
}
