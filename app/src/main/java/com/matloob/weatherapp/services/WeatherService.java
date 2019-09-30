package com.matloob.weatherapp.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import org.json.JSONObject;

public class WeatherService extends Service {
    public static final String EXTRA_WEATHER_REQUEST_TYPE_KEY = "weather_request_type";
    public static final String EXTRA_WEATHER_LOCATION_KEY = "location";
    public static final int REQUEST_TYPE_CURRENT = 1;
    public static final int REQUEST_TYPE_FORECAST = 2;
    // TAG
    private static final String TAG = "WeatherService";
    private int requestType = -1;
    // binder
    private IBinder binder = new WeatherServiceBinder();
    // callback
    private WeatherCallback weatherCallback;

    public WeatherService() {
    }

    public int getRequestType() {
        return requestType;
    }

    /**
     * Sets the result callback
     *
     * @param weatherCallback {@link WeatherCallback}
     */
    public void setWeatherCallback(WeatherCallback weatherCallback) {
        this.weatherCallback = weatherCallback;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        // get location object
        Location location = intent.getParcelableExtra(EXTRA_WEATHER_LOCATION_KEY);
        // get request requestType
        requestType = intent.getIntExtra(EXTRA_WEATHER_REQUEST_TYPE_KEY, -1);
        if (location != null) {
            // make the request
            makeWeatherRequest(location.getLongitude(), location.getLatitude(), requestType);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Generate api endpoint based on request requestType
     *
     * @param requestType {@link Integer}
     * @param lon         {@link Double}
     * @param lat         {@link Double}
     * @return URL {@link String}
     */
    private String getWeatherEndpoint(final int requestType, final double lon, final double lat) {
        final String apiKey = Application.getInstance().getString(R.string.weather_service_api_key);
        // always return weather as default requestType
        return getResources().getString(R.string.weather_endpoint, requestType == REQUEST_TYPE_FORECAST ?
                "forecast" : "weather", lat, lon, apiKey);
    }

    /**
     * Make api request
     *
     * @param lon         {@link Double}
     * @param lat         {@link Double}
     * @param requestType {@link Integer}
     */
    public void makeWeatherRequest(final double lon, final double lat, final int requestType) {
        // Create request queue
        RequestQueue queue = Volley.newRequestQueue(Application.getInstance());
        // get API url depending on the requestType (0 current weather, 1 forecast weather)
        final String requestUrl = getWeatherEndpoint(requestType, lon, lat);
        // create JsonObjectRequest
        JsonObjectRequest weatherRequest = new JsonObjectRequest(requestUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: " + response.toString());

                // save the response in shared preference in case we lost internet connection.
                SharedPreferencesUtil.getInstance().setStringPreference(Application.getInstance(),
                        requestType == REQUEST_TYPE_CURRENT ? SharedPreferencesUtil.PREF_CURRENT_WEATHER : SharedPreferencesUtil.PREF_FORECAST_WEATHER, response.toString());
                // send UI updates using the callback
                if (weatherCallback != null) {
                    weatherCallback.onWeatherResultReady(response.toString());
                }
                // stop service after finished
                stopSelf();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: " + error.getMessage());
                if (weatherCallback != null) {
                    weatherCallback.onWeatherResultFailed(error.getMessage());
                }
            }
        });
        // accept only request types 1 and 2
        if (requestType == REQUEST_TYPE_CURRENT || requestType == REQUEST_TYPE_FORECAST) {
            queue.add(weatherRequest);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Callback interface for the clients
     */
    public interface WeatherCallback {
        /**
         * Called when weather result is ready
         * @param weatherResult a {@link String} of Json
         */
        void onWeatherResultReady(String weatherResult);

        /**
         * Called when failing to deliver weather result
         * @param errorMessage a {@link String} of Error message
         */
        void onWeatherResultFailed(String errorMessage);
    }

    /**
     * The binder class
     */
    public class WeatherServiceBinder extends Binder {
        /**
         * Get weather service instance
         * @return a {@link WeatherService} instance
         */
        public WeatherService getService() {
            return WeatherService.this;
        }
    }
}
