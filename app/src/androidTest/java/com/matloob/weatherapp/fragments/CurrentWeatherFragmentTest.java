package com.matloob.weatherapp.fragments;

import android.Manifest;
import android.content.Context;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.activities.MainActivity;
import com.matloob.weatherapp.models.CurrentWeatherModel;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Serar Matloob on 9/27/2019.
 */
public class CurrentWeatherFragmentTest {
    // ActivityTestRule launches a given activity before the test starts and closes after the test.
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    // Grant access to permissions by default
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION);

    private SharedPreferencesUtil sharedPreferencesUtil;
    private Context context;

    @Before
    public void setUp() throws Exception {
        sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
        context = Application.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * This test check updating UI of current weather page
     */
    @Test
    public void testCurrentWeatherUI() {
        // Click on first page of bottom nav bar
        onView(withId(R.id.navigation_current_weather)).perform(click());
        // Make sure correct page displayed
        onView(withId(R.id.current_weather_container)).check(matches(isDisplayed()));
        // wait 2 seconds for the service to make the api call
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // get result from shared preference
        final CurrentWeatherModel currentWeatherModel = sharedPreferencesUtil.getWeatherItem(context);
        // check that result matches what's displayed on screen
        // temp
        String expectedTemp = context.getResources().getString(R.string.temperature, (int) currentWeatherModel.getMain().getTemp());
        onView(withId(R.id.current_temp)).check(matches(withText(expectedTemp)));
        // humidity
        String expectedHumidity = context.getResources().getString(R.string.current_humidity, (int) currentWeatherModel.getMain().getHumidity());
        onView(withId(R.id.current_humidity)).check(matches(withText(expectedHumidity)));
        // pressure
        String expectedPressure = context.getResources().getString(R.string.current_pressure, (int) currentWeatherModel.getMain().getPressure());
        onView(withId(R.id.current_pressure)).check(matches(withText(expectedPressure)));
    }
}