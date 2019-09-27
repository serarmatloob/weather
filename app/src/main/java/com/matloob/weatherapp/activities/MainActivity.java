package com.matloob.weatherapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.fragments.CurrentWeatherFragment;
import com.matloob.weatherapp.fragments.GrantPermissionFragment;
import com.matloob.weatherapp.fragments.ForecastWeatherFragment;

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
            navigation.setVisibility(View.GONE);
            transitionToFragment(GrantPermissionFragment.getInstance());
            return;
        }
        // Before transitioning to target fragment, check the current fragment.
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

        // If current fragment is grant permissions fragment, but all permissions were granted, then make the target fragment CurrentWeatherFragment.
        if (currentFragment instanceof GrantPermissionFragment && GrantPermissionFragment.getInstance().allPermissionsGranted()) {
            transitionToFragment(CurrentWeatherFragment.getInstance());

            // set bottom view nav to visible and set it checked.
            navigation.setVisibility(View.VISIBLE);
            navigation.getMenu().getItem(0).setChecked(true);
        }
    }

    public void transitionToFragment(Fragment targetFragment) {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        // if that's the first time to transition to fragment or the fragment to transition to is different than current fragment, then continue transitioning.
        if (currentFragment == null || !targetFragment.getClass().getSimpleName().equals(currentFragment.getTag())) {
            getSupportFragmentManager().popBackStack();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.frame_layout, targetFragment, targetFragment.getClass().getSimpleName());
            transaction.commit();
        }

        if(!(targetFragment instanceof GrantPermissionFragment)){
            navigation.setVisibility(View.VISIBLE);
        }
    }
}
