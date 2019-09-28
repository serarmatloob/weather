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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.activities.MainActivity;
import com.matloob.weatherapp.models.CurrentWeatherModel;
import com.matloob.weatherapp.services.WeatherService;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import static com.matloob.weatherapp.services.WeatherService.EXTRA_WEATHER_LOCATION_KEY;
import static com.matloob.weatherapp.services.WeatherService.EXTRA_WEATHER_REQUEST_TYPE_KEY;
import static com.matloob.weatherapp.services.WeatherService.REQUEST_TYPE_CURRENT;

public class CurrentWeatherFragment extends Fragment implements ServiceConnection, WeatherService.WeatherCallback, MainActivity.LocationCallback {
    //TAG
    private static final String TAG = "CurrentWeatherFragment";

    private static CurrentWeatherFragment currentWeatherFragment;
    private boolean bound = false;

    private MainActivity mainActivity;

    private WeatherService weatherService;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLocationUpdated(Location location) {
        Intent serviceIntent = new Intent(Application.getInstance(), WeatherService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_WEATHER_LOCATION_KEY, location);
        bundle.putInt(EXTRA_WEATHER_REQUEST_TYPE_KEY, REQUEST_TYPE_CURRENT);
        serviceIntent.putExtras(bundle);
        Application.getInstance().startService(serviceIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivity.fetchAndSaveLastKnownLocation(CurrentWeatherFragment.this);
        Intent serviceIntent = new Intent(Application.getInstance(), WeatherService.class);
        Application.getInstance().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
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
        CurrentWeatherModel currentWeatherModel = null;
        // if weather results are ready and usable, then use it now.
        // Otherwise, used the last saved in shared preference.
        if (weatherResult != null) {
            SharedPreferencesUtil.getInstance().setStringPreference(Application.getInstance(), SharedPreferencesUtil.PREF_CURRENT_WEATHER, weatherResult);
            currentWeatherModel = new Gson().fromJson(weatherResult, CurrentWeatherModel.class);
        } else {
            currentWeatherModel = SharedPreferencesUtil.getInstance().getWeatherItem(Application.getInstance());
        }

        if (currentWeatherModel != null) {
            Log.i(TAG, "onWeatherResultReady: " + currentWeatherModel.getMain().getTemp());
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
        weatherService.setWeatherCallback(CurrentWeatherFragment.this);
        bound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        bound = false;
    }
}
