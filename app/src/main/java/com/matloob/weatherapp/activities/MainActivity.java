package com.matloob.weatherapp.activities;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.fragments.CurrentWeatherFragment;
import com.matloob.weatherapp.fragments.ForecastWeatherFragment;
import com.matloob.weatherapp.fragments.GrantPermissionFragment;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

/**
 * Created by Serar Matloob on 9/26/2019
 */
public class MainActivity extends AppCompatActivity {
    // TAG
    private static final String TAG = "MainActivity";

    // BottomNavigationView
    private BottomNavigationView navigation;

    // BottomNavigationView page selection listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_current_weather:
                    selectedFragment = CurrentWeatherFragment.getInstance();
                    break;
                case R.id.navigation_forecast_weather:
                    selectedFragment = ForecastWeatherFragment.getInstance();
                    break;
            }

            transitionToFragment(selectedFragment);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing navigation view
        navigation = findViewById(R.id.navigation);

        // Attaching listener to navigation view
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            if (GrantPermissionFragment.getInstance().allPermissionsGranted()) {
                navigation.setVisibility(View.VISIBLE);
                //Manually displaying the first fragment - one Time only
                transitionToFragment(CurrentWeatherFragment.getInstance());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissionsIfNeeded();
    }

    private void requestPermissionsIfNeeded() {
        // If permissions not granted, move to grant permissions fragment.
        if (!GrantPermissionFragment.getInstance().allPermissionsGranted()) {
            transitionToFragment(GrantPermissionFragment.getInstance());
            return;
        }
        // Before transitioning to target fragment, check the current fragment.
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        // If current fragment is grant permissions fragment, but all permissions were granted, then make the target fragment CurrentWeatherFragment.
        if (currentFragment instanceof GrantPermissionFragment && GrantPermissionFragment.getInstance().allPermissionsGranted()) {
            transitionToFragment(CurrentWeatherFragment.getInstance());

            // set the first view checked.
            navigation.getMenu().getItem(0).setChecked(true);
        }
    }

    /**
     * This is a helper function to move to desired fragment.
     *
     * @param targetFragment {@link Fragment} instance
     */
    public void transitionToFragment(Fragment targetFragment) {
        // get current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        // if that's the first time to transition to fragment or the fragment to transition to is different than current fragment, then continue transitioning.
        if (currentFragment == null || !targetFragment.getClass().getSimpleName().equals(currentFragment.getTag())) {
            getSupportFragmentManager().popBackStack();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.frame_layout, targetFragment, targetFragment.getClass().getSimpleName());
            transaction.commit();
        }
        // Show bottom nav view if fragment was not grant permissions fragment
        if (!(targetFragment instanceof GrantPermissionFragment)) {
            navigation.setVisibility(View.VISIBLE);
        } else {
            navigation.setVisibility(View.GONE);
        }
    }

    /**
     * This function retrieve last location from Google location services
     *
     * @param locationCallback {@link LocationCallback} to notify the client
     */
    public void fetchAndSaveLastKnownLocation(final LocationCallback locationCallback) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Save location in shared preferences.
                            SharedPreferencesUtil.getInstance().setDoublePreference(Application.getInstance(), SharedPreferencesUtil.PREF_LAST_LONG, location.getLongitude());
                            SharedPreferencesUtil.getInstance().setDoublePreference(Application.getInstance(), SharedPreferencesUtil.PREF_LAST_LAT, location.getLatitude());
                            // update callback
                            if (locationCallback != null) {
                                locationCallback.onLocationUpdated(location);
                            }
                        }
                    }
                });
    }

    /**
     * Location callback interface for fragments
     */
    public interface LocationCallback {
        void onLocationUpdated(Location location);
    }

}
