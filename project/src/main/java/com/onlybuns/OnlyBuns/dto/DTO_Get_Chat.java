package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Chat;

import java.util.List;

public class DTO_Get_Chat {

    public Long id;
    public String name;
    public List<DTO_Get_Chat_Account> members;

    public DTO_Get_Chat(Chat chat) {
        this.id = chat.getId();
        this.name = chat.getName();
        this.members = chat.getMembers().stream().map(DTO_Get_Chat_Account::new).toList();
    }
}
