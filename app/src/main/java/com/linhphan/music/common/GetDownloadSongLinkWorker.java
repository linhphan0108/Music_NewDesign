package com.linhphan.music.common;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;

import com.linhphan.androidboilerplate.util.Logger;

/**
 * Created by linhphan on 10/22/15.
 */
public class GetDownloadSongLinkWorker extends AsyncTask<Void, Void, String> {
    private Context mContext;
    private String mUrl;
    private AsyncTaskCallback mCallback;
    private IOException mEx;

    public GetDownloadSongLinkWorker(Context mContext, String mUrl, AsyncTaskCallback callback) {
        this.mContext = mContext;
        this.mUrl = mUrl;
        this.mCallback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (mUrl == null || mUrl.isEmpty()) return null;
        Logger.d(getTag(), "try to get the download link of the song at " + mUrl);
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(mUrl).get();
            return doc.select("div[id=downloadlink]").select("a").last().attr("abs:href");
        } catch (IOException e) {
            mEx = e;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (this.mEx == null) {
            mCallback.onDownloadSuccessfully(s);
            Logger.d(getTag(), "got the download url from "+ this.mUrl);
        }
        else
            mCallback.onDownloadError(this.mEx, this.mUrl);

    }

    private String getTag(){
        return getClass().getName();
    }
}
