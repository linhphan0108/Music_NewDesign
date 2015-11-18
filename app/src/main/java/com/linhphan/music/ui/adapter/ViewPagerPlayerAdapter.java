package com.linhphan.music.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.linhphan.music.ui.fragment.CenterPlayerFragment;
import com.linhphan.music.ui.fragment.LeftPlayerFragment;
import com.linhphan.music.ui.fragment.RightPlayerFragment;

/**
 * Created by linhphan on 11/18/15.
 */
public class ViewPagerPlayerAdapter extends FragmentPagerAdapter {

    public ViewPagerPlayerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return LeftPlayerFragment.newInstance("0", "1");
            case 1:
                return CenterPlayerFragment.newInstance("0", "1");
            case 2:
                return RightPlayerFragment.newInstance("0", "1");

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
