package com.matloob.weatherapp.services;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ServiceTestRule;

import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.models.CurrentWeatherModel;
import com.matloob.weatherapp.models.ForecastWeatherModel;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Serar Matloob on 9/27/2019.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class WeatherServiceTest {
    @Rule
    public final ServiceTestRule serviceRule = new ServiceTestRule();

    @Before
    public void setUp() throws Exception {
        // set saved weather results to null.
        SharedPreferencesUtil.getInstance().getWeatherItem(Application.getInstance());
        SharedPreferencesUtil.getInstance().getForecastItem(Application.getInstance());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCurrentWeatherResults() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent = new Intent(getApplicationContext(), WeatherService.class);
        // expected location coord
        Location location = new Location("provider");
        location.setLongitude(32);
        location.setLatitude(25);

        // Passed data to the service via the Intent.
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", location);
        bundle.putInt(WeatherService.EXTRA_WEATHER_REQUEST_TYPE_KEY, WeatherService.REQUEST_TYPE_CURRENT);
        serviceIntent.putExtras(bundle);

        // Start the service
        Application.getInstance().startService(serviceIntent);

        // Bind the service and grab a reference to the binder.
        IBinder binder = serviceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call public methods on the binder directly.
        WeatherService service = ((WeatherService.WeatherServiceBinder) binder).getService();

        // Verify that the service is working correctly.
        assertThat(service.getRequestType(), is(WeatherService.REQUEST_TYPE_CURRENT));

        // wait 2 seconds for the service to make the api call
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // At this point the forecast should be saved in shared preference
        CurrentWeatherModel currentWeatherModel = SharedPreferencesUtil.getInstance().getWeatherItem(Application.getInstance());
        // make sure the coord in response matches the same location we sent in our API request.
        assertEquals(location.getLatitude(), currentWeatherModel.getCoord().getLat(), 2f);
        assertEquals(location.getLongitude(), currentWeatherModel.getCoord().getLon(), 2f);

        serviceRule.unbindService();
    }

    @Test
    public void testForecastWeatherResults() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent = new Intent(getApplicationContext(), WeatherService.class);
        // expected location coord
        Location location = new Location("provider");
        location.setLongitude(139.0077);
        location.setLatitude(35.0164);

        // Passed data to the service via the Intent.
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", location);
        bundle.putInt(WeatherService.EXTRA_WEATHER_REQUEST_TYPE_KEY, WeatherService.REQUEST_TYPE_FORECAST);
        serviceIntent.putExtras(bundle);

        // Start the service
        Application.getInstance().startService(serviceIntent);

        // Bind the service and grab a reference to the binder.
        IBinder binder = serviceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call public methods on the binder directly.
        WeatherService service = ((WeatherService.WeatherServiceBinder) binder).getService();

        // Verify that the service is working correctly.
        assertThat(service.getRequestType(), is(WeatherService.REQUEST_TYPE_FORECAST));

        // wait 2 seconds for the service to make the api call
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // At this point the forecast should be saved in shared preference
        ForecastWeatherModel forecastWeatherModel = SharedPreferencesUtil.getInstance().getForecastItem(Application.getInstance());

        // Here, if coord doesn't exist in API response, it will return 0.0.
        // So, we first check to see if city exist, then we know that coord exist.
        if (forecastWeatherModel.getCity().getName() != null) {
            // make sure it matches the same location we sent in our API request
            assertEquals(location.getLatitude(), forecastWeatherModel.getCity().getCoord().getLat(), 4f);
            assertEquals(location.getLongitude(), forecastWeatherModel.getCity().getCoord().getLon(), 4f);
        } else {
            // If we got successful response even with no coord object, then we check if the list of 5 days weather exist
            assertNotNull(forecastWeatherModel.getList());
        }
        // unbind from the service
        serviceRule.unbindService();
    }
}