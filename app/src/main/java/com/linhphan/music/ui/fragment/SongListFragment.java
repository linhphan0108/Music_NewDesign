package com.linhphan.music.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.linhphan.androidboilerplate.api.JSoupDownloadWorker;
import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.androidboilerplate.ui.fragment.BaseFragment;
import com.linhphan.music.R;
import com.linhphan.music.ui.activity.MainActivity;
import com.linhphan.music.adapter.SongListAdapter;
import com.linhphan.music.api.parser.JSoupSongListParser;
import com.linhphan.music.common.ContentManager;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.common.UrlProvider;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.service.MusicService;

import java.util.ArrayList;

public class SongListFragment extends BaseFragment implements AbsListView.OnItemClickListener, DownloadCallback {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARGUMENT_KEY_MENU_ITEM_ID =  "ARGUMENT_KEY_MENU_ITEM_ID";

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
    private ArrayList<SongModel> mSongList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCategory = getArguments().getInt(ARGUMENT_KEY_MENU_ITEM_ID, R.id.menu_item_hot_vi);
//            if (mCategory != ContentManager.getInstance().getCurrentCategory()) {
//                (new GetSongListWorker(getContext(), mUrl, mCategory, this)).execute();
//            }
        }
        if (mCategory == 0)
            mCategory = R.id.menu_item_hot_vi;//2131493013
        mUrl = UrlProvider.getUrl(mCategory);
        JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), this);
        worker.setParser(new JSoupSongListParser())
                .showProgressbar(true, false)
                .execute(mUrl);
        mAdapter = new SongListAdapter(getActivity(), ContentManager.getInstance().getCurrentDisplayedList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        setSelectedItem(ContentManager.getInstance().getCurrentSongPosition());

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;

        MusicService musicService = mainActivity.getBoundServiceInstance();
        if (musicService == null) return;

        ContentManager contentManager = ContentManager.getInstance();
        contentManager.setupCurrentPlayingSongList();

        musicService.play(position);
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
        mListView.setItemChecked(position, true);
        if (!isItemVisible(position))
            mListView.setSelection(position);
        Logger.d(getTag(), "change the selected item in list view");
    }

    /**
     * determine whether the item in list view is visible or not
     *
     * @param position the position of item in list view which will be determined
     * @return true if the item is visible whereas return false
     */
    public boolean isItemVisible(int position) {
        int firstItemVisible = mListView.getFirstVisiblePosition();
        int lastItemVisible = mListView.getChildCount();
        return (position >= firstItemVisible) && (position <= lastItemVisible);
    }

    //===================== get song list callback
    @Override
    public void onDownloadSuccessfully(Object data) {
        ArrayList<SongModel> songList = (ArrayList<SongModel>) data;
        ContentManager contentManager = ContentManager.getInstance();
        contentManager.setCurrentDisplayedSongList(songList, mCategory);
        mAdapter.resetList(songList);
    }

    @Override
    public void onDownloadFailed(Exception e) {

    }
    //===================== end

}