package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.ChatMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_Get_Chat_Account {
    public Long id;
    public String userName;
    public String avatar;
    public Boolean isGroupAdmin;
    public LocalDateTime joinedDate;
    public String joinedDateStr;

    public DTO_Get_Chat_Account(ChatMember chatMember, Boolean isGroupAdmin) {
        Account account = chatMember.getAccount();
        this.id = account.getId();
        this.userName = account.getUserName();
        this.avatar = account.getAvatar();
        this.isGroupAdmin = isGroupAdmin;
        this.joinedDate = chatMember.getJoinedDate();
        this.joinedDateStr = this.joinedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
