package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_Get_Chat_Account {
    public Long id;
    public String userName;
    public String avatar;
    public Boolean isAdmin;

    public DTO_Get_Chat_Account(Account account, Boolean isAdmin) {
        this.id = account.getId();
        this.userName = account.getUserName();
        this.avatar = account.getAvatar();
        this.isAdmin = isAdmin;
    }
}
