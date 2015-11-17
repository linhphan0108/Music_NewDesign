package com.linhphan.music.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by linhphan on 10/22/15.
 */
public class Utils {

    private static String SHARED_PREFERENCES_FILE_NAME = "shared_preferences_common";

    public static int calculatePercentage(int numerator, int denominator){
        return (numerator * 100)/denominator;
    }

    public static void putIntToSharedPreferences(Context context, String key, int value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Activity.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntFromSharedPreferences(Context context, String key, int defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }
}
