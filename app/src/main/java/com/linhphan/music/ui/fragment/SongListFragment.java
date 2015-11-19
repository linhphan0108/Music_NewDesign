package com.linhphan.music.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.linhphan.androidboilerplate.api.JSoupDownloadWorker;
import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.music.R;
import com.linhphan.music.ui.activity.BaseActivity;
import com.linhphan.music.ui.activity.PlayerActivity;
import com.linhphan.music.ui.adapter.SongListAdapter;
import com.linhphan.music.api.parser.JSoupSongListParser;
import com.linhphan.music.util.ContentManager;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.util.DrawerNavigationUtil;
import com.linhphan.music.util.UrlProvider;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.service.MusicService;
import com.linhphan.music.util.Utils;

import java.util.ArrayList;

public class SongListFragment extends BaseFragment implements AbsListView.OnItemClickListener, DownloadCallback {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARGUMENT_KEY_MENU_ITEM_ID = "ARGUMENT_KEY_MENU_ITEM_ID";
    public static final String ARGUMENT_KEY_LAYOUT_RESOURCE_ID = "ARGUMENT_KEY_LAYOUT_RESOURCE_ID";

    private String mUrl;
    private int mCategory;


    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

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
            mCategory = getArguments().getInt(ARGUMENT_KEY_MENU_ITEM_ID, R.id.menu_item_hot_vi);
            layoutResource = getArguments().getInt(ARGUMENT_KEY_LAYOUT_RESOURCE_ID, R.layout.song_item_black_transparent);
        }
        if (mCategory == 0)
            mCategory = R.id.menu_item_hot_vi;//2131493013
        ArrayList<SongModel> songList = ContentManager.getInstance().getSongListByCategory(mCategory);
        if (songList == null || songList.size() <= 0) {
            mUrl = UrlProvider.getUrl(mCategory);
            JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), this);
            worker.setParser(new JSoupSongListParser())
                    .showProgressbar(true, false)
                    .execute(mUrl);
        }
        mAdapter = new SongListAdapter(getActivity(), layoutResource, songList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item, container, false);
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
        int index = DrawerNavigationUtil.getMenuItemPosition(mCategory);
        Utils.putIntToSharedPreferences(getContext(), Utils.SHARED_PREFERENCES_KEY_CURRENT_PLAYING_CATEGORY, index);

        //== go to player activity
        if (!(getActivity() instanceof PlayerActivity)) {//if the hosting activity of this fragment isn't PlayerActivity then go to it
            BaseActivity activity = (BaseActivity) getActivity();
            if (!activity.isMediaPlayerPlaying()) {//if the music is playing then do nothing
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getContext().startActivity(intent);
            }
        }
    }

    private void getWidgets(View root) {
        // Set the adapter
        mListView = (AbsListView) root.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

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
        if (mCategory == ContentManager.getInstance().getCurrentPlayingCategory()) {
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
        ArrayList<SongModel> songList = (ArrayList<SongModel>) data;
        ContentManager contentManager = ContentManager.getInstance();
        contentManager.setCurrentDisplayed(songList, mCategory);
        mAdapter.resetList(songList);
    }

    @Override
    public void onDownloadFailed(Exception e) {

    }
}
