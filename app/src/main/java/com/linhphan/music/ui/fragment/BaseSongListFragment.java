package com.linhphan.music.ui.fragment;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.R;
import com.linhphan.music.service.MusicService;
import com.linhphan.music.ui.activity.BaseMusicActivity;
import com.linhphan.music.ui.adapter.SongListAdapter;
import com.linhphan.music.util.ContentManager;

/**
 * Created by linhphan on 2/29/16.
 */
public abstract class BaseSongListFragment extends BaseMusicFragment implements AbsListView.OnItemClickListener {
    /**
     * The fragment's ListView/GridView.
     */
    protected ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    protected SongListAdapter mAdapter;

    //========== overridden methods ================================================================
    @Override
    public void onResume() {
        super.onResume();

        setSelectedItem(ContentManager.getInstance().getCurrentPlayingSongPosition());
    }

    @Override
    protected int getFragmentLayoutResource() {
        return R.layout.fragment_song_list;
    }

    @Override
    protected void getWidgets(View root) {
        // Set the adapter
        mListView = (ListView) root.findViewById(R.id.listView);
        mListView.setEmptyView(root.findViewById(R.id.txt_empty));
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void registerEventHandler() {
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
    }

    //============= implemented methods ============================================================
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaseMusicActivity mainActivity = (BaseMusicActivity) getActivity();
        if (mainActivity == null) return;

        MusicService musicService = mainActivity.getBoundServiceInstance();
        if (musicService == null) return;

        ContentManager contentManager = ContentManager.getInstance();
        contentManager.setupCurrentPlayingFromDisplayed();
        musicService.play(position);
    }

    //=========== inner methods ====================================================================
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
    private boolean isItemVisible(int position) {
        int firstItemVisible = mListView.getFirstVisiblePosition();
        int lastItemVisible = mListView.getChildCount();
        return (position >= firstItemVisible) && (position <= lastItemVisible);
    }


}
