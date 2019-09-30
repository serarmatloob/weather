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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.activities.MainActivity;
import com.matloob.weatherapp.activities.MainActivity.MainCallback;
import com.matloob.weatherapp.adapters.DaysForecastAdapter;
import com.matloob.weatherapp.models.ForecastWeatherModel;
import com.matloob.weatherapp.services.WeatherService;
import com.matloob.weatherapp.tasks.CreateForecastArrays;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import java.util.List;

import static com.matloob.weatherapp.services.WeatherService.EXTRA_WEATHER_LOCATION_KEY;
import static com.matloob.weatherapp.services.WeatherService.EXTRA_WEATHER_REQUEST_TYPE_KEY;
import static com.matloob.weatherapp.services.WeatherService.REQUEST_TYPE_FORECAST;

/**
 * This Fragment class shows the forecast weather details.
 */
public class ForecastWeatherFragment extends Fragment implements ServiceConnection, WeatherService.WeatherCallback,
        MainCallback, CreateForecastArrays.ArrayCallback, SwipeRefreshLayout.OnRefreshListener {
    // TAG
    private static final String TAG = "ForecastWeatherFragment";
    // Main activity instance
    private MainActivity mainActivity;
    // Weather service instance
    private WeatherService weatherService;
    // boolean to check if service is bound
    private boolean bound = false;
    // Our 5 days recycler view
    private RecyclerView daysForecastRecycler;
    // Loading progress
    private ProgressBar progressBar;
    // Refresh layout
    private SwipeRefreshLayout swipeRefreshLayout;

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forecast_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // init views
        progressBar = view.findViewById(R.id.progress_bar);
        daysForecastRecycler = view.findViewById(R.id.days_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.forecast_weather_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        // create layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // attach it to recycler
        daysForecastRecycler.setLayoutManager(linearLayoutManager);
        // set visibility to gone until we have new data
        daysForecastRecycler.setVisibility(View.GONE);
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
    public void onLocationUnavailable() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        mainActivity.showConnectionErrorSnackbar("Failed to get location!", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefresh();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        updateForecastWeather();
    }

    private void updateForecastWeather() {
        // update location when user is back to the app.
        progressBar.setVisibility(View.VISIBLE);
        daysForecastRecycler.setVisibility(View.GONE);
        mainActivity.createLocationRequest();
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
            mainActivity.setMainCallback(ForecastWeatherFragment.this);
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
            updateUI(forecastWeatherModel);
        } else {
            progressBar.setVisibility(View.GONE);
            mainActivity.showConnectionErrorSnackbar("Something went wrong", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRefresh();
                }
            });
        }

    }

    @Override
    public void onWeatherResultFailed(String errorMessage) {
        Log.i(TAG, "onWeatherResultFailed: " + errorMessage);
        ForecastWeatherModel forecastWeatherModel = SharedPreferencesUtil.getInstance().getForecastItem(Application.getInstance());
        if (forecastWeatherModel != null) {
            Toast.makeText(Application.getInstance(), R.string.connection_error_showing_last_known, Toast.LENGTH_LONG).show();
            updateUI(forecastWeatherModel);
        } else {
            progressBar.setVisibility(View.GONE);
            mainActivity.showConnectionErrorSnackbar(getString(R.string.connection_error_try_again), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRefresh();
                }
            });
        }
    }

    /**
     * Updates the UI with the new model
     *
     * @param forecastWeatherModel {@link ForecastWeatherModel} instance
     */
    private void updateUI(final ForecastWeatherModel forecastWeatherModel) {
        if (forecastWeatherModel != null) {
            new CreateForecastArrays(forecastWeatherModel, this).execute();
        }

    }

    /**
     * Called when forecast array ready to be used.
     *
     * @param result ForecastWeatherModel array list
     */
    @Override
    public void onArrayReady(List<ForecastWeatherModel> result) {
        DaysForecastAdapter daysForecastAdapter = new DaysForecastAdapter(result);
        daysForecastRecycler.setAdapter(daysForecastAdapter);
        progressBar.setVisibility(View.GONE);
        daysForecastRecycler.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        updateForecastWeather();
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

