package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import jakarta.persistence.Column;

public class AccountDTO {

    private Integer id;
    private String userName;
    private String firstName;
    private String lastName;

    public AccountDTO() {

    }

    public AccountDTO(Account account) {
        super();
        this.id = account.getId();
        this.userName = account.getUserName();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
