package com.onlybuns.OnlyBuns.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DTO_Get_Location {
    public String url;
    public String coordinates;
    public DTO_Get_Location_Type type;
}
