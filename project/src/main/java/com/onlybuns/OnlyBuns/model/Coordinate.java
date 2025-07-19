package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coordinate {
    private double lat;
    private double lon;

    public Coordinate(){}
    public Coordinate(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }
}
