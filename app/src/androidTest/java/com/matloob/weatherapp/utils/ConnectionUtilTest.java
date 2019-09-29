package com.matloob.weatherapp.utils;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.matloob.weatherapp.Application;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Serar Matloob on 9/28/2019.
 */
@RunWith(AndroidJUnit4.class)
public class ConnectionUtilTest {
    // change this after turning on/off internet from actual device wifi/data before the test
    private final boolean ASSUME_CONNECTED = true;

    @Test
    public void isConnectedToInternet() {
        if (ASSUME_CONNECTED) {
            assertTrue("assuming connected", ConnectionUtil.isConnectedToInternet(Application.getInstance()));
        } else {
            assertFalse("assuming not connected", ConnectionUtil.isConnectedToInternet(Application.getInstance()));
        }
    }
}