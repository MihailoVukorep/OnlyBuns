package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;

import java.time.LocalDate;


public class DTO_Post_Account {

    public Integer id;
    public String userName;
    public String email;
    public String address;
    //public String password;
    public String firstName;
    public String lastName;
    public LocalDate dateOfBirth;
    public String profilePicture;
    public String description;
    public AccountRole accountRole;

    public DTO_Post_Account() {

    }

    public DTO_Post_Account(Account account) {
        this.id = account.getId();
        this.userName = account.getUserName();
        this.email = account.getEmail();
        this.address = account.getAddress();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.dateOfBirth = account.getDateOfBirth();
        this.profilePicture = account.getProfilePicture();
        this.description = account.getDescription();
        this.accountRole = account.getAccountRole();
    }
}
