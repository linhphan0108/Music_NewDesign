package com.linhphan.music.api.parser;

import com.linhphan.androidboilerplate.api.Parser.IParser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by linhphan on 11/17/15.
 */
public class JSoupDirectlyDownloadSongParser implements IParser {
    @Override
    public Object parse(Object data) {
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
        return convertArrayListToStringArray(arr);
    }

    private String[] convertArrayListToStringArray(ArrayList<String> arr){
        String[] result = new String[arr.size()];
        for (int i=0; i<arr.size(); i++){
            result[i] = arr.get(i);
        }
        return result;
    }
}
