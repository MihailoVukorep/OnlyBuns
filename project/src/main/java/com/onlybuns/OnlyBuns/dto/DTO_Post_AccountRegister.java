package com.onlybuns.OnlyBuns.dto;
import lombok.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String validate() {
        if (isNullOrEmpty(email)) { return "Email cannot be empty"; }
        if (isNullOrEmpty(userName)) { return "Username cannot be empty"; }
        if (isNullOrEmpty(password)) { return "Password cannot be empty"; }
        if (isNullOrEmpty(firstName)) { return "First name cannot be empty"; }
        if (isNullOrEmpty(lastName)) { return "Last name cannot be empty"; }
        if (isNullOrEmpty(address)) { return "Address cannot be empty"; }
        if (!isValidEmail(email)) { return "Email is not valid."; }
        return null;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
