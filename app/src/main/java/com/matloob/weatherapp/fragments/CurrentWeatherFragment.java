package com.matloob.weatherapp.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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

public class CurrentWeatherFragment extends Fragment implements ServiceConnection, WeatherService.WeatherCallback, MainActivity.MainCallback {
    //TAG
    private static final String TAG = "CurrentWeatherFragment";

    private boolean bound = false;

    private MainActivity mainActivity;

    private WeatherService weatherService;

    private View view;
    private ProgressBar progressBar;
    private LinearLayout currentWeatherLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_weather, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        currentWeatherLayout = view.findViewById(R.id.current_weather_layout);
        currentWeatherLayout.setVisibility(View.GONE);
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
    public void onConnectionChanged() {
        mainActivity.fetchAndSaveLastKnownLocation(CurrentWeatherFragment.this);
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
            updateUI(currentWeatherModel);
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

    public void updateUI(final CurrentWeatherModel model) {
        Handler updateUiHandler = new Handler(Looper.getMainLooper());
        updateUiHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView temperature = view.findViewById(R.id.current_temp);
                TextView humidity = view.findViewById(R.id.current_humidity);
                TextView pressure = view.findViewById(R.id.current_pressure);
                TextView main = view.findViewById(R.id.current_main);
                TextView city = view.findViewById(R.id.current_city);
                TextView description = view.findViewById(R.id.current_description);
                ImageView icon = view.findViewById(R.id.current_icon);
                loadIconWithGlide(icon, model.getWeather()[0].getIcon());
                city.setText(model.getName());
                temperature.setText(getString(R.string.temperature, (int) model.getMain().getTemp()));
                humidity.setText(getString(R.string.current_humidity,(int) model.getMain().getHumidity()));
                pressure.setText(getString(R.string.current_pressure, (int) model.getMain().getPressure()));
                main.setText(model.getWeather()[0].getMain());
                description.setText(capitalize(model.getWeather()[0].getDescription()));
                progressBar.setVisibility(View.GONE);
                currentWeatherLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private String capitalize(final String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private void loadIconWithGlide(ImageView imageView, String icon){
        Glide.with(Application.getInstance()).load(getString(R.string.icon_endpoint, icon)).into(imageView);
    }

}
