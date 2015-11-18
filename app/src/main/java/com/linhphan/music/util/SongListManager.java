package com.linhphan.music.util;


import com.linhphan.music.data.model.SongModel;

import java.util.ArrayList;

/**
 * Created by linh on 08/11/2015.
 */
public class SongListManager {
    private ArrayList<SongModel> mSongList;
    private int mCategory;
    private int mIndex;

    public SongListManager() {
        mSongList = new ArrayList<>();
    }

    public SongListManager(ArrayList<SongModel> mSongList, int mCategory) {
        this.mSongList = mSongList;
        this.mCategory = mCategory;
    }

    public ArrayList<SongModel> getSongList() {
        return mSongList;
    }

    public void setSongList(ArrayList<SongModel> songList) {
        this.mSongList = (songList);
    }

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int category) {
        this.mCategory = category;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public SongModel getSongAt(int index) {
        if (mSongList.size() <= 0 || mSongList.size() <= index)
            return null;
        return mSongList.get(index);
    }

    public SongModel getCurrentSong() {
        if (mSongList.size() <= 0 || mSongList.size() <= mIndex)
            return null;
        return mSongList.get(mIndex);
    }

    public int nextSongPosition() {
        int next;
        if (mIndex + 1 < mSongList.size()) {
            next = mIndex + 1;
        } else {
            next = 0;
        }
        return next;
    }

    public int getPreviousSongPosition() {
        int pre;
        if (mIndex - 1 >= 0) {
            pre = mIndex - 1;
        } else {
            pre = mSongList.size() - 1;
        }
        return pre;
    }
}
