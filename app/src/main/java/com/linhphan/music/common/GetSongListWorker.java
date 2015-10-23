package com.linhphan.music.common;

import android.content.Context;
import android.os.AsyncTask;

import com.linhphan.music.model.SongModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by linhphan on 10/22/15.
 */
public class GetSongListWorker extends AsyncTask<Void, Void, ArrayList<SongModel>> {
    private Context context;
    private String mUrl;
    private MusicCategories mCategory;
    private AsyncTaskCallback callback;
    private IOException ex;

    public GetSongListWorker(Context context, String url, MusicCategories category, AsyncTaskCallback callback) {
        this.context = context;
        this.mUrl = url;
        this.callback = callback;
        this.mCategory = category;
    }

    @Override
    protected ArrayList<SongModel> doInBackground(Void... params) {

        if (mUrl == null || mUrl.isEmpty()) return null;

        Logger.d(getTag(), "begin getting song list from " + mUrl);
        ArrayList<SongModel> links = new ArrayList<>();

        try {
            Document document = Jsoup.connect(this.mUrl).get();
            Elements elements = document.select("div[class=h-center]");
            if (elements.size() <= 0) return null;
            Element element = elements.get(0);
            Elements songListElements = element.select("div[class=text2]");
            if (songListElements.size() == 0)
                songListElements = element.select("div[class=text2 text2x]");
            for (int i = 0; i < songListElements.size(); i++) {
                Element songElement = songListElements.get(i);
                String songName = songElement.select("a[class=txtsp1]").text();
                String artist = songElement.select("p[class=spd1]").text();
                String originPath = songElement.select("a").attr("abs:href");
                //http://playlist.chiasenhac.com/nhac-hot-2/hoa-tuyet-trang~phuong-nhung-zj~1435091.html
                String[] arr = originPath.split("/");
                if (arr.length > 4) {
                    String downloadPath = arr[4];
                    downloadPath = UrlProvider.PREFIX_DOWNLOAD_PATH + downloadPath.substring(0, downloadPath.length() - 5) + UrlProvider.SURfFIX_DOWNLOAD_PATH;
                    SongModel songModel = new SongModel(songName, artist, downloadPath, originPath);

                    links.add(songModel);
                }
            }

        } catch (IOException e) {
            this.ex = e;
            e.printStackTrace();
        }

        ContentManager contentManager = ContentManager.getInstance();
        contentManager.setCurrentSongList(links, mCategory);
        return links;
    }

    private String getTag() {
        return getClass().getName();
    }

    @Override
    protected void onPostExecute(ArrayList<SongModel> result) {
        super.onPostExecute(result);
        if (this.ex == null) {
            callback.onDownloadSuccessfully(result);
            Logger.d(getTag(), "got song list from server successfully");
        } else {
            callback.onDownloadError(this.ex, mUrl);
        }
    }
}
