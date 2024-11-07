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
public class DTO_View_Account {

    public Integer id;
    public String email;
    public String userName;
    //public String password;
    public String firstName;
    public String lastName;
    public String address;
    public String avatar;
    public String bio;
    public Set<Role> accountRoles;

    public DTO_View_Account(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.userName = account.getUserName();
        //this.password = account.getPassword();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.address = account.getAddress();
        this.avatar = account.getAvatar();
        this.bio = account.getBio();
        this.accountRoles = account.getRoles();
    }
}
