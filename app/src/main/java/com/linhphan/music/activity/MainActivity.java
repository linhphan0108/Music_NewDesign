package com.linhphan.music.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.linhphan.music.R;
import com.linhphan.music.common.ContentManager;
import com.linhphan.music.common.Logger;
import com.linhphan.music.common.MessageCode;
import com.linhphan.music.common.MusicCategories;
import com.linhphan.music.fragment.ControllerFragment;
import com.linhphan.music.fragment.SongListFragment;
import com.linhphan.music.model.SongModel;
import com.linhphan.music.service.MusicService;

public class MainActivity extends AppCompatActivity implements ControllerFragment.OnFragmentInteractionListener,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private SeekBar seekBar;

    private MusicService mMusicSrv;
    private boolean isServiceBound;
    private MyHandler handler = new MyHandler();
    private SongListFragment mContentFragment;
    private ControllerFragment mControllerFragment;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (mMusicSrv == null)
                mMusicSrv = ((MusicService.MusicBinder) service).getMusicService();
            mMusicSrv.onBind();
            mMusicSrv.setupHandler(handler);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();//setup toolbar
        setupNavigationView();
        getAllWidget();
        registerEventHandler();

        //open default fragment in the first time the main activity is opened
        if (savedInstanceState == null) {
            mContentFragment = SongListFragment.newInstance(MusicCategories.VI_HOT);
            openFragment(mContentFragment);
        }

        FragmentManager fm = getSupportFragmentManager();
        mControllerFragment = ControllerFragment.newInstance();
        fm.beginTransaction().replace(R.id.controllers, mControllerFragment).commit();


    }

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //called is a view is clicked
    @Override
    public void onClick(View v) {

    }

    //called when an item in drawer navigation is selected.
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        drawerLayout.closeDrawers();
        SongListFragment fragment = null;
        switch (menuItem.getItemId()) {
            //vietnamese music
            case R.id.menu_item_hot_vi:
                fragment = SongListFragment.newInstance(MusicCategories.VI_HOT);
                break;
            case R.id.menu_item_remix_vi:
                fragment = SongListFragment.newInstance(MusicCategories.VI_REMIX);
                break;
            case R.id.menu_item_rap_vi:
                fragment = SongListFragment.newInstance(MusicCategories.VI_RAP);
                break;
            case R.id.menu_item_country_vi:
                fragment = SongListFragment.newInstance(MusicCategories.VI_COUNTRY);
                break;

                //english music
            case R.id.menu_item_pop_en:
                fragment = SongListFragment.newInstance(MusicCategories.EN_POP);
                break;
            case R.id.menu_item_remix_en:
                fragment = SongListFragment.newInstance(MusicCategories.EN_REMIX);
                break;
            case R.id.menu_item_rap_en:
                fragment = SongListFragment.newInstance(MusicCategories.EN_RAP);
                break;
            case R.id.menu_item_dance_en:
                fragment = SongListFragment.newInstance(MusicCategories.EN_DANCE);
                break;

            //korean music
            case R.id.menu_item_pop_korea:
                fragment = SongListFragment.newInstance(MusicCategories.KOREAN_POP);
                break;
            case R.id.menu_item_remix_korea:
                fragment = SongListFragment.newInstance(MusicCategories.KOREAN_REMIX);
                break;
            case R.id.menu_item_rap_korea:
                fragment = SongListFragment.newInstance(MusicCategories.KOREAN_RAP);
                break;
            case R.id.menu_item_dance_korea:
                fragment = SongListFragment.newInstance(MusicCategories.KOREAN_DANCE);
                break;

            //chinese music
            case R.id.menu_item_pop_china:
                fragment = SongListFragment.newInstance(MusicCategories.CHINESE_POP);
                break;
            case R.id.menu_item_remix_china:
                fragment = SongListFragment.newInstance(MusicCategories.KOREAN_REMIX);
                break;
            case R.id.menu_item_rap_china:
                fragment = SongListFragment.newInstance(MusicCategories.CHINESE_RAP);
                break;
            case R.id.menu_item_dance_china:
                fragment = SongListFragment.newInstance(MusicCategories.CHINESE_DANCE);
                break;

            default:
                return false;
        }

        openFragment(fragment);
        return true;
    }

    //================ ControllerFragment callback =================================================
    @Override
    public void seekTo(int position) {
        mMusicSrv.seekTo(position);
    }

    @Override
    public void play() {
        mMusicSrv.play();
    }

    @Override
    public void pause() {
        mMusicSrv.pause();
    }
    //==============================================================================================


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void getAllWidget() {
    }

    private void registerEventHandler() {
    }

    private void openFragment(SongListFragment fragment) {
        if (fragment == null) return;
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.main_content, fragment).commit();
    }

    private String getTag() {
        return getClass().getName();
    }

    public MusicService getBoundServiceInstance() {
        return mMusicSrv;
    }


    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MessageCode.SONG_CHANGED.ordinal()) {
                //==set the selected item in list view in ContentFragment
                ContentManager contentManager = ContentManager.getInstance();
                mContentFragment.setSelectedItem(contentManager.getCurrentSongPosition());

                //==update the displayed title and artist name ControllerFragment
                SongModel currentSong = contentManager.getCurrentSong();
                mControllerFragment.updateArtistAndTitle(currentSong.getTitle(), currentSong.getArtist());

                //==update the paused or playing button in ControllerFragment
                boolean paused = false;
                mControllerFragment.updatePausedOrPlayingButton(paused);

                Logger.d(getTag(), "the current song has been changed");

            } else if (msg.what == MessageCode.TIMING.ordinal()) {
                String data = (String) msg.obj;
                if (data == null || data.isEmpty()) return;
                Logger.d(getTag(), "receive data from handler " + data);
                String[] arr = data.split("-");
                try {
                    int position = Integer.parseInt(arr[0]);
                    int duration = Integer.parseInt(arr[1]);
                    mControllerFragment.updateSeekBarAndTimer(position, duration);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == MessageCode.BUFFERING.ordinal()) {
                mControllerFragment.updateBuffer((Integer) msg.obj);

            } else if (msg.what == MessageCode.PAUSED.ordinal()) {
                mControllerFragment.updatePausedOrPlayingButton(true);

            } else if (msg.what == MessageCode.PLAYING.ordinal()) {
                mControllerFragment.updatePausedOrPlayingButton(false);

            } else if (msg.what == MessageCode.DESTROYED.ordinal()) {
                finish();
            }
        }
    }
}
