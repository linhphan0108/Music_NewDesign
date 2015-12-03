package com.linhphan.music.util;

import com.linhphan.music.data.model.SongModel;

import java.util.ArrayList;

/**
 * Created by linhphan on 10/22/15.
 */
public class ContentManager {
    private static ContentManager mContentManager;

    private SongListManager mCurrentPlaying;
    private SongListManager mCurrentDisplayed;

    public ContentManager() {
        mCurrentPlaying = new SongListManager();
        mCurrentDisplayed = new SongListManager();
    }

    public static ContentManager getInstance() {
        if (mContentManager == null)
            mContentManager = new ContentManager();
        return mContentManager;
    }

    public boolean isCategoryExists(int cat) {
        return cat == mCurrentDisplayed.getCategory() || cat == mCurrentPlaying.getCategory();
    }

    //===================== current displayed =======================================
    public int getCurrentDisplayedCategory() {
        return mCurrentDisplayed.getCategory();
    }

    public void setCurrentDisplayedCategory(int category) {
        this.mCurrentDisplayed.setCategory(category);
    }

    public void setCurrentDisplayedList(ArrayList<SongModel> list) {
        mCurrentDisplayed.setSongList(list);
    }

    public ArrayList<SongModel> getCurrentDisplayedList() {
        return mCurrentDisplayed.getSongList();
    }

    public ArrayList<SongModel> getCurrentDisplayedList(int cat) {
        if (cat == mCurrentDisplayed.getCategory() && mCurrentDisplayed.getSongList().size() > 0)
            return mCurrentDisplayed.getSongList();

        else if (cat == mCurrentPlaying.getCategory() && mCurrentPlaying.getSongList().size() > 0) {
            ArrayList<SongModel> list;
            list = mCurrentPlaying.getSongList();
            mCurrentDisplayed.setCategory(cat);
            mCurrentDisplayed.setSongList(list);
            return list;

        } else
            return null;
    }

    public void setCurrentDisplayed(ArrayList<SongModel> list, int category) {
        mCurrentDisplayed.setSongList(list);
        mCurrentDisplayed.setCategory(category);
    }

    //===================== current playing =======================================
    public int getCurrentPlayingCategory() {
        return mCurrentPlaying.getCategory();
    }

    public void setCurrentPlayingCategory(int category) {
        this.mCurrentPlaying.setCategory(category);
    }

    public void setCurrentPlayingList(ArrayList<SongModel> list) {
        mCurrentPlaying.setSongList(list);
    }

    public ArrayList<SongModel> getCurrentPlayingList() {
        return mCurrentPlaying.getSongList();
    }

    public void setupCurrentPlayingFromDisplayed() {
        if (mCurrentDisplayed.getSongList() != mCurrentPlaying.getSongList()) {
            mCurrentPlaying.setSongList(mCurrentDisplayed.getSongList());
            mCurrentPlaying.setCategory(mCurrentDisplayed.getCategory());
        }
    }

    public SongModel getSongAt(int position) {
        return mCurrentPlaying.getSongAt(position);
    }

    /**
     * set the duration of the song in mCurrentPlayingList at special position
     *
     * @param position
     */
    public void setDurationAt(int position, int duration) {
        SongModel songModel = getSongAt(position);
        songModel.setDuration(duration);
    }

    public int getCurrentPlayingSongPosition() {
        return mCurrentPlaying.getIndex();
    }

    public SongModel getCurrentPlayingSong() {
        if (mCurrentPlaying != null) {
            return mCurrentPlaying.getCurrentSong();
        }else{
            return null;
        }
    }

    public void setDirectlyDownloadPathToCurrentPlayingSong(String[] arr){
        SongModel current = getCurrentPlayingSong();
        current.setDirectlyDownloadPath(arr);
    }

    public String[] getDirectlyDownloadPathToCurrentPlayingSong(){
        SongModel current = getCurrentPlayingSong();
        return current.getDirectlyDownloadPath();
    }

    public String getCurrentPlayingSongTitle(){
        SongModel currentSong = getCurrentPlayingSong();
        if (currentSong != null){
            return currentSong.getTitle();
        }else{
            return null;
        }
    }

    public String getCurrentPlayingSongPath(){
        SongModel currentSong = getCurrentPlayingSong();
        if (currentSong != null){
            return currentSong.getPath();
        }else{
            return null;
        }
    }

    public int getNextSong() {
        return mCurrentPlaying.nextSongPosition();
    }

    public int getRandom(){
        return mCurrentPlaying.getRandom();
    }

    public int getPreviousSong() {
        return mCurrentPlaying.getPreviousSongPosition();
    }

    public void setCurrentSongPosition(int index) {
        this.mCurrentPlaying.setIndex(index);
    }

    public ArrayList<SongModel> getSongListByCategoryCode(int categoryCode){
        if (categoryCode == DrawerNavigationUtil.CURRENT_CATEGORY_CODE){
            if (mCurrentPlaying.getSongList().size() > 0){
                return mCurrentPlaying.getSongList();
            }else{
                return mCurrentDisplayed.getSongList();
            }
        }

        if (categoryCode == mCurrentDisplayed.getCategory()){
            return mCurrentDisplayed.getSongList();
        }

        if (categoryCode == mCurrentPlaying.getCategory()){
            return mCurrentPlaying.getSongList();
        }
        return new ArrayList<>();
    }
}
