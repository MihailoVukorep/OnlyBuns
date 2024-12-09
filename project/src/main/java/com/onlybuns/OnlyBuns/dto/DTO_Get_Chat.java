package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DTO_Get_Chat {

    public Long id;
    public String name;
    public List<DTO_Get_Chat_Account> members;
    public LocalDateTime createdDate;
    public String createdDateStr;

    public DTO_Get_Chat(Chat chat) {
        this.id = chat.getId();
        this.name = chat.getName();
        this.members = chat.getMembers().stream().map(DTO_Get_Chat_Account::new).toList();
        this.createdDate = chat.getCreatedDate();
        this.createdDateStr = this.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
