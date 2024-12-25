package com.onlybuns.OnlyBuns.dto;

import java.util.ArrayList;
import java.util.List;

public class DTO_Get_Location {
    public String userCoordinates;
    public List<String> coordinates = new ArrayList<>();

    public DTO_Get_Location(String userCoordinates, List<String> coordinates) {
        this.userCoordinates = userCoordinates;
        this.coordinates = coordinates;
    }
}
