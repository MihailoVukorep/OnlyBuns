package com.onlybuns.OnlyBuns.dto;

import com.onlybuns.OnlyBuns.model.Account;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_CreatePost {
    public String title;
    public String description;  //text
    //public String picture;
    public String location;
    public Account account;

    public String validate() {
        if (isNullOrEmpty(title)) {
            return "Title cannot be empty";
        }
        if (isNullOrEmpty(description)) {
            return "Description cannot be empty";
        }
        /*if (isNullOrEmpty(picture)) {
            return "Picture cannot be empty";
        }*/
        if (isNullOrEmpty(location)) {
            return "Location name cannot be empty";
        }
        return null;
    }


    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
