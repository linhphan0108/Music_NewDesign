package com.linhphan.music.api.parser;

import com.linhphan.androidboilerplate.api.BaseDownloadWorker;
import com.linhphan.androidboilerplate.api.Parser.IParser;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.util.UrlProvider;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by linhphan on 11/20/15.
 */
public class JSoupSearchParser implements IParser {
    @Override
    public Object parse(Object data, BaseDownloadWorker.ResponseCodeHolder responseCode) {
        ArrayList<SongModel> links = null;
        if (data instanceof Document){
            Document document = (Document) data;
            Elements elements = document.select("body > div.mu-wrapper > div > div.m-left > div > div > div.pad > div.h-main > div.page-dsms > div.bod > table > tbody > tr");
            links = new ArrayList<>();
            for (int i=1; i<elements.size(); i++){
                Element songElement = elements.get(i);
                String songName = songElement.select("a[class=musictitle]").text();
                String artist = songElement.select("div[class=tenbh] > p").get(1).text();
                String originPath = songElement.select("a").attr("abs:href");
                if (!originPath.contains("video")){
                    String downloadPath = originPath.substring(0, originPath.length() - 5) + UrlProvider.SURfFIX_DOWNLOAD_PATH;
                    SongModel songModel = new SongModel(songName, artist, downloadPath, originPath);
                    links.add(songModel);
                }
            }
        }

        return links;
    }
}
