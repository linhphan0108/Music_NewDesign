package com.linhphan.music.ui.activity;

import android.animation.Animator;
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
import android.widget.FrameLayout;

import com.linhphan.music.R;
import com.linhphan.music.ui.fragment.BaseMusicFragment;
import com.linhphan.music.util.Constants;
import com.linhphan.music.util.ContentManager;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.util.DrawerNavigationUtil;
import com.linhphan.music.util.MessageCode;
import com.linhphan.music.util.Utils;
import com.linhphan.music.ui.fragment.ControllerFragment;
import com.linhphan.music.ui.fragment.SongListInHomeFragment;
import com.linhphan.music.data.model.SongModel;

public class HomeActivity extends BaseMusicActivity implements Handler.Callback, ControllerFragment.OnFragmentInteractionListener,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FrameLayout mFlControllers;

    private boolean isNewCreated = false;

    private SongListInHomeFragment mContentFragment;
    private ControllerFragment mControllerFragment;


    //=========== overridden methods ===============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupToolbar();//setup toolbar
        setupNavigationView();
        registerEventHandler();

        mBaseHandler = new Handler(this);

        //open default fragment in the first time the main activity is opened
        if (savedInstanceState == null) {
            int categoryCode = Utils.getIntFromSharedPreferences(this, Utils.SHARED_PREFERENCES_KEY_CURRENT_PLAYING_CATEGORY, 0);
            Bundle bundle = new Bundle();
            bundle.putInt(SongListInHomeFragment.ARGUMENT_KEY_MENU_ITEM_ID, categoryCode);
            mContentFragment = (SongListInHomeFragment) BaseMusicFragment.instantiate(this, SongListInHomeFragment.class.getName());
            openContentFragment(mContentFragment);
            isNewCreated = true;

            Logger.d(getClass().getName(), "selected category index: " + categoryCode + " will be resumed");
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
        ContentManager contentManager = ContentManager.getInstance();
        int categoryCode = contentManager.getCurrentDisplayedCategory();
        if (categoryCode == -1){
            categoryCode = contentManager.getCurrentPlayingCategory();
        }
        if (categoryCode == -1){
            categoryCode = Utils.getIntFromSharedPreferences(this, Utils.SHARED_PREFERENCES_KEY_CURRENT_PLAYING_CATEGORY, 0);
        }

        if (categoryCode == DrawerNavigationUtil.SEARCH_CATEGORY_CODE){
            for (int i=0; i<navigationView.getMenu().size(); i++){
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }else {
            selectedMenuItem = DrawerNavigationUtil.getMenuItemId(categoryCode);
            navigationView.setCheckedItem(selectedMenuItem);
        }
        setTitle(DrawerNavigationUtil.getTitle(this, categoryCode));

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            checkToShowOrHideControllerFragment();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_activity, menu);
        return true;
    }

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    protected void getWidgets() {
        super.getWidgets();

        mFlControllers = (FrameLayout) findViewById(R.id.controllers);
    }

    //============= implemented methods ============================================================
    //called is a view is clicked
    @Override
    public void onClick(View v) {

    }

    //called when an item in drawer navigation is selected.
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawers();

        Bundle bundle = new Bundle();
        int categoryCode = DrawerNavigationUtil.getCategoryCode(menuItem.getItemId());
        bundle.putInt(SongListInHomeFragment.ARGUMENT_KEY_MENU_ITEM_ID, categoryCode);
        mContentFragment = (SongListInHomeFragment) BaseMusicFragment.instantiate(this, SongListInHomeFragment.class.getName(), bundle);
        openContentFragment(mContentFragment);

        Utils.putIntToSharedPreferences(this, Constants.SELECTED_MENU_ITEM_KEY, menuItem.getItemId());
        setTitle(DrawerNavigationUtil.getTitle(this, categoryCode));
        return true;
    }

    //ControllerFragment callback
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

    //handler's callback
    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MessageCode.SONG_CHANGED.ordinal()) {
            //==set the selected item in list view in ContentFragment
            if (mContentFragment == null) return false;
            ContentManager contentManager = ContentManager.getInstance();
            mContentFragment.setSelectedItem(contentManager.getCurrentPlayingSongPosition());

            //==update the displayed title and artist name ControllerFragment
            SongModel currentSong = contentManager.getCurrentPlayingSong();
            mControllerFragment.updateArtistAndTitle(currentSong.getTitle(), currentSong.getArtist());

            //==update the paused or playing button in ControllerFragment
            showControlFragment();
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
    //==================== other methods ===========================================================
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

    private void openContentFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.main_content, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    public void hideControlFragment() {
        if (!isCurrentSongNotNull() || Math.abs(mFlControllers.getTranslationY()) < mFlControllers.getHeight()) {
            mFlControllers.animate().cancel();
            mFlControllers.animate()
                    .translationY(mFlControllers.getHeight())
                    .alpha(0)
                    .setDuration(200)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Logger.e(getTag(), "hide translation y : "+ mFlControllers.getTranslationY());
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }

    public void showControlFragment() {
        if (isCurrentSongNotNull() && Math.abs(mFlControllers.getTranslationY()) == mFlControllers.getHeight()) {
            mFlControllers.animate().cancel();
            mFlControllers.animate()
                    .translationY(0)
                    .alpha(100)
                    .setDuration(200)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Logger.e(getTag(), "translation y : "+ mFlControllers.getTranslationY());
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }


    private void checkToShowOrHideControllerFragment(){
        if (isCurrentSongNotNull()){
            showControlFragment();
        }else{
            hideControlFragment();
        }
    }

    public boolean isCurrentSongNotNull(){
        ContentManager contentManager = ContentManager.getInstance();
        SongModel songModel = contentManager.getCurrentPlayingSong();
        if (songModel != null){
            return true;
        }else{
            return false;
        }
    }
}
