package com.linhphan.music.common;

import com.linhphan.music.model.SongModel;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by linhphan on 10/22/15.
 */
public interface AsyncTaskCallback {
    public void onDoingBackground();

    public void onDownloadSuccessfully(ArrayList<SongModel> list);

    public void onDownloadSuccessfully(String url);

    public void onDownloadError(IOException ex, String url);
}
