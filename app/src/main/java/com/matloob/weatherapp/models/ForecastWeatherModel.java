package com.matloob.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Serar Matloob on 9/27/2019.
 */
public class ForecastWeatherModel implements Serializable {
    @SerializedName("list")
    private List<CurrentWeatherModel> list;
    @SerializedName("city")
    private City city;

    public City getCity() {
        return city;
    }

    public List<CurrentWeatherModel> getList() {
        return list;
    }

    public void setList(List<CurrentWeatherModel> list) {
        this.list = list;
    }
}
