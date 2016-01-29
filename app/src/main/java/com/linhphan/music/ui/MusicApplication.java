package com.linhphan.music.ui;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.service.MusicService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linhphan on 1/29/16.
 */
public class MusicApplication extends Application implements ServiceConnection {
    private MusicService mMusicSrv;
    private final AtomicInteger refCount = new AtomicInteger();

    private Handler mHandler;
    private OnServiceConnection mOnServiceConnectionCallback;

    public MusicApplication() {
        super();
    }

    //=========== overridden methods ===============================================================
    @Override
    public void onCreate() {
        super.onCreate();

        bindService();
    }

    //=========== implemented methods ==============================================================
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (mMusicSrv == null)
            mMusicSrv = ((MusicService.MusicBinder) service).getMusicService();
        mMusicSrv.onBind();
        mMusicSrv.setHandler(mHandler);
        if (mOnServiceConnectionCallback != null) {
            mOnServiceConnectionCallback.onServiceConnected(mMusicSrv);
        }
        Logger.d(getClassTagName(), "onServiceConnected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMusicSrv.onUnbind();
        Logger.d(getClassTagName(), "onServiceDisconnected");
    }

    //========== inner methods =====================================================================
    private void bindService() {
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        bindService(musicServiceIntent, this, BIND_AUTO_CREATE);
        startService(musicServiceIntent);
    }

    public MusicService acquireBinding() {
        bindService();
        refCount.incrementAndGet();
        Logger.d(getClassTagName(), "acquireBinding " + String.valueOf(refCount.get()));
        return mMusicSrv;
    }

    public void releaseBinding() {
        if (refCount.get() == 0 || refCount.decrementAndGet() == 0) {
            // release binding
            if (mMusicSrv != null) {
                mMusicSrv.onUnbind();
//                unbindService(this);
            }
        }
        Logger.d(getClassTagName(), "releaseBinding " + String.valueOf(refCount.get()));
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
        if (mMusicSrv != null) {
            mMusicSrv.setHandler(mHandler);
        }
    }

    public void setOnServiceConnectionCallback(OnServiceConnection callback) {
        mOnServiceConnectionCallback = callback;
    }

    public String getClassTagName() {
        return this.getClass().getName();
    }

    //======== inner classes =======================================================================
    public interface OnServiceConnection {
        void onServiceConnected(MusicService musicService);
    }
}
