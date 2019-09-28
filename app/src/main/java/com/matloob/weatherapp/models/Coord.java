package com.matloob.weatherapp.models;

import java.io.Serializable;

public class Coord implements Serializable {
    private float lon;
    private float lat;

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }
}