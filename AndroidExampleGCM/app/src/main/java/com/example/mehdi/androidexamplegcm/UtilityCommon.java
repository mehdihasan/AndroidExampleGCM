package com.example.mehdi.androidexamplegcm;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Mehdi on 4/26/2015.
 */
public class UtilityCommon {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static UtilityCommon instance;
    static final String TAG = "UtilityCommon";


    public synchronized static UtilityCommon getInstance()
    {
        if( instance == null ) {
            instance = new UtilityCommon();
        }
        return instance;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
