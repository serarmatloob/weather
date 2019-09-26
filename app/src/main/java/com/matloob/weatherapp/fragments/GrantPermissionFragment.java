package com.matloob.weatherapp.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GrantPermissionFragment extends Fragment {


    public GrantPermissionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grant_permission, container, false);
    }

    private boolean allPermissionsGranted() {
        int access_fine_location = ContextCompat.checkSelfPermission(Application.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION);
        int access_coarse_location = ContextCompat.checkSelfPermission(Application.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION);

        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

        if (access_fine_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (access_coarse_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return listPermissionsNeeded.isEmpty();
    }

}
