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

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public class Coord implements Serializable {
        private float lon;
        private float lat;

        public float getLon() {
            return lon;
        }

        public void setLon(float lon) {
            this.lon = lon;
        }

        public float getLat() {
            return lat;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }
    }

    public class Main implements Serializable {
        private float temp;
        private float pressure;
        private float humidity;
        private float temp_min;
        private float temp_max;

        public Main(float temp, int pressure, int humidity, float temp_min, float temp_max) {
            this.temp = temp;
            this.pressure = pressure;
            this.humidity = humidity;
            this.temp_min = temp_min;
            this.temp_max = temp_max;
        }

        public float getTemp() {
            return temp;
        }

        public void setTemp(float temp) {
            this.temp = temp;
        }

        public float getPressure() {
            return pressure;
        }

        public void setPressure(int pressure) {
            this.pressure = pressure;
        }

        public float getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public float getTemp_min() {
            return temp_min;
        }

        public void setTemp_min(float temp_min) {
            this.temp_min = temp_min;
        }

        public float getTemp_max() {
            return temp_max;
        }

        public void setTemp_max(float temp_max) {
            this.temp_max = temp_max;
        }
    }

    public class Weather implements Serializable {
        @SerializedName("main")
        private String main;
        @SerializedName("description")
        private String description;

        Weather(String main, String description) {
            this.main = main;
            this.description = description;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}


