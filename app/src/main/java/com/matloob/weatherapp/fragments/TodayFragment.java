package com.matloob.weatherapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matloob.weatherapp.R;

public class TodayFragment extends Fragment {

    private static TodayFragment todayFragment;
    public static TodayFragment getInstance() {
        return todayFragment != null ? todayFragment : new TodayFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

}
