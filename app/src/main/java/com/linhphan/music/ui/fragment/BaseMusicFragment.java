package com.linhphan.music.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.linhphan.androidboilerplate.ui.fragment.BaseFragment;
import com.linhphan.music.ui.activity.BaseMusicActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by linhphan on 11/18/15.
 */
public abstract class BaseMusicFragment extends BaseFragment {
    protected Handler mBaseHandler;//this mHandler will be gotten from an activity which this fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BaseMusicActivity baseActivity = (BaseMusicActivity) context;
        mBaseHandler = baseActivity.getBaseHandler();
    }

    public static BaseFragment newInstance(Class<?> c, Bundle bundle){
        BaseFragment baseFragment = null;
        try {
            Constructor<?> constructor = c.getConstructors()[0];
            baseFragment = (BaseFragment) constructor.newInstance();
            if (bundle != null){
                baseFragment.setArguments(bundle);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseFragment;
    }
}