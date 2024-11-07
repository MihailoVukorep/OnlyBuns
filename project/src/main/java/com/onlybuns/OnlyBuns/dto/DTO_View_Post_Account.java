package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Role;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_View_Post_Account {
    public Integer id;
    public String userName;
    public String avatar;
    public Set<Role> roles;

    public DTO_View_Post_Account(Account account) {
        this.id = account.getId();
        this.userName = account.getUserName();
        this.avatar = account.getAvatar();
        this.roles = account.getRoles();
    }
}
