package com.linhphan.music.api.parser;

import com.linhphan.androidboilerplate.api.Parser.IParser;

import org.jsoup.nodes.Document;

/**
 * Created by linhphan on 11/17/15.
 */
public class JSoupDownloadSongParser implements IParser {
    @Override
    public Object parse(Object data) {
        Object result = null;
        if (data instanceof Document) {
            Document document = (Document) data;
            result = document.select("div[id=downloadlink]").select("a").last().attr("abs:href");
        }
        return result;
    }
}
