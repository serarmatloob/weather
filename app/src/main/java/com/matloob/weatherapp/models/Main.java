package com.matloob.weatherapp.models;

import java.io.Serializable;

public class Main implements Serializable {
    private float temp;
    private float pressure;
    private float humidity;
    private float temp_min;
    private float temp_max;

    public float getTemp() {
        return temp;
    }

    public float getPressure() {
        return pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getTemp_min() {
        return temp_min;
    }

    public float getTemp_max() {
        return temp_max;
    }
}