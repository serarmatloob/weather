package com.matloob.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Serar Matloob on 9/26/2019
 */
public class CurrentWeatherModel implements Serializable {
    @SerializedName("weather")
    private Weather[] weather;
    @SerializedName("main")
    private Main main;
    @SerializedName("name")
    private String name;
    @SerializedName("coord")
    private Coord coord;

    public Coord getCoord() {
        return coord;
    }

    public String getName() {
        return name;
    }

    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }
}


