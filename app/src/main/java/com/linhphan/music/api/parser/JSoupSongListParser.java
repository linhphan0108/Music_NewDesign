package com.linhphan.music.api.parser;

import com.linhphan.androidboilerplate.api.Parser.IParser;
import com.linhphan.music.common.UrlProvider;
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
    public Object parse(Object data) {
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
                //http://playlist.chiasenhac.com/nhac-hot-2/hoa-tuyet-trang~phuong-nhung-zj~1435091.html
                String[] arr = originPath.split("/");
                if (arr.length > 4) {
                    String downloadPath = arr[4];
                    downloadPath = UrlProvider.PREFIX_DOWNLOAD_PATH + downloadPath.substring(0, downloadPath.length() - 5) + UrlProvider.SURfFIX_DOWNLOAD_PATH;
                    SongModel songModel = new SongModel(songName, artist, downloadPath, originPath);

                    links.add(songModel);
                }
            }
        }

        return links;

    }
}
