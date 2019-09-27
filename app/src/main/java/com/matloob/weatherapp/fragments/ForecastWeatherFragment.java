package com.matloob.weatherapp.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matloob.weatherapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastWeatherFragment extends Fragment {

    private static ForecastWeatherFragment forecastWeatherFragment;

    public static ForecastWeatherFragment getInstance() {
        if (forecastWeatherFragment == null) {
            forecastWeatherFragment = new ForecastWeatherFragment();
        }
        return forecastWeatherFragment;
    }

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forecast_weather, container, false);
    }

}
