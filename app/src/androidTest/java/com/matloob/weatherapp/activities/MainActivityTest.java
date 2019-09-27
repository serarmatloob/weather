package com.matloob.weatherapp.activities;

import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.matloob.weatherapp.R;
import com.matloob.weatherapp.TestHelper;
import com.matloob.weatherapp.fragments.CurrentWeatherFragment;
import com.matloob.weatherapp.fragments.ForecastWeatherFragment;
import com.matloob.weatherapp.fragments.GrantPermissionFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Serar Matloob on 9/26/2019.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    // MainActivity
    private MainActivity mainActivity;

    // ActivityTestRule launches a given activity before the test starts and closes after the test.
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION);

    @Before
    public void setUp() throws Exception {
        mainActivity = activityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testUserSwitchingBetweenFragments() {

        onView(withId(R.id.navigation_current_weather)).perform(click());

        onView(withId(R.id.current_weather_container)).check(matches(isDisplayed()));

        onView(withId(R.id.navigation_forecast_weather)).perform(click());

        onView(withId(R.id.forecast_weather_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testInternalSwitchingBetweenFragments() {

        mainActivity.transitionToFragment(GrantPermissionFragment.getInstance());

        onView(withId(R.id.grant_permissions_container)).check(matches(isDisplayed()));

        mainActivity.transitionToFragment(CurrentWeatherFragment.getInstance());

        onView(withId(R.id.current_weather_container)).check(matches(isDisplayed()));

        mainActivity.transitionToFragment(ForecastWeatherFragment.getInstance());

        onView(withId(R.id.forecast_weather_container)).check(matches(isDisplayed()));
    }

}