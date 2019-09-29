package com.matloob.weatherapp.fragments;

import android.Manifest;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.activities.MainActivity;
import com.matloob.weatherapp.models.CurrentWeatherModel;
import com.matloob.weatherapp.models.ForecastWeatherModel;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Locale;

import static androidx.core.util.Preconditions.checkNotNull;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Serar Matloob on 9/27/2019.
 */
@RunWith(AndroidJUnit4.class)
public class ForecastWeatherFragmentTest {
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
     * This test check updating UI of forecast page
     */
    @Test
    public void testCurrentForecastSize() {
        // Click on first page of bottom nav bar
        onView(withId(R.id.navigation_forecast_weather)).perform(click());
        // Make sure correct page displayed
        onView(withId(R.id.forecast_weather_container)).check(matches(isDisplayed()));
        // wait 2 seconds for the service to make the api call
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // make sure list is of 5 days size
        onView(withId(R.id.days_recycler_view)).check(matches(hasChildCount(5)));
    }

    /**
     * This test checks the data integrity of forecast page
     */
    @Test
    public void testCurrentForecastData() {
        // Click on first page of bottom nav bar
        onView(withId(R.id.navigation_forecast_weather)).perform(click());
        // Make sure correct page displayed
        onView(withId(R.id.forecast_weather_container)).check(matches(isDisplayed()));
        // wait 2 seconds for the service to make the api call
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // get result from shared preference
        final ForecastWeatherModel forecastWeatherModel = sharedPreferencesUtil.getForecastItem(context);

        // make sure that the data displayed in the 3rd row position of the first day, matches the list we got.
        onView(withIndex(withId(R.id.forecast_recycler), 0)).check(matches(atPosition(3, hasDescendant(withText(getTime(forecastWeatherModel.getList().get(3).getDt()))))));
        // make sure that the data displayed in the 5th row position of the third day, matches the list we got.
        onView(withIndex(withId(R.id.forecast_recycler), 2)).check(matches(atPosition(5, hasDescendant(withText(getTime(forecastWeatherModel.getList().get(5).getDt()))))));

        // make sure that the data displayed in the 1st row of the first day matches the list we got.
        onView(withIndex(withId(R.id.forecast_recycler), 0)).check(matches(atPosition(0, hasDescendant(withText(Application.getInstance().getString(R.string.temperature, (int) forecastWeatherModel.getList().get(0).getMain().getTemp()))))));
    }

    private static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    private static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    private String getTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format("hh:mma", cal).toString();
    }

}