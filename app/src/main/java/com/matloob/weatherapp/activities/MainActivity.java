package com.matloob.weatherapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.fragments.CurrentWeatherFragment;
import com.matloob.weatherapp.fragments.ForecastWeatherFragment;
import com.matloob.weatherapp.fragments.GrantPermissionFragment;
import com.matloob.weatherapp.utils.SharedPreferencesUtil;

import static com.matloob.weatherapp.fragments.GrantPermissionFragment.allPermissionsGranted;
import static com.matloob.weatherapp.utils.ConnectionUtil.isConnectedToInternet;

/**
 * Created by Serar Matloob on 9/26/2019
 * <p>
 * Main activity class that mainly hold a single fragment which can be replaced using the bottom navigation view.
 * This activity manage transitioning between fragments based on the conditions (Permissions, Location, updates, etc..)
 */
public class MainActivity extends AppCompatActivity {
    // TAG
    private static final String TAG = "MainActivity";

    // Callback instance
    private MainCallback mainCallback;

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
                    // try to find fragment by tag (The tag is the fragment class name)
                    selectedFragment = getSupportFragmentManager().findFragmentByTag(CurrentWeatherFragment.class.getSimpleName());
                    // if fragment not found, create new one.
                    if (selectedFragment == null) {
                        selectedFragment = new CurrentWeatherFragment();
                    }
                    break;
                case R.id.navigation_forecast_weather:
                    selectedFragment = getSupportFragmentManager().findFragmentByTag(ForecastWeatherFragment.class.getSimpleName());
                    if (selectedFragment == null) {
                        selectedFragment = new ForecastWeatherFragment();
                    }
                    break;
            }
            // Transition to the fragment
            transitionToFragment(selectedFragment);
            return true;
        }
    };

    /**
     * Sets the callback instance to allow communication between activity and fragment
     *
     * @param mainCallback {@link MainCallback}
     */
    public void setMainCallback(MainCallback mainCallback) {
        this.mainCallback = mainCallback;
    }

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
            // If all permissions granted, show the nav bar, then navigate to current weather fragment.
            if (allPermissionsGranted()) {
                navigation.setVisibility(View.VISIBLE);
                //Manually displaying the first fragment
                Fragment currentWeather = getSupportFragmentManager().findFragmentByTag(CurrentWeatherFragment.class.getSimpleName());
                if (currentWeather == null) {
                    currentWeather = new CurrentWeatherFragment();
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
    }

    private void requestPermissionsIfNeeded() {
        // If permissions not granted, move to grant permissions fragment.
        if (!allPermissionsGranted()) {
            Fragment grantPermissionFragment = getSupportFragmentManager().findFragmentByTag(GrantPermissionFragment.class.getSimpleName());
            if (grantPermissionFragment == null) {
                grantPermissionFragment = new GrantPermissionFragment();
            }
            transitionToFragment(grantPermissionFragment);
            return;
        }
        // Before transitioning to target fragment, check the current fragment.
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        // If current fragment is grant permissions fragment, but all permissions were granted, then make the target fragment CurrentWeatherFragment.
        if (currentFragment instanceof GrantPermissionFragment && allPermissionsGranted()) {
            Fragment currentWeather = getSupportFragmentManager().findFragmentByTag(CurrentWeatherFragment.class.getSimpleName());
            if (currentWeather == null) {
                currentWeather = new CurrentWeatherFragment();
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
//            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
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
     * This method initiate location request and ask the user to turn on GPS if off.
     */
    public void createLocationRequest() {
        // Check if connected to internet
        if (isConnectedToInternet(getApplicationContext())) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                    fetchAndSaveLastKnownLocation();
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    LocationRequest.PRIORITY_HIGH_ACCURACY);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        } else {
            // Not connected to internet. Try to show last known weather if available
            Toast.makeText(Application.getInstance(), R.string.connection_error_try_again, Toast.LENGTH_LONG).show();
            fetchAndSaveLastKnownLocation();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(TAG, "onActivityResult: GPS Enabled");
                    // GPS Enabled, fetch location after 3 seconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fetchAndSaveLastKnownLocation();
                        }
                    }, 3000);
                    break;
                case Activity.RESULT_CANCELED:
                    // The user rejected turning on GPS, try to use last known location.
                    Log.i(TAG, "onActivityResult: GPS request rejected");
                    sendLastKnownLocation();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * This function retrieve last location from Google location services
     */
    public void fetchAndSaveLastKnownLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Save location in shared preferences.
                            SharedPreferencesUtil.getInstance().setLastKnownLocation(Application.getInstance(), location);
                            // update callback
                            if (mainCallback != null) {
                                mainCallback.onLocationUpdated(location);
                            }
                        } else {
                            sendLastKnownLocation();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendLastKnownLocation();
            }
        });
    }

    /**
     * Retrieve and send last known location from preference.
     */
    private void sendLastKnownLocation() {
        Location location = SharedPreferencesUtil.getInstance().getLastKnownLocation(getApplicationContext());
        if (location != null && mainCallback != null) {
            Toast.makeText(Application.getInstance(), R.string.location_error, Toast.LENGTH_LONG).show();
            mainCallback.onLocationUpdated(location);
        } else {
            if (mainCallback != null) {
                mainCallback.onLocationUnavailable();
            }
        }
    }

    /**
     * Shows Snackbar to the user with desired message and listener
     *
     * @param message           text
     * @param clickListener     click listener
     */
    public void showConnectionErrorSnackbar(String message, View.OnClickListener clickListener) {
        final Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", clickListener);
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackbar != null)
                    snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    /**
     * Location callback interface for fragments
     */
    public interface MainCallback {
        /**
         * Called when location is updated
         * @param location {@link Location} instance
         */
        void onLocationUpdated(Location location);

        /**
         * Called when location is unavailable
         */
        void onLocationUnavailable();
    }
}
