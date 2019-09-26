package com.matloob.weatherapp.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matloob.weatherapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekFragment extends Fragment {
    private static WeekFragment weekFragment;
    public static WeekFragment getInstance() {
        return weekFragment != null ? weekFragment : new WeekFragment();
    }

    public WeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_week, container, false);
    }

}
