package com.linhphan.music.common;

import com.linhphan.music.model.SongModel;

import java.util.ArrayList;

/**
 * Created by linhphan on 10/22/15.
 */
public class ContentManager {
    private ArrayList<SongModel> mCurrentPlayingList;
    private ArrayList<SongModel> mCurrentDisplayedList;
    private static ContentManager mContentManager;
    private int mCurrentSongPosition;//the song's index in mCurrentPlayingList which is playing
    private int mCurrentTimePosition;
    private int mCurrentPlayingCategory;
    private int mCurrentDisplayedCategory;

    public ContentManager() {
        mCurrentPlayingList = new ArrayList<>();
        mCurrentDisplayedList = new ArrayList<>();
    }

    public static ContentManager getInstance() {
        if (mContentManager == null)
            mContentManager = new ContentManager();
        return mContentManager;
    }

    public int getCurrentCategory() {
        return mCurrentPlayingCategory;
    }

    public void setCurrentCategory(int currentCategory) {
        this.mCurrentPlayingCategory = currentCategory;
    }

    public void setmCurrentDisplayedList(ArrayList<SongModel> list){
        mCurrentPlayingList = new ArrayList<>();
        mCurrentPlayingList.addAll(list);
    }


    public ArrayList<SongModel> getCurrentPlayingSongList() {
        return mCurrentPlayingList;
    }

    /**
     * set the current displayed song list to the current displayed song list.
     */
    public void setupCurrentPlayingSongList() {
        if (mCurrentDisplayedList == null || mCurrentDisplayedList.size() <= 0)
            return;
        mCurrentPlayingList = null;
        mCurrentPlayingList = new ArrayList<>();
        mCurrentPlayingCategory = mCurrentDisplayedCategory;
        mCurrentPlayingList.addAll(mCurrentDisplayedList);
    }

    public ArrayList<SongModel> getCurrentDisplayedList(){
        return mCurrentDisplayedList;
    }

    public void setCurrentDisplayedSongList(ArrayList<SongModel> list, int category){
        this.mCurrentDisplayedList = null;
        this.mCurrentDisplayedList = new ArrayList<>();
        this.mCurrentDisplayedCategory = category;
        this.mCurrentDisplayedList.addAll(list);
    }

    public SongModel getSongAt(int position) {
        if (mCurrentPlayingList == null || mCurrentPlayingList.size() <= position)
            return null;
        else return mCurrentPlayingList.get(position);
    }

    /**
     * set the duration of the song in mCurrentPlayingList at special position
     * @param position
     */
    public void setDurationAt(int position, int duration) {
        SongModel songModel = getSongAt(position);
        songModel.setDuration(duration);
    }

    public int getCurrentSongPosition() {
        return mCurrentSongPosition;
    }

    public SongModel getCurrentSong() {
        if (mCurrentPlayingList == null || mCurrentSongPosition >= mCurrentPlayingList.size())
            return null;
        return mCurrentPlayingList.get(mCurrentSongPosition);
    }

    public int getNextSong() {
        int next;
        if (mCurrentSongPosition + 1 < mCurrentPlayingList.size()) {
            next = mCurrentSongPosition + 1;
        } else {
            next = 0;
        }
        return next;
    }

    public int getPreviousSong(){
        int pre;
        if (mCurrentSongPosition -1 >= 0){
            pre = mCurrentSongPosition - 1;
        }else{
            pre = mCurrentPlayingList.size() - 1;
        }
        return pre;
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
