package com.matloob.weatherapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.matloob.weatherapp.fragments.GrantPermissionFragment.allPermissionsGranted;

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
                    selectedFragment = getSupportFragmentManager().findFragmentByTag(CurrentWeatherFragment.class.getSimpleName());
                    if(selectedFragment == null){
                        selectedFragment  = new CurrentWeatherFragment();
                    }
                    break;
                case R.id.navigation_forecast_weather:
                    selectedFragment = getSupportFragmentManager().findFragmentByTag(ForecastWeatherFragment.class.getSimpleName());
                    if(selectedFragment == null){
                        selectedFragment  = new ForecastWeatherFragment();
                    }
                    break;
            }

            transitionToFragment(selectedFragment);
            return true;
        }
    };

    private MainCallback mainCallback;

    /**
     * Broadcast to listen for connectivity changes so that fragments can update
     */
    private BroadcastReceiver mConnectionChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mainCallback != null){
                mainCallback.onConnectionChanged();
            }
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
            if (allPermissionsGranted()) {
                navigation.setVisibility(View.VISIBLE);
                //Manually displaying the first fragment - one Time only
                Fragment currentWeather = getSupportFragmentManager().findFragmentByTag(CurrentWeatherFragment.class.getSimpleName());
                if(currentWeather == null){
                    currentWeather  = new CurrentWeatherFragment();
                }
                transitionToFragment(currentWeather);
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
        registerReceiver(mConnectionChangedReceiver, new IntentFilter(CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mConnectionChangedReceiver);
    }

    private void requestPermissionsIfNeeded() {
        // If permissions not granted, move to grant permissions fragment.
        if (!allPermissionsGranted()) {
            Fragment grantPermissionFragment = getSupportFragmentManager().findFragmentByTag(GrantPermissionFragment.class.getSimpleName());
            if(grantPermissionFragment == null){
                grantPermissionFragment  = new GrantPermissionFragment();
            }
            transitionToFragment(grantPermissionFragment);
            return;
        }
        // Before transitioning to target fragment, check the current fragment.
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        // If current fragment is grant permissions fragment, but all permissions were granted, then make the target fragment CurrentWeatherFragment.
        if (currentFragment instanceof GrantPermissionFragment && allPermissionsGranted()) {
            Fragment currentWeather = getSupportFragmentManager().findFragmentByTag(CurrentWeatherFragment.class.getSimpleName());
            if(currentWeather == null){
                currentWeather  = new CurrentWeatherFragment();
            }
            transitionToFragment(currentWeather);

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigation.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigation.setVisibility(View.GONE);
                }
            });

        }
    }

    /**
     * This function retrieve last location from Google location services
     *
     * @param mainCallback {@link MainCallback} to notify the client
     */
    public void fetchAndSaveLastKnownLocation(final MainCallback mainCallback) {
        this.mainCallback = mainCallback;
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
                            if (mainCallback != null) {
                                mainCallback.onLocationUpdated(location);
                            }
                        }
                    }
                });
    }

    /**
     * Location callback interface for fragments
     */
    public interface MainCallback {
        void onLocationUpdated(Location location);
        void onConnectionChanged();
    }

}
