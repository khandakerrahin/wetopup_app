package com.example.wetop_up;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.wetop_up.home_activities.HomeActivity;

public class InternetConnection {

    public static boolean connection(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        boolean net = networkInfo != null && networkInfo.isConnected();

        if(!net){
            HomeActivity.netConnect(context);
        }

        return net;
    }

}
