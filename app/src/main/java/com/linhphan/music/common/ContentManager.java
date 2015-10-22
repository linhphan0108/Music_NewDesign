package com.linhphan.music.common;

import com.linhphan.music.model.SongModel;

import java.util.ArrayList;

/**
 * Created by linhphan on 10/22/15.
 */
public class ContentManager {
    private static ArrayList<SongModel> mCurrentSongList;
    private static ContentManager mContentManager;
    private int mCurrentSongPosition;//the song's index in mCurrentSongList which is playing
    private int mCurrentTimePosition;

    public ContentManager() {
        mCurrentSongList = new ArrayList<>();
    }

    public static ContentManager getInstance() {
        if (mContentManager == null)
            mContentManager = new ContentManager();
        return mContentManager;
    }

    public ArrayList<SongModel> getCurrentSongList() {
        return mCurrentSongList;
    }

    public void setCurrentSongList(ArrayList<SongModel> list) {
        mCurrentSongList.clear();
        mCurrentSongList.addAll(list);
    }

    public SongModel getSongAt(int position) {
        if (mCurrentSongList == null || mCurrentSongList.size() <= position)
            return null;
        else return mCurrentSongList.get(position);
    }

    /**
     * set the duration of the song in mCurrentSongList at special position
     * @param position
     */
    public void setDurationAt(int position, int duration){
        SongModel songModel = getSongAt(position);
        songModel.setDuration(duration);
    }

    public int getCurrentSongPosition() {
        return mCurrentSongPosition;
    }

    public void setCurrentSongPosition(int mCurrentSongPosition) {
        this.mCurrentSongPosition = mCurrentSongPosition;
    }

    public int getCurrentTimePossition() {
        return mCurrentTimePosition;
    }

    public void setCurrentTimePossition(int mCurrentTimePosition) {
        this.mCurrentTimePosition = mCurrentTimePosition;
    }
}
