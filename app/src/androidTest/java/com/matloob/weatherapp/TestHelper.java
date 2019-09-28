package com.matloob.weatherapp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import java.util.List;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/**
 * Created by Serar Matloob on 9/26/2019.
 */
public class TestHelper {

    public static void checkGrantingPermissionsDialogIfNeeded(Activity activity, List<String> permissions, boolean grant) throws Exception {
        for (String permission : permissions) {
            boolean isPermissionGranted = activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            if (!isPermissionGranted) {
                if (Build.VERSION.SDK_INT >= 29) {
                    // interact with dialog assuming allow or deny button names are known.
                    // If first choice does not exist, use alternative button name.
                    interactWithDialog(grant ? "Allow only while using the app" : "Deny", grant ? "Allow" : null);
                    // Wait for dialog
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (Build.VERSION.SDK_INT >= 23) {
                    interactWithDialog(grant ? "Allow" : "Deny", null);
                    // Wait for dialog
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }

    private static void interactWithDialog(String btnName, String alternative) throws Exception {
        // Click ok on the dialog
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject positiveBtn = device.findObject(new UiSelector().text(btnName)).exists() ? device.findObject(new UiSelector().text(btnName)) : device.findObject(new UiSelector().text(btnName.toUpperCase()));
        if (positiveBtn.exists()) {
            try {
                positiveBtn.click();
                Log.i("interactWithDialog", "Clicked " + btnName);
            } catch (UiObjectNotFoundException e) {
                Log.e("interactWithDialog", "There is no dialog or dialog button to interact with " + e);
            }
        } else if (alternative != null) {
            interactWithDialog(alternative, null);
        } else {
            throw new Exception("There is no dialog or dialog button to interact with");
        }
    }


    public static void pressBack() {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressBack();
    }

}
