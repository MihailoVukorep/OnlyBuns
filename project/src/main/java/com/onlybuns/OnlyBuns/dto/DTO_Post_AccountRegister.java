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
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
}
