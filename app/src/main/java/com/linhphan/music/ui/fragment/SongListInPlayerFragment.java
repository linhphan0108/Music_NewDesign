package com.linhphan.music.ui.fragment;

import android.os.Bundle;
import com.linhphan.music.R;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.ui.adapter.SongListAdapter;
import com.linhphan.music.util.ContentManager;

import java.util.ArrayList;

/**
 * Created by linhphan on 2/29/16.
 */
public class SongListInPlayerFragment extends BaseSongListFragment{

    //========== overridden methods ================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<SongModel> songList = ContentManager.getInstance().getCurrentPlayingList();
        mAdapter = new SongListAdapter(getActivity(), R.layout.song_item_white_solid, songList);
    }

    @Override
    protected void init() {

    }

    //============= implemented methods ============================================================


    //=========== other methods ====================================================================
}
