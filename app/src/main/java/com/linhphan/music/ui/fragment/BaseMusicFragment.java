package com.linhphan.music.ui.fragment;

import android.content.Context;
import android.os.Handler;

import com.linhphan.androidboilerplate.ui.fragment.BaseFragment;
import com.linhphan.music.ui.activity.BaseMusicActivity;
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
}
