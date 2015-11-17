package com.linhphan.music.ui.activity;

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
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.linhphan.androidboilerplate.ui.activity.BaseActivity;
import com.linhphan.androidboilerplate.ui.fragment.BaseFragment;
import com.linhphan.music.R;
import com.linhphan.music.util.Constants;
import com.linhphan.music.util.ContentManager;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.util.MessageCode;
import com.linhphan.music.util.Utils;
import com.linhphan.music.ui.fragment.ControllerFragment;
import com.linhphan.music.ui.fragment.SongListFragment;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.service.MusicService;

public class MainActivity extends BaseActivity implements ControllerFragment.OnFragmentInteractionListener,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
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

        mContainerResource = R.id.main_content;

        //open default fragment in the first time the main activity is opened
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(SongListFragment.ARGUMENT_KEY_MENU_ITEM_ID, R.id.menu_item_hot_vi);
            Message message = mBaseHandler.obtainMessage();
            message.what = REPLACING_FRAGMENT;
            mContentFragment = (SongListFragment) BaseFragment.newInstance(SongListFragment.class, bundle);
            message.obj = mContentFragment;
            mBaseHandler.sendMessage(message);

//            mContentFragment = SongListFragment.newInstance(R.id.menu_item_hot_vi);
//            openFragment(mContentFragment);
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

        int selectedMenuItem = Utils.getIntFromSharedPreferences(this, Constants.SELECTED_MENU_ITEM_KEY, R.id.menu_item_hot_vi);
        navigationView.setCheckedItem(selectedMenuItem);
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

        Bundle bundle = new Bundle();
        bundle.putInt(SongListFragment.ARGUMENT_KEY_MENU_ITEM_ID, menuItem.getItemId());
        Message message = mBaseHandler.obtainMessage();
        message.what = REPLACING_FRAGMENT;
        mContentFragment = (SongListFragment) BaseFragment.newInstance(SongListFragment.class, bundle);
        message.obj = mContentFragment;
        mBaseHandler.sendMessage(message);
        Utils.putIntToSharedPreferences(this, Constants.SELECTED_MENU_ITEM_KEY, menuItem.getItemId());
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
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
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
