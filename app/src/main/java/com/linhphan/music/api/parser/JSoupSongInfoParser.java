package com.linhphan.music.api.parser;

import com.linhphan.androidboilerplate.api.BaseDownloadWorker;
import com.linhphan.androidboilerplate.api.Parser.IParser;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.util.ContentManager;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by linhphan on 11/19/15.
 */
public class JSoupSongInfoParser implements IParser {
    @Override
    public Object parse(Object data, BaseDownloadWorker.ResponseCodeHolder responseCode) {
        SongModel songModel = null;
        if (data instanceof Element){
            Element root = (Element) data;
            String coverPath = root.select("head > meta:nth-child(12)").attr("content");
            Elements tops = root.select("body > div.mu-wrapper > div > div.m-left > div:nth-child(1) > div > div.pad > div > div.pl_top > div.datelast > span");
            String lyrics = root.select("#fulllyric > p.genmed").html();
            if (lyrics != null) {
                lyrics = " " + lyrics.replaceAll("(?i)<br>", "\n").replaceAll("<span.*</span>", "");
            }else{
                lyrics = "not found";
            }


            String composer = root.select("#fulllyric > p:nth-child(4) > b > a").text();
            String album = root.select("#fulllyric > p:nth-child(5) > b > a:nth-child(1)").text();
            String year = root.select("#fulllyric > p:nth-child(6) > b").text();


            songModel = ContentManager.getInstance().getCurrentPlayingSong();
            songModel.setCoverPath(coverPath);
            if (tops.size() > 2) {
                songModel.setViewed(tops.get(1).text());
                songModel.setDownloaded(tops.get(2).text());
            }else{
                songModel.setViewed("");
                songModel.setDownloaded("");
            }
            songModel.setComposer(composer);
            songModel.setAlbum(album);
            songModel.setYear(year);
            songModel.setLyrics(lyrics);
        }
        return songModel;
    }
}
