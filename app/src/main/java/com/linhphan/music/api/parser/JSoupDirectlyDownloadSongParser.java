package com.linhphan.music.api.parser;

import android.content.Context;
import android.widget.Toast;

import com.linhphan.androidboilerplate.api.BaseDownloadWorker;
import com.linhphan.androidboilerplate.api.Parser.IParser;
import com.linhphan.androidboilerplate.util.ListLruCache;
import com.linhphan.androidboilerplate.util.Logger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by linhphan on 11/17/15.
 */
public class JSoupDirectlyDownloadSongParser implements IParser {
    private Context mContext;
    private String mKey;


    public JSoupDirectlyDownloadSongParser(Context context, String url) {
        this.mContext = context;
        this.mKey = url;
    }

    @Override
    public Object parse(Object data, BaseDownloadWorker.ResponseCodeHolder responseCode) {
        ArrayList<String> arr = new ArrayList<>();
        if (data instanceof Document) {
            Document document = (Document) data;
            Elements elements = document.select("#downloadlink > b > a");
            for (int i=0; i< elements.size(); i++){
                String temp = elements.get(i).attr("abs:href").trim().replace(" ", "");
                if (temp.endsWith(".mp3") || temp.endsWith(".m4a")){
                    arr.add(temp);
                }
            }
        }
//        ListLruCache cache = ListLruCache.getInstance(mContext);
//        cache.put(mKey, arr);
//        Logger.d(getClass().getName(), "urls has been cached, urls' size " + arr.size());
        return arr;
    }

    private String[] convertArrayListToStringArray(ArrayList<String> arr){
        String[] result = new String[arr.size()];
        for (int i=0; i<arr.size(); i++){
            result[i] = arr.get(i);
        }
        return result;
    }
}
