package com.linhphan.music.api.parser;

import com.linhphan.androidboilerplate.api.BaseDownloadWorker;
import com.linhphan.androidboilerplate.api.Parser.IParser;
import com.linhphan.music.util.UrlProvider;
import com.linhphan.music.data.model.SongModel;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by linhphan on 11/17/15.
 */
public class JSoupSongListParser implements IParser {
    @Override
    public Object parse(Object data, BaseDownloadWorker.ResponseCodeHolder responseCode) {
        ArrayList<SongModel> links = null;
        if (data instanceof Document) {
            Document document = (Document) data;
            Elements elements = document.select("div[class=h-center]");
            if (elements.size() <= 0) return null;
            Element element = elements.get(0);
            Elements songListElements = element.select("div[class=text2]");
            if (songListElements.size() == 0)
                songListElements = element.select("div[class=text2 text2x]");
            links = new ArrayList<>();
            for (int i = 0; i < songListElements.size(); i++) {
                Element songElement = songListElements.get(i);
                String songName = songElement.select("a[class=txtsp1]").text();
                String artist = songElement.select("p[class=spd1]").text();
                String originPath = songElement.select("a").attr("abs:href");
                String downloadPath = originPath.substring(0, originPath.length() - 5).trim() + UrlProvider.SURfFIX_DOWNLOAD_PATH;
                SongModel songModel = new SongModel(songName, artist, downloadPath, originPath);
                links.add(songModel);
            }
        }

        return links;
    }
}
