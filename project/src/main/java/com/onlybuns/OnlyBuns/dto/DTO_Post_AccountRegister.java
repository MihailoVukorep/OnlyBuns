package com.onlybuns.OnlyBuns.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_Post_AccountRegister {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String mailAddress;
    private String password;

    public DTO_Post_AccountRegister(String firstName, String lastName, String username, String mailAddress, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.mailAddress = mailAddress;
        this.password = password;
    }
}
