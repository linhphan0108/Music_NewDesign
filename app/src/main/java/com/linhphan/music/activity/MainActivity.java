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

import com.linhphan.music.R;
import com.linhphan.music.common.Logger;
import com.linhphan.music.common.UrlProvider;
import com.linhphan.music.fragment.ControllerFragment;
import com.linhphan.music.fragment.SongListFragment;
import com.linhphan.music.service.MusicService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private MusicService musicSrv;
    private boolean isServiceBound;
    public MyHandler handler = new MyHandler();
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (musicSrv == null)
                musicSrv = ((MusicService.MusicBinder) service).getMusicService();
            musicSrv.setupHandler(handler);
            isServiceBound = true;
            Logger.d(getTag(), "service is connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(getTag(), "service is disconnected");
            isServiceBound = false;
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
            openFragment(SongListFragment.newInstance(UrlProvider.getHotMusicViUrl()));
        }

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.controllers, ControllerFragment.newInstance("10")).commit();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        bindService(musicServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        startService(musicServiceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        SongListFragment fragment;
        switch (menuItem.getItemId()) {
            //vietnamese music
            case R.id.menu_item_hot_vi:
                fragment = SongListFragment.newInstance(UrlProvider.getHotMusicViUrl());
                return true;
            case R.id.menu_item_remix_vi:
                return true;
            case R.id.menu_item_rap_vi:
                return true;
            case R.id.menu_item_dance_vi:
                return true;

            //english music
            case R.id.menu_item_pop_en:
                return true;
            case R.id.menu_item_remix_en:
                return true;
            case R.id.menu_item_rap_en:
                return true;
            case R.id.menu_item_dance_en:
                return true;

            //korean music
            case R.id.menu_item_pop_korea:
                return true;
            case R.id.menu_item_remix_korea:
                return true;
            case R.id.menu_item_rap_korea:
                return true;
            case R.id.menu_item_dance_korea:
                return true;

            //chinese music
            case R.id.menu_item_pop_china:
                return true;
            case R.id.menu_item_remix_china:
                return true;
            case R.id.menu_item_rap_china:
                return true;
            case R.id.menu_item_dance_china:
                return true;

            default:
                return false;
        }
    }

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

    public MusicService getBoundServiceInstance(){
        return musicSrv;
    }

    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
        }
    }
}
