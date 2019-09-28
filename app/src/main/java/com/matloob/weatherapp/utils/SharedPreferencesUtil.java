package com.matloob.weatherapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.matloob.weatherapp.models.CurrentWeatherModel;
import com.matloob.weatherapp.models.ForecastWeatherModel;

/**
 * Created by Serar Matloob on 9/27/2019
 */

/**
 * This is a helper class to save and retrieve values in shared preferences
 */
public class SharedPreferencesUtil {

    // Shared preferences keys
    public static final String PREF_LAST_LONG = "last_long";
    public static final String PREF_LAST_LAT = "last_lat";
    public static final String PREF_CURRENT_WEATHER = "current_weather";
    public static final String PREF_FORECAST_WEATHER = "forecast_weather";

    private static SharedPreferencesUtil sharedPreferencesUtil;

    /**
     * Get instance of this class
     *
     * @return a {@link SharedPreferencesUtil} instance
     */
    public static SharedPreferencesUtil getInstance() {
        if (sharedPreferencesUtil == null) {
            sharedPreferencesUtil = new SharedPreferencesUtil();
        }
        return sharedPreferencesUtil;
    }

    /**
     * Save boolean in preferences
     *
     * @param context
     * @param key
     * @param value
     */
    public void setBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Get boolean from preferences
     *
     * @param context
     * @param key
     * @return a {@link Boolean} value
     */
    public boolean getBooleanPreference(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, false);
    }

    /**
     * Save a string in preferences
     *
     * @param context
     * @param key
     * @param value
     */
    public void setStringPreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(key, value).apply();
    }

    /**
     * Get a string from preferences
     *
     * @param context
     * @param key
     * @return a {@link String} value
     */
    public String getStringPreference(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    /**
     * Save a double in preferences
     *
     * @param context
     * @param key
     * @param value
     */
    public void setDoublePreference(Context context, String key, double value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putLong(key, Double.doubleToRawLongBits(value)).apply();
    }

    /**
     * Get a double from preferences
     *
     * @param context
     * @param key
     * @return a {@link Double} value
     */
    public double getDoublePreference(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Double.longBitsToDouble(preferences.getLong(key, Double.doubleToLongBits(0)));
    }

    /**
     * Save an integer in preferences
     *
     * @param context
     * @param key
     * @param value
     */
    public void setIntPreference(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putInt(key, value).apply();
    }

    /**
     * Get an integer from preferences
     *
     * @param context
     * @param key
     * @return a {@link Integer} value
     */
    public int getIntPreference(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 5);
    }

    /**
     * Get the last currentWeather model saved in shared preferences
     *
     * @param context
     * @return a {@link CurrentWeatherModel} instance
     */
    public CurrentWeatherModel getWeatherItem(Context context) {
        String jsonText = getStringPreference(context, PREF_CURRENT_WEATHER);
        return new Gson().fromJson(jsonText, CurrentWeatherModel.class);
    }

    /**
     * Get the last forecastWeather model saved in shared preferences
     *
     * @param context
     * @return a {@link ForecastWeatherModel} instance
     */
    public ForecastWeatherModel getForecastItem(Context context) {
        String jsonText = getStringPreference(context, PREF_FORECAST_WEATHER);
        return new Gson().fromJson(jsonText, ForecastWeatherModel.class);
    }

}
