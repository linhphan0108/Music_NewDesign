package com.linhphan.music.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.linhphan.music.R;
import com.linhphan.music.ui.fragment.BaseMusicFragment;
import com.linhphan.music.ui.fragment.CenterPlayerFragment;
import com.linhphan.music.ui.fragment.LeftPlayerFragment;
import com.linhphan.music.ui.fragment.SongListFragment;
import com.linhphan.music.util.ContentManager;

/**
 * Created by linhphan on 11/18/15.
 */
public class ViewPagerPlayerAdapter extends FragmentPagerAdapter {

    private Fragment mRightPlayerInstance;
    private Fragment mLeftPlayerInstance;
    private Fragment mCentralPlayerInstance;

    public ViewPagerPlayerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                mLeftPlayerInstance = BaseMusicFragment.newInstance(LeftPlayerFragment.class, null);
                return mLeftPlayerInstance;

            case 1:
                mCentralPlayerInstance = BaseMusicFragment.newInstance(CenterPlayerFragment.class, null);
                return mCentralPlayerInstance;

            case 2:
                Bundle bundle = new Bundle();
                bundle.putInt(SongListFragment.ARGUMENT_KEY_MENU_ITEM_ID, ContentManager.getInstance().getCurrentPlayingCategory());
                bundle.putInt(SongListFragment.ARGUMENT_KEY_LAYOUT_RESOURCE_ID, R.layout.song_item_white_solid);
                mRightPlayerInstance =  BaseMusicFragment.newInstance(SongListFragment.class, bundle);
                return mRightPlayerInstance;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public Fragment getRightPlayerFragment(){
        return mRightPlayerInstance;
    }

    public Fragment getLeftPlayerFragment(){
        return mLeftPlayerInstance;
    }

    public Fragment getCentralPlayerInstance(){
        return mCentralPlayerInstance;
    }
}
