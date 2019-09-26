package com.matloob.weatherapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.fragments.TodayFragment;
import com.matloob.weatherapp.fragments.WeekFragment;

/**
 * Created by serar matloob on 9/26/2019
 */
public class MainActivity extends AppCompatActivity {

    //  BottomNavigationView page selection listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String tag = null;
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_today:
                    selectedFragment = TodayFragment.getInstance();
                    break;
                case R.id.navigation_week:
                    selectedFragment = WeekFragment.getInstance();
                    tag = "week_fragment";
                    break;
            }
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);

            if(currentFragment != null && selectedFragment != null && tag != null){
                Log.i("TAG", "selected fragment tag: "+selectedFragment.getTag());
                Log.i("TAG", "current fragment tag: "+currentFragment.getTag());

                if(!tag.equals(currentFragment.getTag())){
                    getSupportFragmentManager().popBackStack();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                            R.anim.fade_in, R.anim.fade_out);
                    transaction.replace(R.id.frame_layout, selectedFragment, tag);
                    transaction.commit();
                }
            }
            return true;
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initializing navigation view
        BottomNavigationView navigation = findViewById(R.id.navigation);

        // Attaching listener to navigation view
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Manually displaying the first fragment - one Time only
        if(savedInstanceState == null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                    R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.frame_layout, TodayFragment.getInstance(), "today_fragment");
            transaction.commit();
        }
    }
}
