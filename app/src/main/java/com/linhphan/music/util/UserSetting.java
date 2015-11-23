package com.linhphan.music.util;

import android.content.Context;

/**
 * Created by linhphan on 11/23/15.
 */
public class UserSetting {

    private static UserSetting mUserSetting;

    public static UserSetting getInstance(){
        if (mUserSetting == null)
            mUserSetting = new UserSetting();
        return mUserSetting;
    }

    public RepeatMode getRepeatMode(Context context){
        int code = Utils.getIntFromSharedPreferences(context, Utils.SHARED_PREFERENCES_KEY_REPEAT_MODE, RepeatMode.REPEAT_ALL.getValue());
        return convertRepeatMode(code);
    }

    public void setRepeatMode(Context context, RepeatMode repeatMode){
        Utils.putIntToSharedPreferences(context, Utils.SHARED_PREFERENCES_KEY_REPEAT_MODE, RepeatMode.REPEAT.getValue());
    }

    private RepeatMode convertRepeatMode(int code){
        switch (code){
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

    public boolean isShuffle(Context context){
        return Utils.getBooleanFromSharedPreferences(context, Utils.SHARED_PREFERENCES_KEY_SHUFFLE_MODE, false);
    }

    /**
     * the toggle function that retrieve a boolean value of shuffle mode then store a reverted value to shared preferences
     * @return the new value of shuffle mode
     */
    public boolean setShuffleMode(Context context){
        boolean isShuffle = Utils.getBooleanFromSharedPreferences(context, Utils.SHARED_PREFERENCES_KEY_SHUFFLE_MODE, false);
        Utils.putBooleanToSharedPreferences(context, Utils.SHARED_PREFERENCES_KEY_SHUFFLE_MODE, !isShuffle);
        return !isShuffle;
    }
}
