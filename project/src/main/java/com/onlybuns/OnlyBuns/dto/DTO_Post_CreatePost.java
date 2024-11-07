package com.onlybuns.OnlyBuns.dto;


public class DTO_Post_CreatePost {

    public String title;
    public String text;
    public String location;
    public String image;

    public String validate() {
        //if (isNullOrEmpty(title)) { return "Title cannot be empty"; }
        if (isNullOrEmpty(text)) { return "Text cannot be empty"; }
        //if (isNullOrEmpty(location)) { return "Location cannot be empty"; }
        //if (isNullOrEmpty(image)) { return "Image cannot be empty"; }

        return null;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
