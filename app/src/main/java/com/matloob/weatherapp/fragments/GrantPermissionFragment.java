package com.matloob.weatherapp.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.matloob.weatherapp.Application;
import com.matloob.weatherapp.R;
import com.matloob.weatherapp.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GrantPermissionFragment extends Fragment {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 123;
    //TAG
    private static final String TAG = "GrantPermissionFragment";
    private static GrantPermissionFragment grantPermissionFragment;
    private MainActivity mainActivity;
    private ArrayList<String> listPermissionsNeeded;

    public GrantPermissionFragment() {
        // Required empty public constructor
    }

    public static GrantPermissionFragment getInstance() {
        if (grantPermissionFragment == null) {
            grantPermissionFragment = new GrantPermissionFragment();
        }
        return grantPermissionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grant_permission, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonGrant = view.findViewById(R.id.button_grant);

        buttonGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allPermissionsGranted()) {
                    requestPermissions(listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                }
            }
        });
    }

    public boolean allPermissionsGranted() {
        int access_fine_location = ContextCompat.checkSelfPermission(Application.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION);
        int access_coarse_location = ContextCompat.checkSelfPermission(Application.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION);

        listPermissionsNeeded = new ArrayList<>();

        if (access_fine_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (access_coarse_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        return listPermissionsNeeded.isEmpty();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();

            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                boolean allGranted = true;
                for (int permission : perms.values()) {
                    if (permission == PackageManager.PERMISSION_DENIED) {
                        allGranted = false;
                    }
                }
                // Check for both permissions
                if (allGranted) {
                    Log.d(TAG, "Permissions granted");

                    // transition to current weather fragment
                    mainActivity.transitionToFragment(CurrentWeatherFragment.getInstance());

                } else {
                    Log.d(TAG, "Some permissions are not granted ask again ");
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        showDialogOK(getResources().getString(R.string.permissions_required_message),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                    } else {
                        explain(getResources().getString(R.string.permissions_required_long_message));
                    }
                }


            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mainActivity)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        new AlertDialog.Builder(mainActivity).setMessage(msg)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", Application.getInstance().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, 123);
                        paramDialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        paramDialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
