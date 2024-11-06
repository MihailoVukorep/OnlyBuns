package com.onlybuns.OnlyBuns.dto;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Admin_View_Account {

    public Integer id;
    public String email;
    public String userName;
    public String avatar;
    public AccountRole accountRole;
    public Integer posts_count;
    public Integer following_count;

    public DTO_Admin_View_Account(Account account) {
        this.email = account.getEmail();
        this.userName = account.getUserName();
        this.avatar = account.getAvatar();
        this.accountRole = account.getAccountRole();
        this.posts_count = 0;
        this.following_count = 0;
    }
}
