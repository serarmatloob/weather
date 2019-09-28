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

    public void setCity(City city) {
        this.city = city;
    }

    public List<CurrentWeatherModel> getList() {
        return list;
    }

    public void setList(List<CurrentWeatherModel> list) {
        this.list = list;
    }

    public class City implements Serializable {
        private String name;
        private String country;
        private CurrentWeatherModel.Coord coord;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public CurrentWeatherModel.Coord getCoord() {
            return coord;
        }

        public void setCoord(CurrentWeatherModel.Coord coord) {
            this.coord = coord;
        }
    }
}
