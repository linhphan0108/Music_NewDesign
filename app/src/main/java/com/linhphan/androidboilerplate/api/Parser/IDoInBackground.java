package com.linhphan.androidboilerplate.api.Parser;

import java.io.IOException;

/**
 * Created by linhphan on 11/17/15.
 */
public interface IDoInBackground {
    Object doInBackground(String url, int category, IParser parser) throws IOException;
}
