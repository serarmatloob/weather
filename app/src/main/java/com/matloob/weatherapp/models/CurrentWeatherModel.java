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
    @SerializedName("wind")
    private Wind wind;
    @SerializedName("dt")
    private long dt;

    @SerializedName("dt_txt")
    private String dt_txt;

    public Wind getWind() {
        return wind;
    }

    public long getDt() {
        return dt;
    }

    public String getDt_txt() {
        return dt_txt;
    }

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


