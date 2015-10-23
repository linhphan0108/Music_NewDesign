package com.linhphan.music.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.linhphan.music.R;
import com.linhphan.music.activity.MainActivity;
import com.linhphan.music.adapter.SongListAdapter;
import com.linhphan.music.common.AsyncTaskCallback;
import com.linhphan.music.common.ContentManager;
import com.linhphan.music.common.GetSongListWorker;
import com.linhphan.music.common.Logger;
import com.linhphan.music.common.MusicCategories;
import com.linhphan.music.common.UrlProvider;
import com.linhphan.music.model.SongModel;
import com.linhphan.music.service.MusicService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class SongListFragment extends Fragment implements AbsListView.OnItemClickListener, AsyncTaskCallback {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "url";

    private String mUrl;
    private MusicCategories mCategory;

    private OnFragmentInteractionListener mListener;

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

    public static SongListFragment newInstance(MusicCategories param1) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

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
            mCategory = (MusicCategories) getArguments().getSerializable(ARG_PARAM1);
            mUrl = UrlProvider.getUrl(mCategory);
            if (mCategory != ContentManager.getInstance().getCurrentCategory()) {
                (new GetSongListWorker(getContext(), mUrl, mCategory, this)).execute();
            }
        }
        mAdapter = new SongListAdapter(getActivity(), ContentManager.getInstance().getCurrentSongList());
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;

        MusicService musicService = mainActivity.getBoundServiceInstance();
        if (musicService == null) return;

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
    public void onDoingBackground() {

    }

    @Override
    public void onDownloadSuccessfully(ArrayList<SongModel> list) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDownloadSuccessfully(String url) {

    }

    @Override
    public void onDownloadError(IOException ex, String url) {

    }
    //===================== end

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

}
