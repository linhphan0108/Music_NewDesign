package com.linhphan.music.common;

import android.util.Log;

import com.linhphan.music.BuildConfig;

/**
 * Created by linhphan on 10/22/15.
 */
public class Logger {
    public static void i(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.w(tag, msg);
        }
    }

    public static void d(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable throwable){
        if (BuildConfig.DEBUG){
            Log.e(tag, msg, throwable);
        }
    }
}
