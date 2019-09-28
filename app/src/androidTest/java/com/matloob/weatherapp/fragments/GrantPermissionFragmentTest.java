package com.matloob.weatherapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.matloob.weatherapp.R;
import com.matloob.weatherapp.TestHelper;
import com.matloob.weatherapp.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Serar Matloob on 9/26/2019.
 */
@RunWith(AndroidJUnit4.class)
public class GrantPermissionFragmentTest {
    // ActivityTestRule launches a given activity before the test starts and closes after the test.
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    // MainActivity
    private MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = activityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    /*
   This test if ran separately, it will clear up all 4 permissions before the test using gradle task "revokeAllPermissions"
   defined in build.gradle file
 */
    @Test
    public void can_request_and_grant_permissions() throws Exception {
        if (!getRequiredPermissionsList().isEmpty()) {
            onView(withId(R.id.button_grant)).perform(click());
            TestHelper.checkGrantingPermissionsDialogIfNeeded(mainActivity, getRequiredPermissionsList(), true);
            onView(withId(R.id.current_weather_container)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void can_request_and_deny_permissions() throws Exception {
        if (!getRequiredPermissionsList().isEmpty()) {
            onView(withId(R.id.button_grant)).perform(click());
            TestHelper.checkGrantingPermissionsDialogIfNeeded(mainActivity, getRequiredPermissionsList(), false);
            onView(withId(R.id.grant_permissions_container)).check(matches(isDisplayed()));
        }
    }

    private List<String> getRequiredPermissionsList() {
        int access_coarse_location = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int access_fine_location = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (access_coarse_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (access_fine_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        return listPermissionsNeeded;
    }
}