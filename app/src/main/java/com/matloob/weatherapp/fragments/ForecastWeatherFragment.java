package com.matloob.weatherapp.fragments;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.activities.MainActivity;
import com.matloob.weatherapp.models.ForecastWeatherModel;
import com.matloob.weatherapp.services.WeatherService;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import static com.matloob.weatherapp.services.WeatherService.EXTRA_WEATHER_LOCATION_KEY;
import static com.matloob.weatherapp.services.WeatherService.EXTRA_WEATHER_REQUEST_TYPE_KEY;
import static com.matloob.weatherapp.services.WeatherService.REQUEST_TYPE_FORECAST;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastWeatherFragment extends Fragment implements ServiceConnection, WeatherService.WeatherCallback, MainActivity.LocationCallback {
    // TAG
    private static final String TAG = "ForecastWeatherFragment";
    // Fragment instance
    private static ForecastWeatherFragment forecastWeatherFragment;
    // Main activity instance
    private MainActivity mainActivity;
    // Weather service instance
    private WeatherService weatherService;
    // boolean to check if service is bound
    private boolean bound = false;

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }

    public static ForecastWeatherFragment getInstance() {
        if (forecastWeatherFragment == null) {
            forecastWeatherFragment = new ForecastWeatherFragment();
        }
        return forecastWeatherFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forecast_weather, container, false);
    }

    @Override
    public void onLocationUpdated(Location location) {
        // once we got location, we send it to our weather service, then start it
        // to get the weather response.
        Intent serviceIntent = new Intent(Application.getInstance(), WeatherService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_WEATHER_LOCATION_KEY, location);
        bundle.putInt(EXTRA_WEATHER_REQUEST_TYPE_KEY, REQUEST_TYPE_FORECAST);
        serviceIntent.putExtras(bundle);
        Application.getInstance().startService(serviceIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // update location when user is back to the app.
        mainActivity.fetchAndSaveLastKnownLocation(ForecastWeatherFragment.this);
        // then bind to the service.
        Intent serviceIntent = new Intent(Application.getInstance(), WeatherService.class);
        Application.getInstance().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
            // Set callback to null then unbind
            weatherService.setWeatherCallback(null);
            Application.getInstance().unbindService(this);
            bound = false;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onWeatherResultReady(String weatherResult) {
        ForecastWeatherModel forecastWeatherModel = null;
        // if weather results are ready and usable, then use it now.
        // Otherwise, used the last saved in shared preference.
        if (weatherResult != null) {
            SharedPreferencesUtil.getInstance().setStringPreference(Application.getInstance(), SharedPreferencesUtil.PREF_FORECAST_WEATHER, weatherResult);
            forecastWeatherModel = new Gson().fromJson(weatherResult, ForecastWeatherModel.class);
        } else {
            forecastWeatherModel = SharedPreferencesUtil.getInstance().getForecastItem(Application.getInstance());
        }

        if (forecastWeatherModel != null) {
            Log.i(TAG, "onWeatherResultReady: " + forecastWeatherModel.getList().size());
        }
    }

    @Override
    public void onWeatherResultFailed(String errorMessage) {
        Log.i(TAG, "onWeatherResultFailed: " + errorMessage);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        WeatherService.WeatherServiceBinder binder = (WeatherService.WeatherServiceBinder) iBinder;
        weatherService = binder.getService();
        weatherService.setWeatherCallback(ForecastWeatherFragment.this);
        bound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        bound = false;
    }
}
