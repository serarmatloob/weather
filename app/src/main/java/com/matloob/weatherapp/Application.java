package com.matloob.weatherapp;

/**
 * Created by serar matloob on 9/26/2019
 */
public class Application extends android.app.Application {
    private static Application ourInstance = null;

    public static Application getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
    }
}
