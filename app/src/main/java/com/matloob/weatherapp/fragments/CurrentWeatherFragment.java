package com.matloob.weatherapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matloob.weatherapp.R;

public class CurrentWeatherFragment extends Fragment {

    private static CurrentWeatherFragment currentWeatherFragment;

    public static CurrentWeatherFragment getInstance() {
        if (currentWeatherFragment == null) {
            currentWeatherFragment = new CurrentWeatherFragment();
        }
        return currentWeatherFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_weather, container, false);
    }

}
