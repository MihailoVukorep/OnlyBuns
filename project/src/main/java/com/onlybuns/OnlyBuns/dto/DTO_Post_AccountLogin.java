package com.onlybuns.OnlyBuns.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DTO_Post_AccountLogin {
    private String email;
    private String password;
}
