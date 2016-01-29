package com.linhphan.androidboilerplate.api.Parser;

import com.linhphan.androidboilerplate.api.BaseDownloadWorker;

/**
 * Created by linhphan on 11/12/15.
 */
public interface IParser {
    Object parse(Object data, BaseDownloadWorker.ResponseCodeHolder responseCode);
}
