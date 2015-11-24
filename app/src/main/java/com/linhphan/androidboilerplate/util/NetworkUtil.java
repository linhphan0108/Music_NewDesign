package com.linhphan.androidboilerplate.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by linhphan on 11/11/15.
 */
public class NetworkUtil {

    public static boolean isNetworkConnected(Context context){
        if (context == null) return false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }
}