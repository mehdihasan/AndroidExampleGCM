package com.example.mehdi.androidexamplegcm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Mehdi on 4/26/2015.
 * Updated at 11/29/2015
 */
public class UtilityCheckInternet {

    private static UtilityCheckInternet instance;

    public synchronized static UtilityCheckInternet getInstance()
    {
        if( instance == null ) {
            instance = new UtilityCheckInternet();
        }
        return instance;
    }

    // Checking for all possible Internet providers
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public boolean isConnectingToInternet(Context mContext){

    	ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }else {
            if (connectivityManager != null) {
                //no inspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        Toast.makeText(mContext,mContext.getString(R.string.please_connect_to_internet),Toast.LENGTH_SHORT).show();
        return false;

    }
}
