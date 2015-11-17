package com.linhphan.androidboilerplate.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by linhphan on 11/11/15.
 */
public class AppUtil {

    //== version
    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }


    public static boolean hasGingerbread() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }


    public static boolean hasHoneycomb() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }


    public static boolean hasHoneycombMR1() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }


    public static boolean hasJellyBean() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }


    public static boolean hasKitKat() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }


    public static boolean isSupportBigNotification(){
        return hasJellyBean();
    }


    /**
     * determine whether an application is installed or not
     *
     * @param packageName the full package name
     * @return true if the application is installed whereas return false
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        boolean isInstalled = false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            isInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return isInstalled;
    }

    /**
     * open galleries app
     * if there are many applications are suitable then the OS will open a chooser dialog to pick one
     */
    public static void openGalleryApp(Context context, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    /**
     * try to retrieve the hash key of an application by package name
     */
    private void retrieveHashKey(Context context, String packageName) {
        // Add code to print out the key hash
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName,PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(e);
        } catch (NoSuchAlgorithmException e) {
            Logger.e(e);
        }
    }
}
