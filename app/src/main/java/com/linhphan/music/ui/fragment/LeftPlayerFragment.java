package com.linhphan.music.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.linhphan.androidboilerplate.api.JSoupDownloadWorker;
import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.androidboilerplate.util.ViewUtil;
import com.linhphan.music.R;
import com.linhphan.music.api.parser.JSoupSongInfoParser;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.util.ContentManager;
import com.squareup.picasso.Picasso;

public class LeftPlayerFragment extends BaseFragment implements DownloadCallback {

    private TextView txtTitle, txtArtists, txtComposer, txtAlbum, txtYear, txtViewed, txtDownloaded;
    private EditText edtLyrics;
    private ImageView imgCover;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_left_player, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getWidgets(getView());

        SongModel songModel = ContentManager.getInstance().getCurrentPlayingSong();
        checkAndShowSongInfo(songModel);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //============ AsyncTask download callback =====================================================
    @Override
    public void onDownloadSuccessfully(Object data) {
        showSongInfo((SongModel) data);
    }

    @Override
    public void onDownloadFailed(Exception e) {

    }
    //====

    private void getWidgets(View root){
        imgCover = (ImageView) root.findViewById(R.id.img_cover);
        txtTitle = (TextView) root.findViewById(R.id.txt_title);
        txtArtists = (TextView) root.findViewById(R.id.txt_artists);
        txtComposer = (TextView) root.findViewById(R.id.txt_composer);
        txtAlbum = (TextView) root.findViewById(R.id.txt_album);
        txtYear = (TextView) root.findViewById(R.id.txt_year);
        txtViewed = (TextView) root.findViewById(R.id.txt_viewed);
        txtDownloaded = (TextView) root.findViewById(R.id.txt_downloaded);
        edtLyrics = (EditText) root.findViewById(R.id.edt_lyrics);
    }

    private void showSongInfo(SongModel songModel){
        if (songModel == null) return;

        txtTitle.setText(songModel.getTitle());
        txtArtists.setText(songModel.getArtist());
        txtComposer.setText(songModel.getComposer());
        txtAlbum.setText(songModel.getAlbum());
        txtYear.setText(songModel.getYear());
        txtViewed.setText(songModel.getViewed());
        txtDownloaded.setText(songModel.getDownloaded());
        edtLyrics.setText(songModel.getLyrics());
        int size = (int) ViewUtil.convertDp2Px(150);
        if(songModel.getCoverPath() != null && !songModel.getCoverPath().equals("http://chiasenhac.com/images/player_csn.png")) {
            Picasso.with(getActivity())
                    .load(songModel.getCoverPath())
                    .resize(size, size)
                    .onlyScaleDown()
                    .centerCrop()
                    .placeholder(R.drawable.ic_recordring)
                    .error(R.drawable.ic_recordring)
                    .into(imgCover);
        }else{
            Picasso.with(getActivity())
                    .load(R.drawable.ic_recordring)
                    .resize(size, size)
                    .centerCrop()
                    .into(imgCover);
        }
    }

    public void checkAndShowSongInfo(SongModel songModel){
        if (songModel.getCoverPath() == null || songModel.getCoverPath().isEmpty()){
            JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), this);
            worker.setParser(new JSoupSongInfoParser())
                    .showProgressbar(true, false)
                    .execute(songModel.getOriginPath());
        }else{
            showSongInfo(songModel);
        }
    }
}
