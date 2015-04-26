package com.example.mehdi.androidexamplegcm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by Mehdi on 4/26/2015.
 */
public class ActivitySplash extends ActionBarActivity {

    String regid;
    static final String TAG = "ActivitySplash";

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();

        startSplash();
    }

    private void startSplash() {

        if (null == Config.SENDER_ID || Config.SENDER_ID.isEmpty()
                || null == Config.SERVER_URL || Config.SERVER_URL.isEmpty()) {

            findViewById(R.id.textView3).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

            return;
        }
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkRegistration();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkRegistration() {
        // Check device for Play Services APK. If check succeeds, proceed with
        if (UtilityCommon.getInstance().checkPlayServices(ActivitySplash.this)) {
            regid = UtilityPreference.getInstance().getRegistrationId(ActivitySplash.this);

            if (regid.isEmpty()) {
                startNewActivity(ActivityRegistration.class);
            } else {
                startNewActivity(ActivityMain.class);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private void startNewActivity(Class ActivityToOpen) {
        Intent intent = new Intent(ActivitySplash.this, ActivityToOpen);
        startActivity(intent);
        finish();
    }
}

