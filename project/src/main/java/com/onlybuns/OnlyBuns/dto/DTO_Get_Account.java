package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.repository.Repository_Follow;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Get_Account {

    public Long id;
    public String email;
    public String userName;
    //public String password;
    public String firstName;
    public String lastName;
    public String address;
    public String avatar;
    public String bio;
    public boolean isAdmin;
    public LocalDateTime createdDate;
    public String createdDateStr;
    public LocalDateTime updatedDate;
    public String updatedDateStr;
    public Integer postsCount;
    public Integer likesCount;
    public Integer followersCount;
    public Integer followingCount;
    public boolean isMyAccount;
    public boolean isFollowing;

    public DTO_Get_Account(Account account, Repository_Follow repository_follow) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.userName = account.getUserName();
        //this.password = account.getPassword();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.address = account.getAddress();
        this.avatar = account.getAvatar();
        this.bio = account.getBio();
        this.isAdmin = account.isAdmin();
        this.createdDate = account.getCreatedDate();
        this.createdDateStr = this.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.updatedDate = account.getUpdatedDate();
        this.updatedDateStr = this.updatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.postsCount = account.getPosts().size();
        this.likesCount = account.getLikes().size();
        this.followersCount = repository_follow.countFollowers(account);
        this.followingCount = repository_follow.countFollowees(account);
    }

    public DTO_Get_Account(Account account, boolean myAccount, boolean following, Repository_Follow repository_follow) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.userName = account.getUserName();
        //this.password = account.getPassword();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.address = account.getAddress();
        this.avatar = account.getAvatar();
        this.bio = account.getBio();
        this.isAdmin = account.isAdmin();
        this.createdDate = account.getCreatedDate();
        this.createdDateStr = this.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.updatedDate = account.getUpdatedDate();
        this.updatedDateStr = this.updatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.postsCount = account.getPosts().size();
        this.likesCount = account.getLikes().size();
        this.followersCount = repository_follow.countFollowers(account);
        this.followingCount = repository_follow.countFollowees(account);
        this.isMyAccount = myAccount;
        this.isFollowing = following;
    }

    public DTO_Get_Account(Account account, int followersCount) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.userName = account.getUserName();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.address = account.getAddress();
        this.avatar = account.getAvatar();
        this.bio = account.getBio();
        this.isAdmin = account.isAdmin();
        this.createdDate = account.getCreatedDate();
        this.createdDateStr = this.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.updatedDate = account.getUpdatedDate();
        this.updatedDateStr = this.updatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.postsCount = account.getPosts().size();
        this.likesCount = account.getLikes().size();
        this.followersCount = followersCount;
        this.followingCount = 0; // or you could fetch this if needed
        this.isMyAccount = false;
        this.isFollowing = false;
    }
}
