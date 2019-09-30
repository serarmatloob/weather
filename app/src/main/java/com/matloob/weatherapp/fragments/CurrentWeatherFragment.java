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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.activities.MainActivity;
import com.matloob.weatherapp.models.CurrentWeatherModel;
import com.matloob.weatherapp.services.WeatherService;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import java.util.Calendar;
import java.util.Locale;

import static com.matloob.weatherapp.services.WeatherService.EXTRA_WEATHER_LOCATION_KEY;
import static com.matloob.weatherapp.services.WeatherService.EXTRA_WEATHER_REQUEST_TYPE_KEY;
import static com.matloob.weatherapp.services.WeatherService.REQUEST_TYPE_CURRENT;

/**
 * This Fragment class shows the current weather details.
 */
public class CurrentWeatherFragment extends Fragment implements ServiceConnection, WeatherService.WeatherCallback, MainActivity.MainCallback, SwipeRefreshLayout.OnRefreshListener {
    //TAG
    private static final String TAG = "CurrentWeatherFragment";
    // Boolean to know if bound to service or not
    private boolean bound = false;
    // Main activity instance
    private MainActivity mainActivity;
    // Weather service instance
    private WeatherService weatherService;
    // Fragment view instance
    private View view;
    // Progressbar instance
    private ProgressBar progressBar;
    // Layout instance
    private LinearLayout currentWeatherLayout;
    // SwipeRefresh layout instance
    private SwipeRefreshLayout swipeRefreshLayout;

    public CurrentWeatherFragment() {
        // Required empty public constructor
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_weather, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // init views
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        currentWeatherLayout = view.findViewById(R.id.current_weather_layout);
        currentWeatherLayout.setVisibility(View.GONE);
        swipeRefreshLayout = view.findViewById(R.id.current_weather_container);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // update weather when user navigate back to the fragment
        updateCurrentWeather();
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
            mainActivity.setMainCallback(CurrentWeatherFragment.this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        updateCurrentWeather();
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

    /**
     * This function request to update weather from main activity and bind to the service
     */
    private void updateCurrentWeather() {
        progressBar.setVisibility(View.VISIBLE);
        currentWeatherLayout.setVisibility(View.GONE);
        mainActivity.createLocationRequest();
        Intent serviceIntent = new Intent(Application.getInstance(), WeatherService.class);
        Application.getInstance().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
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
        // If not null, update the UI with the new results, otherwise show error message to user
        if (currentWeatherModel != null) {
            updateUI(currentWeatherModel);
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
        CurrentWeatherModel currentWeatherModel = SharedPreferencesUtil.getInstance().getWeatherItem(Application.getInstance());
        // If we already have something saved in shared preference, then we just show connection error and old weather date.
        if (currentWeatherModel != null) {
            Toast.makeText(Application.getInstance(), R.string.connection_error_showing_last_known, Toast.LENGTH_LONG).show();
            updateUI(currentWeatherModel);
        } else {
            // In case we don't have any data
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
     * @param currentWeatherModel {@link CurrentWeatherModel} instance
     */
    private void updateUI(final CurrentWeatherModel currentWeatherModel) {
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
                TextView wind = view.findViewById(R.id.current_wind);
                TextView date = view.findViewById(R.id.current_date);
                ImageView icon = view.findViewById(R.id.current_icon);

                try {
                    loadIconWithGlide(icon, currentWeatherModel.getWeather()[0].getIcon());
                    city.setText(currentWeatherModel.getName());
                    temperature.setText(getString(R.string.temperature, (int) currentWeatherModel.getMain().getTemp()));
                    humidity.setText(getString(R.string.current_humidity, (int) currentWeatherModel.getMain().getHumidity()));
                    pressure.setText(getString(R.string.current_pressure, (int) currentWeatherModel.getMain().getPressure()));
                    wind.setText(getString(R.string.current_wind, (int) currentWeatherModel.getWind().getSpeed()));
                    main.setText(currentWeatherModel.getWeather()[0].getMain());
                    description.setText(capitalize(currentWeatherModel.getWeather()[0].getDescription()));
                    date.setText(getString(R.string.last_updated, getDate(currentWeatherModel.getDt())));
                    progressBar.setVisibility(View.GONE);
                    currentWeatherLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * helper to get readable date and time
     *
     * @param time in unix time
     * @return a {@link String} of date and time
     */
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format("MM-dd-yyyy hh:mma", cal).toString();
    }

    /**
     * Helper to format weather forecast
     *
     * @param text text
     * @return a {@link String} of capitalized first word of string.
     */
    private String capitalize(final String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /**
     * Helper function to load icon into image view
     *
     * @param imageView {@link ImageView}
     * @param icon      {@link String} code
     */
    private void loadIconWithGlide(ImageView imageView, String icon) {
        Glide.with(Application.getInstance()).load(getString(R.string.icon_endpoint, icon)).into(imageView);
    }
}
