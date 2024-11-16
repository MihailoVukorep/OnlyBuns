package com.onlybuns.OnlyBuns.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Post_AccountLogin {
    public String email;
    public String password;

    public String validate() {
        if (isNullOrEmpty(email)) {
            return "Email cannot be empty.";
        }
        if (isNullOrEmpty(password)) {
            return "Password cannot be empty.";
        }
        return null;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
