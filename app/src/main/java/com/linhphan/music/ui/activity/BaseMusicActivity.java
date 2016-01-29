package com.linhphan.music.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.linhphan.androidboilerplate.ui.activity.BaseActivity;
import com.linhphan.androidboilerplate.util.AppUtil;
import com.linhphan.music.R;
import com.linhphan.music.service.MusicService;
import com.linhphan.music.ui.MusicApplication;
import com.linhphan.music.util.Constants;
import com.linhphan.music.util.RepeatMode;
import com.linhphan.music.util.UserSetting;

/**
 * Created by linhphan on 11/18/15.
 */
public class BaseMusicActivity extends BaseActivity implements MusicApplication.OnServiceConnection{

    protected MusicService mMusicSrv;
    private MusicApplication musicApplication;
    protected Handler mBaseHandler;

    //=============== overridden methods ===========================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        musicApplication.setOnServiceConnectionCallback(this);
        musicApplication.setHandler(mBaseHandler);
        mMusicSrv = musicApplication.acquireBinding();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        musicApplication.releaseBinding();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESPONSE_CODE_FINISH)
            finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_volume:
                AppUtil.getInstance().openVolumeSystem(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getActivityLayoutResource() {
        return 0;
    }

    @Override
    protected void init() {
        musicApplication = (MusicApplication) getApplication();
    }

    @Override
    protected void getWidgets() {
    }

    @Override
    protected void registerEventHandler() {

    }

    //============ implemented methods =============================================================
    @Override
    public void onServiceConnected(MusicService musicService) {
        mMusicSrv = musicService;
    }

    //============ other methods ===================================================================
    protected String getTag() {
        return this.getClass().getName();
    }

    protected void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }

    public MusicService getBoundServiceInstance() {
        return mMusicSrv;
    }

    public Handler getBaseHandler() {
        return mBaseHandler;
    }

    //=== media player controls
    public void play() {
        if (mMusicSrv != null)
            mMusicSrv.play();
    }

    public void pause() {
        if (mMusicSrv != null)
            mMusicSrv.pause();
    }

    public void next() {
        if (mMusicSrv != null)
            mMusicSrv.next();
    }

    public void pre() {
        if (mMusicSrv != null)
            mMusicSrv.pre();
    }

    public void seekTo(int position) {
        if (mMusicSrv != null)
            mMusicSrv.seekTo(position);
    }

    public boolean isMediaPlayerPlaying() {
        return mMusicSrv != null && mMusicSrv.isPlaying();
    }

    protected RepeatMode onRepeatButtonClicked() {
        UserSetting userSetting = UserSetting.getInstance();
        RepeatMode repeatMode = userSetting.getRepeatMode(this);
        RepeatMode newMode;
        switch (repeatMode) {
            case REPEAT_ALL:
                newMode = RepeatMode.REPEAT;
                break;

            case REPEAT:
                newMode = RepeatMode.REPEAT_ONE;
                break;

            case REPEAT_ONE:
                newMode = RepeatMode.REPEAT_ALL;
                break;

            default:
                newMode = RepeatMode.REPEAT_ALL;
        }

        userSetting.setRepeatMode(this, newMode);
        return newMode;
    }

    /**
     * change the image of repeat button
     *
     * @param isActionbar whether change the repeat button on tool bar
     */
    protected void setupRepeatButton(ImageButton btn, RepeatMode repeatMode, boolean isActionbar) {
        if (btn == null) return;
        switch (repeatMode) {
            case REPEAT_ALL:
                if (isActionbar)
                    btn.setImageResource(R.drawable.ic_action_repeat_all);
                else
                    btn.setImageResource(R.drawable.ic_button_repeat_all);
                break;

            case REPEAT:
                if (isActionbar)
                    btn.setImageResource(R.drawable.ic_action_repeat_disable);
                else
                    btn.setImageResource(R.drawable.ic_button_repeat);
                break;

            case REPEAT_ONE:
                if (isActionbar)
                    btn.setImageResource(R.drawable.ic_action_repeat_one);
                else
                    btn.setImageResource(R.drawable.ic_button_repeat_one);
                break;

            default:
                if (isActionbar)
                    btn.setImageResource(R.drawable.ic_action_repeat_all);
                else
                    btn.setImageResource(R.drawable.ic_button_repeat_all);
                break;
        }
    }

    protected boolean onShuffleButtonClicked() {
        UserSetting userSetting = UserSetting.getInstance();
        return userSetting.setShuffleMode(this);
    }

    protected void setupShuffleButton(ImageButton btn, boolean isShuffle, boolean isActionbar) {
        if (isShuffle) {
            if (isActionbar)
                btn.setImageResource(R.drawable.ic_action_shuffle);
            else
                btn.setImageResource(R.drawable.ic_button_shuffle);
        } else {
            if (isActionbar)
                btn.setImageResource(R.drawable.ic_action_repeat_disable);
            else
                btn.setImageResource(R.drawable.ic_action_shuffle_disable);
        }
    }
}
