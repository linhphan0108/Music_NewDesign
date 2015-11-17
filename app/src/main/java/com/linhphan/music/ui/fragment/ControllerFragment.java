package com.linhphan.music.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.linhphan.androidboilerplate.util.TimerUtil;
import com.linhphan.music.R;
import com.linhphan.music.util.ContentManager;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.util.Utils;
import com.linhphan.music.data.model.SongModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ControllerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControllerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private SeekBar mSeekBar;
    private TextView mTxtDuration;
    private TextView mTxtCurrentTitle;
    private TextView mTxtCurrentArtistName;
    private ImageButton mImgButtonPlay;
    private ImageButton mImgButtonPaused;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ControllerFragment.
     */
    public static ControllerFragment newInstance() {
        ControllerFragment fragment = new ControllerFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_controller, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getWidgets(getView());
        registerEventHandlers();
        getAndDisplayCurrentSongInfo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //=============== click event of views is handled here
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_play:
                mListener.play();
                updatePausedOrPlayingButton(false);
                break;
            case R.id.img_btn_pause:
                mListener.pause();
                updatePausedOrPlayingButton(true);
                break;
        }
    }

    //=============== SeekBar callback handlers ====================================================
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mListener.seekTo(seekBar.getProgress());
    }
    //==============================================================================================


    private void getWidgets(View root) {
        if (root == null) return;
        mSeekBar = (SeekBar) root.findViewById(R.id.sb_loading);
        mTxtDuration = (TextView) root.findViewById(R.id.txt_duration);
        mTxtCurrentTitle = (TextView) root.findViewById(R.id.txt_current_song_title);
        mTxtCurrentArtistName = (TextView) root.findViewById(R.id.txt_current_artist);
        mImgButtonPlay = (ImageButton) root.findViewById(R.id.img_btn_play);
        mImgButtonPaused = (ImageButton) root.findViewById(R.id.img_btn_pause);
    }

    private void registerEventHandlers() {
        mSeekBar.setOnSeekBarChangeListener(this);
        mImgButtonPaused.setOnClickListener(this);
        mImgButtonPlay.setOnClickListener(this);
    }

    private void getAndDisplayCurrentSongInfo(){
        ContentManager contentManager = ContentManager.getInstance();
        SongModel songModel = contentManager.getCurrentSong();
        if (songModel == null) return;
        updateArtistAndTitle(songModel.getTitle(), songModel.getArtist());
    }

    public void updateSeekBarAndTimer(int position, int duration) {
        mSeekBar.setProgress(Utils.calculatePercentage(position, duration));
        mTxtDuration.setText(TimerUtil.convertTime2String(duration));
    }

    public void updateBuffer(int percentage) {
        mSeekBar.setSecondaryProgress(percentage);
        Logger.d(getTag(), "buffering " + percentage);
    }

    public void updateArtistAndTitle(String title, String artistName) {
        mTxtCurrentTitle.setText(title);
        mTxtCurrentArtistName.setText(artistName);
    }

    /**
     * update paused or playing buttons
     *
     * @param paused true if media player is paused
     */
    public void updatePausedOrPlayingButton(boolean paused) {
        if (paused) {
            mImgButtonPaused.setVisibility(View.GONE);
            mImgButtonPlay.setVisibility(View.VISIBLE);
        } else {
            mImgButtonPaused.setVisibility(View.VISIBLE);
            mImgButtonPlay.setVisibility(View.GONE);
        }
    }

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
        void seekTo(int position);

        void play();

        void pause();
    }
}
