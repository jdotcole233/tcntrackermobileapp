package com.example.khoby.tcntracker.NetworkFiles;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TonTrackerNetworkService {


    public static boolean isNetworkConnectionAvailable(Context context){
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            isConnected = true;
        }
        return isConnected;
    }
}
