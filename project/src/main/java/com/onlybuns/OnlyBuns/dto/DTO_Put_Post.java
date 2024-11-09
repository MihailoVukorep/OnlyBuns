package com.onlybuns.OnlyBuns.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Put_Post {
    public String title;
    public String text;
    public String location;
    public LocalDateTime updatedDate;

    public DTO_Put_Post(String title, String text, String location) {
        this.title = title;
        this.text = text;
        this.location = location;
        this.updatedDate = LocalDateTime.now();
    }
}