package com.linhphan.music.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.service.MusicService;

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
        mMusicSrv.onUnbind();
        unbindService(serviceConnection);
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
}
