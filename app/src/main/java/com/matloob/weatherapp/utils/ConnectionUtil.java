package com.matloob.weatherapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.matloob.weatherapp.R;

/**
 * Created by Serar Matloob on 9/27/2019.
 */

/**
 * This is a helper class to retrieve connection status
 */
public class ConnectionUtil {

    /**
     * Returns weather is connected to internet or not
     *
     * @param context {@link Context}
     * @return a {@link Boolean} value
     */
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        } else {
            NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(NetworkCapabilities.TRANSPORT_WIFI);
            NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(NetworkCapabilities.TRANSPORT_CELLULAR);
            if (wifiNetworkInfo == null && mobileNetworkInfo == null) {
                return false;
            }
            return wifiNetworkInfo != null ? wifiNetworkInfo.isConnected() : mobileNetworkInfo.isConnected();
        }
    }
}
