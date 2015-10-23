package com.linhphan.music.common;

import android.os.Build.VERSION_CODES;

/**
 * Created by linhphan on 10/22/15.
 */
public class Utils {

    //== version
    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return android.os.Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }


    public static boolean hasGingerbread() {
        return android.os.Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }


    public static boolean hasHoneycomb() {
        return android.os.Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }


    public static boolean hasHoneycombMR1() {
        return android.os.Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }


    public static boolean hasJellyBean() {
        return android.os.Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }


    public static boolean hasKitKat() {
        return android.os.Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }


    public static boolean isSupportBigNotification(){
        return hasJellyBean();
    }

    /**
     * converters miliseconds to string format
     * @param ms
     * @return a string
     */
    public static String convertTime2String(long ms){
        int hour = (int) (ms / (1000*60*60));
        int minute = (int) ((ms % (1000*60*60)) / (1000*60));
        int second = (int) ((ms % (1000*60*60) % (1000*60)) / 1000);
        String result, h="", m="", s="";


        if (minute<10)
            m = "0"+minute;
        else m = minute+"";


        if (second<10)
            s = "0"+second;
        else s = second +"";


        if(hour <= 0) {
            result = m +":"+ s;
        }else if(hour < 10) {
            h = "0" + hour;
            result = h +":"+ m +":"+ s;
        }else{
            result = h +":"+ m +":"+ s;
        }


        return result;
    }

    public static int calculatePercentage(int numerator, int denominator){
        return (numerator * 100)/denominator;
    }
}
