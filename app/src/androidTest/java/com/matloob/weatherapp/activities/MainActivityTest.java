package com.matloob.weatherapp.activities;

import android.Manifest;
import android.location.Location;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.fragments.CurrentWeatherFragment;
import com.matloob.weatherapp.fragments.ForecastWeatherFragment;
import com.matloob.weatherapp.fragments.GrantPermissionFragment;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Serar Matloob on 9/26/2019.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    // ActivityTestRule launches a given activity before the test starts and closes after the test.
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    // Grant access to permissions by default
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION);
    // MainActivity
    private MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = activityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * This test simulate user click on the bottom nav bar to switch between the two pages
     */
    @Test
    public void testUserSwitchingBetweenFragments() {

        onView(withId(R.id.navigation_current_weather)).perform(click());

        onView(withId(R.id.current_weather_container)).check(matches(isDisplayed()));

        onView(withId(R.id.navigation_forecast_weather)).perform(click());

        onView(withId(R.id.forecast_weather_container)).check(matches(isDisplayed()));
    }

    /**
     * This test make sure that main activity can switch correctly between fragments
     */
    @Test
    public void testInternalSwitchingBetweenFragments() {

        mainActivity.transitionToFragment(new CurrentWeatherFragment());

        onView(withId(R.id.current_weather_container)).check(matches(isDisplayed()));

        mainActivity.transitionToFragment(new ForecastWeatherFragment());

        onView(withId(R.id.forecast_weather_container)).check(matches(isDisplayed()));

        mainActivity.transitionToFragment(new GrantPermissionFragment());

        onView(withId(R.id.grant_permissions_container)).check(matches(isDisplayed()));
    }

    /**
     * This test make sure that Google location API works as expected.
     */
    @Test
    public void testFetchAndSaveLastKnownLocation() {
        SharedPreferencesUtil.getInstance().setStringPreference(Application.getInstance(), SharedPreferencesUtil.PREF_LAST_LONG, null);
        SharedPreferencesUtil.getInstance().setStringPreference(Application.getInstance(), SharedPreferencesUtil.PREF_LAST_LAT, null);
        mainActivity.createLocationRequest();

        // wait 2 seconds for the service to make the api call
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // make sure preferences were updated so that it doesn't equal null.
        // I know that this not the ideal way, I can also mock Location object, but I don't really have time to do it.
        Location location = SharedPreferencesUtil.getInstance().getLastKnownLocation(Application.getInstance());

        assertNotNull(location);
    }

    /**
     * This test make sure snackbar message shows up.
     */
    @Test
    public void A_testShowSnackbar() {
        mainActivity.showConnectionErrorSnackbar("Test Message", null);
        onView(withText("Test Message")).check(matches(isDisplayed()));
    }


}