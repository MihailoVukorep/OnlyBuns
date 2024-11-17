package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;

public class DTO_Get_User {
    public Long id;
    public String email;
    public String userName;
    public String avatar;
    public boolean isAdmin;

    public DTO_Get_User(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.userName = account.getUserName();
        this.avatar = account.getAvatar();
        this.isAdmin = account.isAdmin();
    }
}
