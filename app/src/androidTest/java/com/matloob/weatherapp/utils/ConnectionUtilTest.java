package com.matloob.weatherapp.utils;

import com.matloob.weatherapp.Application;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Serar Matloob on 9/28/2019.
 */
public class ConnectionUtilTest {
    // change this after turning on/off internet from actual device wifi/data before the test
    private final boolean ASSUME_CONNECTED = true;

    @Test
    public void isConnectedToInternet() {
        if (ASSUME_CONNECTED) {
            assertTrue(ConnectionUtil.isConnectedToInternet(Application.getInstance()));
        } else {
            assertFalse(ConnectionUtil.isConnectedToInternet(Application.getInstance()));
        }
    }
}