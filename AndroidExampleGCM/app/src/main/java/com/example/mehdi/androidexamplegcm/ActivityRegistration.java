package com.example.mehdi.androidexamplegcm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Mehdi on 4/26/2015.
 */
public class ActivityRegistration extends ActionBarActivity implements View.OnClickListener{

    static final String TAG = "ActivityRegistration";
    private  final int MAX_ATTEMPTS = 5;
    private  final int BACKOFF_MILLI_SECONDS = 2000;
    private  final Random random = new Random();
    String regid, userName, userEmail;

    GoogleCloudMessaging gcm;

    Context context;
    EditText editTextName, editTextEmail;
    Button buttonSignUp;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        context = ActivityRegistration.this;

        editTextName = (EditText) findViewById(R.id.editText_name);
        editTextEmail = (EditText) findViewById(R.id.editText_email);
        findViewById(R.id.button_registration).setOnClickListener(ActivityRegistration.this);

        String userDefaultEmail = UtilityUserEmailFetcher.getEmail(ActivityRegistration.this);
        editTextEmail.setText(userDefaultEmail);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_registration) {
            // registration task
            checkRegistrationParams();
        }
    }

    private void checkRegistrationParams() {

        userName = editTextName.getEditableText().toString();
        userEmail = editTextEmail.getEditableText().toString();
        if (null == userName || null == userEmail || userName.isEmpty() || userEmail.isEmpty()) {
            toast("Both user name and email are required.");
        } else {
            registerInBackground();
        }
    }

    private void toast(String mString) {
        Toast.makeText(ActivityRegistration.this, mString, Toast.LENGTH_LONG).show();
    }

    private void startNewActivity(Class ActivityToOpen) {
        Intent intent = new Intent(ActivityRegistration.this, ActivityToOpen);
        startActivity(intent);
        finish();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setTitle("Registering...");
                pd.setMessage("Please wait.");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Config.SENDER_ID);
                    msg = "" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    register(userName, userEmail, regid);

                    // Persist the regID - no need to register again.
                    UtilityPreference.getInstance().
                            storeRegistrationId(context, regid);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    msg = "Error";
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

                if (pd!=null) {
                    pd.dismiss();
                }

                if (msg.equalsIgnoreCase("Error")) {
                    toast("Registration failed! Please try again later.");
                } else {
                    toast("Registration Successful!");
                    startNewActivity(ActivityMain.class);
                }
            }
        }.execute(null, null, null);
    }

    // Register this account with the server.
    void register(String name, String email, final String regId) {

        Log.i(TAG, "registering device (regId = " + regId + ")");

        String serverUrl = Config.SERVER_URL;

        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("name", name);
        params.put("email", email);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {

            Log.d(TAG, "Attempt #" + i + " to register");

            try {
                //Send Broadcast to Show message on screen
                //mDisplay.append("server_registering" + "\n");
                /*displayMessageOnScreen(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));*/

                // Post registration values to web server
                if (UtilityCheckInternet.getInstance().
                        isConnectingToInternet(ActivityRegistration.this)) {
                    UtilityHttpRequest.post(serverUrl, params);
                } else {
                    //mDisplay.append("internet connection not available" + "\n");
                    return;
                }

                //GCMRegistrar.setRegisteredOnServer(context, true);

                //Send Broadcast to Show message on screen
                //mDisplay.append("server_registered" + "\n");
                /*String message = context.getString(R.string.server_registered);
                displayMessageOnScreen(context, message);*/

                return;
            } catch (IOException e) {

                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).

                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);

                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {

                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);

                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }

                // increase backoff exponentially
                backoff *= 2;
            }
        }

        //Send Broadcast to Show message on screen
        Log.i(TAG, "server_register_error" + "\n");
    }
}
