package com.linhphan.music.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by linhphan on 10/22/15.
 */
public class Utils {

    private static String SHARED_PREFERENCES_FILE_NAME = "shared_preferences_common";

    //== keys
    public static String SHARED_PREFERENCES_KEY_CURRENT_PLAYING_CATEGORY = "SHARED_PREFERENCES_KEY_CURRENT_PLAYING_CATEGORY";
    public static String SHARED_PREFERENCES_KEY_REPEAT_MODE = "SHARED_PREFERENCES_KEY_REPEAT_MODE";
    public static String SHARED_PREFERENCES_KEY_SHUFFLE_MODE = "SHARED_PREFERENCES_KEY_SHUFFLE_MODE";


    public static int calculatePercentage(int numerator, int denominator){
        return (numerator * 100)/denominator;
    }

    public synchronized static void putIntToSharedPreferences(Context context, String key, int value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Activity.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

     public synchronized static int  getIntFromSharedPreferences (Context context, String key, int defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void putBooleanToSharedPreferences(Context context, String key, boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Activity.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanFromSharedPreferences(Context context, String key, boolean defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static RepeatMode convertRepeatMode(int value){
        switch (value){
            case 1:
                return RepeatMode.REPEAT_ALL;
            case 2:
                return RepeatMode.REPEAT;
            case 3:
                return RepeatMode.REPEAT_ONE;
            default:
                return RepeatMode.REPEAT_ALL;
        }
    }
}
