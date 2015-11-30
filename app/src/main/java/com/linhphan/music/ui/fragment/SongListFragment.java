package com.linhphan.music.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linhphan.androidboilerplate.api.JSoupDownloadWorker;
import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.androidboilerplate.util.ViewUtil;
import com.linhphan.music.R;
import com.linhphan.music.api.parser.JSoupSearchParser;
import com.linhphan.music.ui.activity.BaseActivity;
import com.linhphan.music.ui.activity.PlayerActivity;
import com.linhphan.music.ui.adapter.SongListAdapter;
import com.linhphan.music.api.parser.JSoupSongListParser;
import com.linhphan.music.util.ContentManager;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.util.DrawerNavigationUtil;
import com.linhphan.music.util.NoInternetConnectionException;
import com.linhphan.music.util.UrlProvider;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.service.MusicService;
import com.linhphan.music.util.Utils;

import java.util.ArrayList;

public class SongListFragment extends BaseFragment implements AbsListView.OnItemClickListener, DownloadCallback, SearchView.OnQueryTextListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARGUMENT_KEY_MENU_ITEM_ID = "ARGUMENT_KEY_MENU_ITEM_ID";
    public static final String ARGUMENT_KEY_LAYOUT_RESOURCE_ID = "ARGUMENT_KEY_LAYOUT_RESOURCE_ID";

    private int mCategoryCode = DrawerNavigationUtil.DEFAULT_CATEGORY_CODE;
    private SearchView mSearchView;
    private String mSearchKey;
    private int mPageSearchIndex = -1;
    private boolean mIsSearchMode = false;


    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SongListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutResource = R.layout.song_item_black_transparent;
        if (getArguments() != null) {
            mCategoryCode = getArguments().getInt(ARGUMENT_KEY_MENU_ITEM_ID, DrawerNavigationUtil.DEFAULT_CATEGORY_CODE);
            layoutResource = getArguments().getInt(ARGUMENT_KEY_LAYOUT_RESOURCE_ID, R.layout.song_item_black_transparent);
        }
        ArrayList<SongModel> songList = ContentManager.getInstance().getSongListByCategory(mCategoryCode);
        if (songList == null || songList.size() <= 0) {
            String url = UrlProvider.getUrlFromCategoryCode(mCategoryCode);
            JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), this);
            worker.setParser(new JSoupSongListParser())
                    .showProgressbar(true, false)
                    .execute(url);
        }
        mAdapter = new SongListAdapter(getActivity(), layoutResource, songList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to access action bar's menu
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getWidgets(getView());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaseActivity mainActivity = (BaseActivity) getActivity();
        if (mainActivity == null) return;

        MusicService musicService = mainActivity.getBoundServiceInstance();
        if (musicService == null) return;

        ContentManager contentManager = ContentManager.getInstance();
        contentManager.setupCurrentPlayingFromDisplayed();
        musicService.play(position);

        // store the playing category
        if (mCategoryCode != DrawerNavigationUtil.SEARCH_CATEGORY_CODE)
            Utils.putIntToSharedPreferences(getContext(), Utils.SHARED_PREFERENCES_KEY_CURRENT_PLAYING_CATEGORY, mCategoryCode);

        //== go to player activity
        if (!(getActivity() instanceof PlayerActivity)) {//if the hosting activity of this fragment isn't PlayerActivity then go to it
            BaseActivity activity = (BaseActivity) getActivity();
            if (!activity.isMediaPlayerPlaying()) {//if the music is playing then do nothing
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getContext().startActivity(intent);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        if (menuItem != null) {
            mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
            mSearchView.setQueryHint("Enter something here");
            mSearchView.setOnQueryTextListener(this);
            ViewUtil.hideKeyBoard(getActivity());
        }
    }

    private void getWidgets(View root) {
        // Set the adapter
        mListView = (ListView) root.findViewById(R.id.listView);
        mListView.setEmptyView(root.findViewById(R.id.txt_empty));
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mIsSearchMode && totalItemCount > 0  && firstVisibleItem + visibleItemCount == totalItemCount){
                    mPageSearchIndex++;
                    Toast.makeText(getContext(), "load more at page "+ mPageSearchIndex, Toast.LENGTH_SHORT).show();
                    String query = mSearchKey.replace(" ", "+");
                    String url = UrlProvider.SEARCH_PATH + Uri.encode(query) +"&page="+ String.valueOf(mPageSearchIndex);
                    JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), SongListFragment.this);
                    worker.showProgressbar(false, false)
                            .setParser(new JSoupSearchParser())
                            .execute(url);
                }
            }
        });

        setSelectedItem(ContentManager.getInstance().getCurrentPlayingSongPosition());
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public void setSelectedItem(int position) {
        if (mCategoryCode == ContentManager.getInstance().getCurrentPlayingCategory()) {
            mListView.setItemChecked(position, true);
            if (!isItemVisible(position))
                mListView.setSelection(position);
            Logger.d(getTag(), "change the selected item in list view");
        }
    }

    /**
     * determine whether the item in list view is visible or not
     *
     * @param position the position of item in list view which will be determined
     * @return true if the item is visible whereas return false
     */
    private boolean isItemVisible(int position) {
        int firstItemVisible = mListView.getFirstVisiblePosition();
        int lastItemVisible = mListView.getChildCount();
        return (position >= firstItemVisible) && (position <= lastItemVisible);
    }

    //===================== get song list callback =================================================
    @Override
    public void onDownloadSuccessfully(Object data) {
        @SuppressWarnings("unchecked")
        ArrayList<SongModel> songList = (ArrayList<SongModel>) data;
        if (songList == null || songList.size() <= 0) return;
        ContentManager contentManager = ContentManager.getInstance();
        if (mIsSearchMode){
            ArrayList<SongModel> currentDisplayedList = contentManager.getCurrentDisplayedList();
            if (currentDisplayedList.size() > 0){
                int firstVisiblePosition = mListView.getFirstVisiblePosition();
                contentManager.getCurrentDisplayedList().addAll(songList);
                mAdapter.resetList(contentManager.getCurrentDisplayedList());
                mListView.setSelection(firstVisiblePosition);

            }else{
                contentManager.setCurrentDisplayed(songList, mCategoryCode);
                mAdapter.resetList(songList);
            }

        }else {
            contentManager.setCurrentDisplayed(songList, mCategoryCode);
            mAdapter.resetList(songList);
        }
    }

    @Override
    public void onDownloadFailed(Exception e) {
        if (e instanceof NoInternetConnectionException)
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    //======================= search view callbacks ================================================
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();
        mSearchKey = query.trim();
        mPageSearchIndex = 1;
        query = mSearchKey.replace(" ", "+");
        String url = UrlProvider.SEARCH_PATH + Uri.encode(query) +"&page="+ String.valueOf(mPageSearchIndex);
        JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), this);
        worker.showProgressbar(true, false)
                .setParser(new JSoupSearchParser())
             .execute(url);
        mCategoryCode = DrawerNavigationUtil.SEARCH_CATEGORY_CODE;
        mIsSearchMode = true;
        mSearchView.clearFocus();
        ViewUtil.hideKeyBoard(getActivity());
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
