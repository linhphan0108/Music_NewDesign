package com.linhphan.music.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linhphan.androidboilerplate.api.JSoupDownloadWorker;
import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.androidboilerplate.util.ViewUtil;
import com.linhphan.music.R;
import com.linhphan.music.api.parser.JSoupSongInfoParser;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.util.ContentManager;
import com.linhphan.music.util.NoInternetConnectionException;
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
        if (e instanceof NoInternetConnectionException)
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }
    //====

    private void getWidgets(View root) {
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

    private void showSongInfo(SongModel songModel) {
        if (songModel == null) return;

        //==title
        if (songModel.getTitle().isEmpty()) {
            txtTitle.setText(R.string.text_view_unknown);
        }else{
            txtTitle.setText(songModel.getTitle());
        }

        //==artist
        if (songModel.getArtist().isEmpty()){
            txtArtists.setText(R.string.text_view_unknown);
        }else {
            txtArtists.setText(songModel.getArtist());
        }

        //==composer
        if (songModel.getComposer().isEmpty()){
            txtComposer.setText(R.string.text_view_unknown);
        }else {
            txtComposer.setText(songModel.getComposer());
        }

        //==album
        if (songModel.getAlbum().isEmpty()){
            txtAlbum.setText(R.string.text_view_unknown);
        }else {
            txtAlbum.setText(songModel.getAlbum());
        }

        //==published year
        if (songModel.getYear().isEmpty()){
            txtYear.setText(R.string.text_view_unknown);
        }else {
            txtYear.setText(songModel.getYear());
        }

        //==viewed
        if (songModel.getViewed().isEmpty()){
            txtViewed.setText(R.string.text_view_unknown);
        }else {
            txtViewed.setText(songModel.getViewed());
        }

        //==downloaded
        if (songModel.getDownloaded().isEmpty()){
            txtDownloaded.setText(R.string.text_view_unknown);
        }else {
            txtDownloaded.setText(songModel.getDownloaded());
        }

        //==lyrics
        if (songModel.getLyrics().isEmpty()){
            edtLyrics.setText(R.string.text_view_unknown);
        }else {
            edtLyrics.setText(songModel.getLyrics());
        }

        //== album cover
        int size = (int) ViewUtil.convertDp2Px(150);
        if (songModel.getCoverPath() != null && !songModel.getCoverPath().equals("http://chiasenhac.com/images/player_csn.png")) {
            Picasso.with(getActivity())
                    .load(songModel.getCoverPath())
                    .resize(size, size)
                    .onlyScaleDown()
                    .centerCrop()
                    .placeholder(R.drawable.ic_recording)
                    .error(R.drawable.ic_recording)
                    .into(imgCover);
        } else {
            Picasso.with(getActivity())
                    .load(R.drawable.ic_recording)
                    .resize(size, size)
                    .centerCrop()
                    .into(imgCover);
        }
    }

    public void checkAndShowSongInfo(SongModel songModel) {
        if (songModel.getCoverPath() == null || songModel.getCoverPath().isEmpty()) {
            JSoupDownloadWorker worker = new JSoupDownloadWorker(getContext(), true, this);
            worker.setParser(new JSoupSongInfoParser())
                    .execute(songModel.getOriginPath());
        } else {
            showSongInfo(songModel);
        }
    }
}
