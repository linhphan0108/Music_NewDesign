package com.linhphan.music.service;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.linhphan.music.R;
import com.linhphan.music.activity.MainActivity;
import com.linhphan.music.common.AsyncTaskCallback;
import com.linhphan.music.common.ContentManager;
import com.linhphan.music.common.GetDownloadSongLinkWorker;
import com.linhphan.music.common.Logger;
import com.linhphan.music.common.MessageCode;
import com.linhphan.music.common.MusicServiceState;
import com.linhphan.music.common.Utils;
import com.linhphan.music.model.SongModel;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by linhphan on 10/22/15.
 */
public class MusicService extends Service implements AsyncTaskCallback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, AudioManager.OnAudioFocusChangeListener {
    public static final String MUSIC_SERVICE_BROADCAST_NOTIFICATION = "uit.linh.services";
    final private static int NOTIFY_ID = 11111;
    public static final String NOTIFY_PREVIOUS = "uit.linh.online.music.previous";
    public static final String NOTIFY_REMOVE = "uit.linh.online.music.delete";
    public static final String NOTIFY_PAUSE = "uit.linh.online.music.pause";
    public static final String NOTIFY_PLAY = "uit.linh.online.music.play";
    public static final String NOTIFY_NEXT = "uit.linh.online.music.next";
    public static final String NOTIFY_OPEN_MAIN_ACTIVITY = "uit.linh.online.music.open.main";
    private final NotificationBroadcast notificationBroadcast = new NotificationBroadcast();

