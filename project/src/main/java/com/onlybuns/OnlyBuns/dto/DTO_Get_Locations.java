package com.onlybuns.OnlyBuns.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DTO_Get_Locations {
    public String userCoordinates;
    public List<DTO_Get_Location> locations;

    public DTO_Get_Locations(String userCoordinates, List<DTO_Get_Location> locations) {
        this.userCoordinates = userCoordinates;
        this.locations = locations;
    }
}
