package com.matloob.weatherapp.interfaces;

import com.matloob.weatherapp.models.ForecastWeatherModel;

import java.util.List;

/**
 * Created by Serar Matloob on 9/29/2019.
 */
public interface ArrayCallback {
    void onArrayReady(List<ForecastWeatherModel> result);
}