    private MediaPlayer mp;
    private MusicBinder musicBinder;
    private MusicServiceState mServiceState;
    private boolean isBound = false;
    private Handler mHandler;
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
            mServiceState = MusicServiceState.idle;
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NOTIFY_PREVIOUS);
        intentFilter.addAction(NOTIFY_PAUSE);
        intentFilter.addAction(NOTIFY_PLAY);
        intentFilter.addAction(NOTIFY_REMOVE);
        intentFilter.addAction(NOTIFY_NEXT);
        intentFilter.addAction(NOTIFY_OPEN_MAIN_ACTIVITY);
        registerReceiver(notificationBroadcast, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (musicBinder == null)
            musicBinder = new MusicBinder();
        isBound = true;
        Logger.d(getTag(), "the service is bound");
        return musicBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        isBound = true;
        Logger.d(getTag(), "the service is rebound");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        terminateThread();
        Logger.d(getTag(), "the service is unbound");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mServiceState = MusicServiceState.destroy;
        unregisterReceiver(notificationBroadcast);
        Logger.d(getTag(), "the service is destroyed");
    }

    //=================== media player callback ====================================================
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (isBound)
            notifyMediaPlayerBuffer(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mServiceState = MusicServiceState.completed;
        next();
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
            mServiceState = MusicServiceState.playing;
            ContentManager contentManager = ContentManager.getInstance();
            contentManager.setDurationAt(contentManager.getCurrentSongPosition(), mp.getDuration());
            retrieveElapseTimePeriodically();
            showCustomNotification(getApplicationContext());
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

    public void setupHandler(Handler handler) {
        this.mHandler = handler;
        ContentManager contentManager = ContentManager.getInstance();
        Logger.d(getTag(), "the current position song: " + contentManager.getCurrentSongPosition());
        retrieveElapseTimePeriodically();
    }

    public void onBind() {
        isBound = true;
    }

    public void onUnbind() {
        isBound = false;
        terminateThread();
    }

    private void notifyCurrentSongHasChanged() {
        Message message = mHandler.obtainMessage();
        message.what = MessageCode.SONG_CHANGED.ordinal();
        mHandler.sendMessage(message);
    }

    private void notifyMediaPlayerTiming(String msg) {
        Message message = mHandler.obtainMessage();
        message.what = MessageCode.TIMING.ordinal();
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    private void notifyMediaPlayerBuffer(int percentage) {
        Message message = mHandler.obtainMessage();
        message.what = MessageCode.BUFFERING.ordinal();
        message.obj = percentage;
        mHandler.sendMessage(message);
    }

    private void notifyMediaPlayerPaused(boolean paused) {
        Message message = mHandler.obtainMessage();
        if (paused)
            message.what = MessageCode.PAUSED.ordinal();
        else message.what = MessageCode.PLAYING.ordinal();
        mHandler.sendMessage(message);
    }

    private void next() {
        ContentManager contentManager = ContentManager.getInstance();
        int nextPosition = contentManager.getNextSong();
        play(nextPosition);
    }

    public void pre() {
        ContentManager contentManager = ContentManager.getInstance();
        int previousPosition = contentManager.getPreviousSong();
        play(previousPosition);
    }

    public void pause() {
        if (mServiceState == MusicServiceState.prepared || mServiceState == MusicServiceState.playing)
            mp.pause();
        mServiceState = MusicServiceState.paused;
        terminateThread();
        notifyMediaPlayerPaused(true);
        showCustomNotification(getApplicationContext());
    }

    private void stop() {
        pause();
        if (mServiceState == MusicServiceState.paused) {
            mp.stop();
            mServiceState = MusicServiceState.stopped;
        }
    }

    public void seekTo(int percentage) {
        if (mServiceState == MusicServiceState.playing || mServiceState == MusicServiceState.prepared ||
                mServiceState == MusicServiceState.paused || mServiceState == MusicServiceState.completed) {
            int duration = mp.getDuration();
            int position = (percentage * duration) / 100;
            mp.seekTo(position);
        } else if (mServiceState == MusicServiceState.stopped) {

        }

    }

    public void play() {
        if (mServiceState == MusicServiceState.paused) {
            mp.start();
            mServiceState = MusicServiceState.playing;
            retrieveElapseTimePeriodically();
            showCustomNotification(getApplicationContext());
        } else {
            ContentManager contentManager = ContentManager.getInstance();
            play(contentManager.getCurrentSongPosition());
        }
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
            notifyCurrentSongHasChanged();
        }
    }

    private void play(String url) {
        Logger.d(getTag(), "try to play the song at " + url);
        Logger.d(getTag(), "try to play the song at " + mServiceState);
        if (url == null) return;

        if (mServiceState == MusicServiceState.playing || mServiceState == MusicServiceState.prepared ||
                mServiceState == MusicServiceState.paused || mServiceState == MusicServiceState.completed) {
            mp.stop();
            mp.reset();

        } else if (mServiceState == MusicServiceState.stopped) {
            mp.reset();

        } else if (mServiceState == MusicServiceState.preparing) {
            return;
        }

        try {
            mp.setDataSource(getBaseContext(), Uri.parse(url));
            mp.prepareAsync();
            mServiceState = MusicServiceState.preparing;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * create a new thread to retrieve the timing of the playing media player
     */
    private void retrieveElapseTimePeriodically() {
        if (runnable == null)
            runnable = new CustomRunnable();
        runnable.terminate(false);
        thread = new Thread(runnable);
        thread.start();
    }

    /**
     * terminate the thread which run to retrieve the timing of the playing media player
     */
    private void terminateThread() {
        if (runnable != null)
            runnable.terminate(true);
    }

    class CustomRunnable implements Runnable {
        private boolean isTerminate = false;

        @Override
        public void run() {
            while (!isTerminate && isBound) {
                try {
                    Logger.d(getTag(), "music state: " + mServiceState);
                    if (mServiceState == MusicServiceState.playing && mHandler != null) {
                        String msg = String.valueOf(mp.getCurrentPosition()) + "-" + String.valueOf(mp.getDuration());
                        notifyMediaPlayerTiming(msg);
                        notifyMediaPlayerPaused(false);
                        Logger.d(getTag(), "send to mHandler: " + String.valueOf(mp.getCurrentPosition()) + "-" + String.valueOf(mp.getDuration()));
                    } else {
                        break;
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void terminate(boolean terminate) {
            this.isTerminate = terminate;
        }
    }

    private void showCustomNotification(Context context) {
        RemoteViews smallView = new RemoteViews(getApplication().getPackageName(), R.layout.small_notificattion);
        RemoteViews bigView = new RemoteViews(getApplication().getPackageName(), R.layout.big_notification);

        ContentManager contentManager = ContentManager.getInstance();
        SongModel currentSong = contentManager.getCurrentSong();

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(currentSong.getTitle()).build();

        if (Utils.isSupportBigNotification()) {
            setListeners(bigView);
            notification.bigContentView = bigView;
            notification.bigContentView.setImageViewResource(R.id.img_notification, R.mipmap.ic_launcher);
            notification.bigContentView.setTextViewText(R.id.txt_title, currentSong.getTitle());
            notification.bigContentView.setTextViewText(R.id.txt_artist, currentSong.getArtist());

            if (mServiceState == MusicServiceState.prepared || mServiceState == MusicServiceState.playing) {
                notification.bigContentView.setViewVisibility(R.id.btn_play, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btn_pause, View.VISIBLE);

            } else {
                notification.bigContentView.setViewVisibility(R.id.btn_play, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btn_pause, View.GONE);
            }

        } else {
            setListeners(smallView);
            notification.contentView = smallView;
            notification.contentView.setImageViewResource(R.id.img_notification, R.mipmap.ic_launcher);
            notification.contentView.setTextViewText(R.id.txt_title, currentSong.getTitle());
            notification.contentView.setTextViewText(R.id.txt_artist, currentSong.getArtist());

            if (mServiceState == MusicServiceState.prepared || mServiceState == MusicServiceState.playing) {
                notification.contentView.setViewVisibility(R.id.btn_play, View.GONE);
                notification.contentView.setViewVisibility(R.id.btn_pause, View.VISIBLE);
            } else {
                notification.contentView.setViewVisibility(R.id.btn_play, View.VISIBLE);
                notification.contentView.setViewVisibility(R.id.btn_pause, View.GONE);
            }
        }

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        startForeground(NOTIFY_ID, notification);

    }

    private void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent remove = new Intent(NOTIFY_REMOVE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);
        Intent openMainActivity = new Intent(NOTIFY_OPEN_MAIN_ACTIVITY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_pre, pPrevious);

        PendingIntent pRemove = PendingIntent.getBroadcast(getApplicationContext(), 0, remove, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_remove, pRemove);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_pause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_next, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_play, pPlay);

        PendingIntent pOpen = PendingIntent.getBroadcast(getApplicationContext(), 0, openMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.img_notification, pOpen);
    }

    public class NotificationBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d(getTag(), "get notification from BroadcastReceiver");
            if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
                KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
                if (keyEvent == null) return;
                if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return;

                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_HEADSETHOOK:
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        break;
                }
            } else {
                if (intent.getAction().equals(MusicService.NOTIFY_PLAY)) {
                    play();
                } else if (intent.getAction().equals(MusicService.NOTIFY_PAUSE)) {
                    pause();
                } else if (intent.getAction().equals(MusicService.NOTIFY_NEXT)) {
                    next();
                } else if (intent.getAction().equals(MusicService.NOTIFY_PREVIOUS)) {
                    pre();
                } else if (intent.getAction().equals(MusicService.NOTIFY_REMOVE)) {
                    stop();
                    stopForeground();
                    MusicService.this.stopSelf();
                    Message message = mHandler.obtainMessage();
                    message.what = MessageCode.DESTROYED.ordinal();
                    mHandler.sendMessage(message);

                } else if (intent.getAction().equals(MusicService.NOTIFY_OPEN_MAIN_ACTIVITY)) {
                    Intent i = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);// close the status bar
                    context.sendBroadcast(i);

                    Intent in = new Intent(context, MainActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(in);
                }
            }
        }
    }

    public void stopForeground() {
        stopForeground(true);
    }
}
