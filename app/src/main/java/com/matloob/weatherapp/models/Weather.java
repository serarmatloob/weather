package com.matloob.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Weather implements Serializable {
    @SerializedName("main")
    private String main;
    @SerializedName("description")
    private String description;
    @SerializedName("icon")
    private String icon;

    public String getIcon() {
        return icon;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }
}