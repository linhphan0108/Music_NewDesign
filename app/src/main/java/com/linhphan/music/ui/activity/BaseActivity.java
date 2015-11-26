package com.linhphan.music.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.linhphan.androidboilerplate.util.AppUtil;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.R;
import com.linhphan.music.service.MusicService;
import com.linhphan.music.util.RepeatMode;
import com.linhphan.music.util.UserSetting;
import com.linhphan.music.util.Utils;

/**
 * Created by linhphan on 11/18/15.
 */
public class BaseActivity extends AppCompatActivity {

    protected MusicService mMusicSrv;
    private boolean isServiceBound;
    protected Handler mBaseHandler;

    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (mMusicSrv == null)
                mMusicSrv = ((MusicService.MusicBinder) service).getMusicService();
            mMusicSrv.onBind();
            mMusicSrv.setupHandler(mBaseHandler);
            isServiceBound = true;
            Logger.d(getTag(), "service is connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(getTag(), "service is disconnected");
            isServiceBound = false;
            mMusicSrv.onUnbind();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        boolean isBound = bindService(musicServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        startService(musicServiceIntent);

        if (isBound) {
            Logger.d(getTag(), "binding service return true");
        } else {
            Logger.d(getTag(), "binding service return false");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMusicSrv != null) {
            mMusicSrv.onUnbind();
            unbindService(serviceConnection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    protected String getTag() {
        return getClass().getName();
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
        if (mMusicSrv != null) {
            return mMusicSrv.isPlaying();
        }else{
            return false;
        }
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
