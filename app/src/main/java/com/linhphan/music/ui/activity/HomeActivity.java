package com.linhphan.music.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.linhphan.music.R;
import com.linhphan.music.ui.fragment.BaseFragment;
import com.linhphan.music.util.Constants;
import com.linhphan.music.util.ContentManager;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.util.DrawerNavigationUtil;
import com.linhphan.music.util.MessageCode;
import com.linhphan.music.util.Utils;
import com.linhphan.music.ui.fragment.ControllerFragment;
import com.linhphan.music.ui.fragment.SongListFragment;
import com.linhphan.music.data.model.SongModel;

public class HomeActivity extends BaseActivity implements Handler.Callback, ControllerFragment.OnFragmentInteractionListener,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private boolean isNewCreated = false;

    private SongListFragment mContentFragment;
    private ControllerFragment mControllerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();//setup toolbar
        setupNavigationView();
        getAllWidget();
        registerEventHandler();

        mBaseHandler = new Handler(this);

        //open default fragment in the first time the main activity is opened
        if (savedInstanceState == null) {
            int index = Utils.getIntFromSharedPreferences(this, Utils.CURRENT_PLAYING_CATEGORY, 0);
            Bundle bundle = new Bundle();
            bundle.putInt(SongListFragment.ARGUMENT_KEY_MENU_ITEM_ID, DrawerNavigationUtil.getMenuItemId(index));
            mContentFragment = (SongListFragment) BaseFragment.newInstance(SongListFragment.class, bundle);
            openContentFragment(mContentFragment);
            isNewCreated = true;

            Logger.d(getClass().getName(), "selected category index: " + index + " will be resumed");
        }

        FragmentManager fm = getSupportFragmentManager();
        mControllerFragment = ControllerFragment.newInstance();
        fm.beginTransaction().replace(R.id.controllers, mControllerFragment).commit();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //== set selected item in drawer navigation
        int selectedMenuItem;
        if (isNewCreated) {
            int index = Utils.getIntFromSharedPreferences(this, Utils.CURRENT_PLAYING_CATEGORY, 0);
            selectedMenuItem = DrawerNavigationUtil.getMenuItemId(index);
            isNewCreated = false;
        } else {
            selectedMenuItem = ContentManager.getInstance().getCurrentDisplayedCategory();
        }
        if (selectedMenuItem == 0) {
            int index = Utils.getIntFromSharedPreferences(this, Utils.CURRENT_PLAYING_CATEGORY, 0);
            selectedMenuItem = DrawerNavigationUtil.getMenuItemId(index);
        }
        navigationView.setCheckedItem(selectedMenuItem);
        setTitle(DrawerNavigationUtil.getTitle(this, selectedMenuItem));
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
    //==============================================================================================
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawers();

        Bundle bundle = new Bundle();
        bundle.putInt(SongListFragment.ARGUMENT_KEY_MENU_ITEM_ID, menuItem.getItemId());
        mContentFragment = (SongListFragment) BaseFragment.newInstance(SongListFragment.class, bundle);
        openContentFragment(mContentFragment);

        Utils.putIntToSharedPreferences(this, Constants.SELECTED_MENU_ITEM_KEY, menuItem.getItemId());
        setTitle(DrawerNavigationUtil.getTitle(this, menuItem.getItemId()));
        return true;
    }

    //================ ControllerFragment callback =================================================
    @Override
    public void onControlSeekTo(int position) {
        seekTo(position);
    }

    @Override
    public void onControlPlay() {
        play();
    }

    @Override
    public void onControlPaused() {
        pause();
    }

    //========================= handler's callback =================================================
    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MessageCode.SONG_CHANGED.ordinal()) {
            //==set the selected item in list view in ContentFragment
            ContentManager contentManager = ContentManager.getInstance();
            mContentFragment.setSelectedItem(contentManager.getCurrentPlayingSongPosition());

            //==update the displayed title and artist name ControllerFragment
            SongModel currentSong = contentManager.getCurrentPlayingSong();
            mControllerFragment.updateArtistAndTitle(currentSong.getTitle(), currentSong.getArtist());

            //==update the paused or playing button in ControllerFragment
            mControllerFragment.updatePausedOrPlayingButton(false);

            Logger.d(getTag(), "the current song has been changed");

        } else if (msg.what == MessageCode.TIMING.ordinal()) {
            String data = (String) msg.obj;
            if (data != null && !data.isEmpty()) {
                Logger.d(getTag(), "receive data from mHandler " + data);
                String[] arr = data.split("-");
                try {
                    int position = Integer.parseInt(arr[0]);
                    int duration = Integer.parseInt(arr[1]);
                    mControllerFragment.updateSeekBarAndTimer(position, duration);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
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
        return false;
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
                //== set the item is selected
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

    private void openContentFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.main_content, fragment, fragment.getClass().getSimpleName())
                .commit();
    }
}
