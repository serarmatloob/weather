package com.matloob.weatherapp.models;

import java.io.Serializable;

public class City implements Serializable {
    private String name;
    private String country;
    private Coord coord;

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Coord getCoord() {
        return coord;
    }
}