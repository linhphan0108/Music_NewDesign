package com.linhphan.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.linhphan.music.common.AsyncTaskCallback;
import com.linhphan.music.common.ContentManager;
import com.linhphan.music.common.GetDownloadSongLinkWorker;
import com.linhphan.music.common.Logger;
import com.linhphan.music.common.MusicServiceState;
import com.linhphan.music.model.SongModel;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by linhphan on 10/22/15.
 */
public class MusicService extends Service implements AsyncTaskCallback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, AudioManager.OnAudioFocusChangeListener {
    private static MediaPlayer mp;
    private MusicBinder musicBinder;
    private MusicServiceState mServiceState = MusicServiceState.unbound;
    private Handler handler;
    private Thread thread;//the thread which run to get periodically time position of the song
    private CustomRunnable runnable;

    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(getTag(), "the service is started by flag " + String.valueOf(flags));
        if (mp == null) {
            mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mp.setOnPreparedListener(this);
            mp.setOnErrorListener(this);
            mp.setOnCompletionListener(this);
            mp.setOnBufferingUpdateListener(this);
            mp.setOnSeekCompleteListener(this);
        }
        mServiceState = MusicServiceState.idle;
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (musicBinder == null)
            musicBinder = new MusicBinder();
        mServiceState = MusicServiceState.bound;
        Logger.d(getTag(), "the service is bound");
        return musicBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        mServiceState = MusicServiceState.bound;
        Logger.d(getTag(), "the service is rebound");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mServiceState = MusicServiceState.unbound;
        Logger.d(getTag(), "the service is unbound");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mServiceState = MusicServiceState.destroy;
        Logger.d(getTag(), "the service is destroyed");
    }

    //=================== media player callback ====================================================
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mServiceState = MusicServiceState.completed;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Logger.d(getTag(), "media player has been error what:" + String.valueOf(what) + ", extra: " + String.valueOf(extra));
        mServiceState = MusicServiceState.error;
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mServiceState = MusicServiceState.prepared;
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            Toast.makeText(getApplicationContext(), "your audio device has problem", Toast.LENGTH_LONG).show();
        } else {
            mp.start();
            ContentManager contentManager = ContentManager.getInstance();
            contentManager.setDurationAt(contentManager.getCurrentSongPosition(), mp.getDuration());
            retrieveElapseTimePeriodically();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    //================== AsyncTask callback ========================================================
    @Override
    public void onDoingBackground() {
    }

    @Override
    public void onDownloadSuccessfully(ArrayList<SongModel> list) {

    }

    @Override
    public void onDownloadSuccessfully(String url) {
        play(url);
    }

    @Override
    public void onDownloadError(IOException ex, String url) {

    }

    //================== audio manager callback ====================================================
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Logger.e("audio focus", "AUDIOFOCUS_GAIN");
                // resume playback
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                Logger.e("audio focus", "AUDIOFOCUS_LOSS");
//                release();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Logger.e("audio focus", "AUDIOFOCUS_LOSS_TRANSIENT");
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Logger.e("audio focus", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");

                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                mp.setVolume(0.2f, 0.2f);
                break;
        }
    }
    //================== end =======================================================================

    private String getTag() {
        return getClass().getName();
    }

    public void setupHandler(Handler handler){
        this.handler = handler;
    }

    /**
     * plays the song at special position
     *
     * @param position the position of the song in list.
     */
    public void play(int position) {
        ContentManager contentManager = ContentManager.getInstance();
        SongModel songModel = contentManager.getSongAt(position);
        if (songModel != null) {
            (new GetDownloadSongLinkWorker(getApplicationContext(), songModel.getPath(), this)).execute();
            contentManager.setCurrentSongPosition(position);
        }
    }

    private void play(String url) {
        Logger.d(getTag(), "try to play the song at " + url);
        if (url == null) return;

        if (mServiceState == MusicServiceState.playing || mServiceState == MusicServiceState.prepared ||
                mServiceState == MusicServiceState.paused || mServiceState == MusicServiceState.completed) {
            mp.stop();
            mp.reset();
        } else if (mServiceState == MusicServiceState.stopped) {
            mp.reset();
        }

        try {
            mp.setDataSource(getBaseContext(), Uri.parse(url));
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void retrieveElapseTimePeriodically(){
        if (runnable == null)
            runnable = new CustomRunnable();
        thread = new Thread(runnable);
        thread.start();
    }

    private void terminateThread(){
        if (runnable != null)
            runnable.terminate();
    }

    class CustomRunnable implements Runnable {
        private boolean isTerminate = false;
        @Override
        public void run() {
            while (!isTerminate) {
                if (mServiceState == MusicServiceState.playing && handler != null) {
                    Message message = handler.obtainMessage();
                    message.what = mp.getCurrentPosition();
                    handler.sendMessage(message);
                }
            }
        }

        public void terminate(){
            this.isTerminate = true;
        }
    }
}
