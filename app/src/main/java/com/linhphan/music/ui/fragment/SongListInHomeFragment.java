package com.linhphan.music.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.linhphan.androidboilerplate.api.BaseDownloadWorker;
import com.linhphan.androidboilerplate.api.JSoupDownloadWorker;
import com.linhphan.androidboilerplate.util.ViewUtil;
import com.linhphan.music.R;
import com.linhphan.music.api.parser.JSoupSearchParser;
import com.linhphan.music.ui.activity.BaseMusicActivity;
import com.linhphan.music.ui.activity.HomeActivity;
import com.linhphan.music.ui.activity.PlayerActivity;
import com.linhphan.music.ui.adapter.SongListAdapter;
import com.linhphan.music.api.parser.JSoupSongListParser;
import com.linhphan.music.util.Constants;
import com.linhphan.music.util.ContentManager;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.util.DrawerNavigationUtil;
import com.linhphan.music.util.NoInternetConnectionException;
import com.linhphan.music.util.UrlProvider;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.util.Utils;

import java.util.ArrayList;

public class SongListInHomeFragment extends BaseSongListFragment implements AbsListView.OnScrollListener, SearchView.OnQueryTextListener, BaseDownloadWorker.DownloadCallback {

    public static final String ARGUMENT_KEY_MENU_ITEM_ID = "ARGUMENT_KEY_MENU_ITEM_ID";

    private int mCategoryCode = DrawerNavigationUtil.DEFAULT_CATEGORY_CODE;
    private SearchView mSearchView;
    private String mSearchKey;
    private int mPageSearchIndex = -1;
    private boolean mIsSearchMode = false;

    //used to detect whether the list view is scroll up or down
    private int mListViewCurrentPosInPixel;

    //========== overridden methods ================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCategoryCode = getArguments().getInt(ARGUMENT_KEY_MENU_ITEM_ID, DrawerNavigationUtil.DEFAULT_CATEGORY_CODE);
        }
        ArrayList<SongModel> songList = ContentManager.getInstance().getSongListByCategoryCode(mCategoryCode);
        if (songList == null || songList.size() <= 0) {
            String url = UrlProvider.getUrlFromCategoryCode(mCategoryCode);
            JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), true, this);
            worker.setParser(new JSoupSongListParser())
                    .execute(url);
        }
        mAdapter = new SongListAdapter(getActivity(), R.layout.song_item_black_transparent, songList);

        //perform search view can be implemented in this fragment.
        setHasOptionsMenu(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        // store the playing category
        if (mCategoryCode != DrawerNavigationUtil.SEARCH_CATEGORY_CODE)
            Utils.putIntToSharedPreferences(getContext(), Utils.SHARED_PREFERENCES_KEY_CURRENT_PLAYING_CATEGORY, mCategoryCode);

        //== go to player activity
        if (!(getActivity() instanceof PlayerActivity)) {//if the hosting activity of this fragment isn't PlayerActivity then go to it
            BaseMusicActivity activity = (BaseMusicActivity) getActivity();
            if (!activity.isMediaPlayerPlaying()) {//if the music is playing then do nothing
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                activity.startActivityForResult(intent, Constants.REQUEST_CODE_OPEN_PLAYER_ACTIVITY);
            }
        }
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

    @Override
    protected void init() {

    }

    @Override
    protected void registerEventHandler() {
        super.registerEventHandler();
        mListView.setOnScrollListener(this);
    }

    //============= implemented methods ============================================================
    // get song list callback
    @Override
    public void onSuccessfully(Object data, int requestCode, int responseCode) {
        @SuppressWarnings("unchecked")
        ArrayList<SongModel> songList = (ArrayList<SongModel>) data;
        if (songList == null || songList.size() <= 0) return;
        ContentManager contentManager = ContentManager.getInstance();
        if (mIsSearchMode) {
            ArrayList<SongModel> currentDisplayedList = contentManager.getCurrentDisplayedList();
            int currentDisplayedCategory = contentManager.getCurrentDisplayedCategory();
            if (currentDisplayedList.size() > 0 && currentDisplayedCategory == DrawerNavigationUtil.SEARCH_CATEGORY_CODE) {
                int firstVisiblePosition = mListView.getFirstVisiblePosition();
                contentManager.getCurrentDisplayedList().addAll(songList);
                mAdapter.resetList(contentManager.getCurrentDisplayedList());
                mListView.setSelection(firstVisiblePosition);

            } else {
                contentManager.setCurrentDisplayed(songList, mCategoryCode);
                mAdapter.resetList(songList);
            }

        } else {
            contentManager.setCurrentDisplayed(songList, mCategoryCode);
            mAdapter.resetList(songList);
        }
    }

    @Override
    public void onFailed(Exception e, int requestCode, int responseCode) {
        if (e instanceof NoInternetConnectionException)
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    // search view callbacks
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();

        int currentDisplayedCategory = ContentManager.getInstance().getCurrentDisplayedCategory();
        if (currentDisplayedCategory == DrawerNavigationUtil.SEARCH_CATEGORY_CODE) {
            ContentManager.getInstance().getCurrentDisplayedList().clear();
        }

        mSearchKey = query.trim();
        mPageSearchIndex = 1;
        query = mSearchKey.replace(" ", "+");
//        String url = UrlProvider.SEARCH_PATH + Uri.encode(query) +"&page="+ String.valueOf(mPageSearchIndex);
        String url = UrlProvider.SEARCH_PATH + query + "&page=" + String.valueOf(mPageSearchIndex);
        JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), true, this);
        worker.setParser(new JSoupSearchParser())
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

    // list view's scroll event callback
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mListViewCurrentPosInPixel = getScrollY(view);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mIsSearchMode && totalItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
            mPageSearchIndex++;
            Toast.makeText(getContext(), getContext().getString(R.string.load_more), Toast.LENGTH_SHORT).show();
            String query = mSearchKey.replace(" ", "+");
            String url = UrlProvider.SEARCH_PATH + Uri.encode(query) + "&page=" + String.valueOf(mPageSearchIndex);
            JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), false, this);
            worker.setParser(new JSoupSearchParser())
                    .execute(url);
        }

        int scrollY = getScrollY(view);
        HomeActivity homeActivity = getOwnerActivity();
        if (scrollY - mListViewCurrentPosInPixel > 20) {
            homeActivity.showControlFragment();

        } else if (scrollY - mListViewCurrentPosInPixel < 20){
            homeActivity.hideControlFragment();
        }
    }

    //=========== other methods ====================================================================

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

    private int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null)
            return 0;
        int firstVisiblePosition = view.getFirstVisiblePosition();
        return firstVisiblePosition * c.getHeight();

    }

    private boolean isEndList(AbsListView view, int visibleItemCount, int totalItemCount) {
        final int lastItem = view.getFirstVisiblePosition() + visibleItemCount;
        return lastItem == totalItemCount;
    }

    private boolean isHeader(AbsListView view) {
        final int firstItem = view.getFirstVisiblePosition();
        return firstItem == 0;
    }
}
