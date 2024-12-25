package com.onlybuns.OnlyBuns.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Post_Reply {
    public Long id;
    public String title;
    public String text;
}
