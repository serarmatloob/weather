package com.matloob.weatherapp;

import android.util.Log;

/**
 * Created by serar matloob on 9/26/2019
 */
public class Application extends android.app.Application {
    private static final String TAG = "Application";
    private static Application ourInstance = null;

    public static Application getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        ourInstance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate");
    }
}
