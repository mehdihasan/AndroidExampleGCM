package com.example.mehdi.androidexamplegcm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Mehdi on 4/26/2015.
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

    // Checking for all possible internet providers
    public boolean isConnectingToInternet(Context mContext){

        ConnectivityManager connectivity =
                (ConnectivityManager) mContext.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }
}
