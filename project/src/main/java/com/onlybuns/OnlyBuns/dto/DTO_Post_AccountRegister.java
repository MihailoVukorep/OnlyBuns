package com.onlybuns.OnlyBuns.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Post_AccountRegister {
    public String email;
    public String userName;
    public String password;
    public String firstName;
    public String lastName;
    public String address;
}
