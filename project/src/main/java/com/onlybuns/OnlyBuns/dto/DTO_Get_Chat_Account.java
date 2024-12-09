package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;

public class DTO_Get_Chat_Account {
    public Long id;
    public String userName;
    public String avatar;
    public DTO_Get_Chat_Account(Account account) {
        this.id = account.getId();
        this.userName = account.getUserName();
        this.avatar = account.getAvatar();
    }
}
