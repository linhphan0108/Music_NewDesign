package com.linhphan.music.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.androidboilerplate.util.TimerUtil;
import com.linhphan.music.R;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.ui.adapter.ViewPagerPlayerAdapter;
import com.linhphan.music.util.ContentManager;
import com.linhphan.music.util.MessageCode;
import com.linhphan.music.util.Utils;

import me.relex.circleindicator.CircleIndicator;

public class PlayerActivity extends BaseActivity implements ViewPager.OnPageChangeListener, Handler.Callback, View.OnClickListener {

    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;
    private SeekBar mSbLoading;
    private TextView mTxtTimer;
    private ImageButton mImgButtonPlay;
    private ImageButton mImgButtonPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getWidgets();
        registerEventHandler();
        setupToolbar();
        setupViewPager();

        mBaseHandler = new Handler(this);

    }

    private void getWidgets() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.tab_indicator);
        mSbLoading = (SeekBar) findViewById(R.id.sb_loading);
        mTxtTimer = (TextView) findViewById(R.id.txt_timer);
        mImgButtonPlay = (ImageButton) findViewById(R.id.img_btn_play);
        mImgButtonPaused = (ImageButton) findViewById(R.id.img_btn_pause);
    }

    private void registerEventHandler() {
        mImgButtonPlay.setOnClickListener(this);
        mImgButtonPaused.setOnClickListener(this);
    }

    private void setupViewPager() {
        ViewPagerPlayerAdapter mAdapter = new ViewPagerPlayerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        mCircleIndicator.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);//open the central fragment
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_sliding, R.anim.animation_sliding_down);
    }

    //========= view pager callback
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //========= end view pager callback

    //== mHandler's callback
    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MessageCode.SONG_CHANGED.ordinal()) {
            //==set the selected item in list view in ContentFragment
            ContentManager contentManager = ContentManager.getInstance();

            //==update the displayed title and artist name ControllerFragment
            SongModel currentSong = contentManager.getCurrentPlayingSong();

            //==update the paused or playing button in ControllerFragment
            updatePausedOrPlayingButton(false);

        } else if (msg.what == MessageCode.TIMING.ordinal()) {
            String data = (String) msg.obj;
            if (data != null && !data.isEmpty()) {
                Logger.d(getTag(), "receive data from mHandler " + data);
                String[] arr = data.split("-");
                try {
                    int position = Integer.parseInt(arr[0]);
                    int duration = Integer.parseInt(arr[1]);
                    mSbLoading.setProgress(Utils.calculatePercentage(position, duration));
                    mTxtTimer.setText(TimerUtil.convertTime2String(position) + "/" + TimerUtil.convertTime2String(duration));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.what == MessageCode.BUFFERING.ordinal()) {
            updateBuffer((Integer) msg.obj);
        } else if (msg.what == MessageCode.PAUSED.ordinal()) {
            updatePausedOrPlayingButton(true);
        } else if (msg.what == MessageCode.PLAYING.ordinal()) {
            updatePausedOrPlayingButton(false);
        } else if (msg.what == MessageCode.DESTROYED.ordinal()) {
            finish();
        }

        return false;
    }

    /**
     * update paused or playing buttons
     *
     * @param paused true if media player is paused
     */
    public void updatePausedOrPlayingButton(boolean paused) {
        if (paused) {
            mImgButtonPaused.setVisibility(View.GONE);
            mImgButtonPlay.setVisibility(View.VISIBLE);
        } else {
            mImgButtonPaused.setVisibility(View.VISIBLE);
            mImgButtonPlay.setVisibility(View.GONE);
        }
    }

    public void updateBuffer(int percentage) {
        mSbLoading.setSecondaryProgress(percentage);
        Logger.d(getTag(), "buffering " + percentage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_play:
                play();
                break;

            case R.id.img_btn_pause:
                pause();
                break;

            case R.id.img_btn_next:
                next();
                break;

            case R.id.img_btn_previous:
                pre();
                break;
        }
    }
}
