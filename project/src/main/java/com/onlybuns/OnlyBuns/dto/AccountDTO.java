package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import jakarta.persistence.*;

import java.time.LocalDate;


public class AccountDTO {

    public Integer id;
    public String userName;
    public String mailAddress;
    //public String password;
    public String firstName;
    public String lastName;
    public LocalDate dateOfBirth;
    public String profilePicture;
    public String description;
    public AccountRole accountRole;

    public AccountDTO() {

    }

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.userName = account.getUserName();
        this.mailAddress = account.getMailAddress();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.dateOfBirth = account.getDateOfBirth();
        this.profilePicture = account.getProfilePicture();
        this.description = account.getDescription();
        this.accountRole = account.getAccountRole();
    }
}
