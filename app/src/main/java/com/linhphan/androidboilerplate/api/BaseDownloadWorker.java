package com.linhphan.androidboilerplate.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.linhphan.androidboilerplate.api.Parser.IParser;
import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.androidboilerplate.util.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by linhphan on 11/17/15.
 */
public class BaseDownloadWorker extends AsyncTask<String, Integer, Object> {

    protected Context mContext;
    protected Method mType = Method.GET;//the method of request whether POST or GET, default value is GET
    protected Map<String, String> mParams;
    protected DownloadCallback mCallback;
    protected IParser mParser;
    protected ProgressDialog mProgressbar;
    protected boolean mIsProgressbarHorizontal;
    protected boolean mIsShowProgressbar;
    protected Exception mException;

    public BaseDownloadWorker(Context mContext, DownloadCallback mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    public BaseDownloadWorker setType(Method type) {
        this.mType = type;
        return this;
    }

    public BaseDownloadWorker setParams(Map<String, String> params) {
        this.mParams = params;
        return this;
    }

    public BaseDownloadWorker setParser(IParser jsonParser){
        mParser = jsonParser;
        return this;
    }

    /**
     * setup the progressbar which will be showed on screen
     * @param isShow     the progressbar will be showed if this parameter is true, otherwise nothing will be showed
     * @param horizontal if this parameter is true then the progressbar will showed in horizontal style, otherwise the progressbar will be showed in spinner style
     * @return JsonDownloadWorker object
     */
    public BaseDownloadWorker showProgressbar(boolean isShow, boolean horizontal) {
        mIsShowProgressbar = isShow;
        this.mIsProgressbarHorizontal = horizontal;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mIsShowProgressbar) {
            mProgressbar = new ProgressDialog(mContext);

            //====
            if (mIsProgressbarHorizontal) {
                mProgressbar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressbar.setMax(100);
                mProgressbar.setProgress(0);

            } else {
                mProgressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }

            mProgressbar.setMessage("Please! wait a minute");
            mProgressbar.setCancelable(false);
            mProgressbar.show();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (mException == null)
            mCallback.onDownloadSuccessfully(o);
        else {
            mCallback.onDownloadFailed(mException);
        }
        if (mProgressbar != null && mProgressbar.isShowing())
            mProgressbar.dismiss();

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mProgressbar != null && mProgressbar.isShowing())
            mProgressbar.setProgress(values[0]);
    }

    /**
     * try to retrieve data from remote server
     *
     * @return data from server which is presented by a string
     * @throws IOException
     */
    protected String sendGet(String path) throws IOException {
        String query = null;
        if (mParams != null)
            query = encodeQueryString(mParams);
        if (query != null && !query.isEmpty())
            query = "?" + query;
        URL url = new URL(path + query);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        int responseCode = httpURLConnection.getResponseCode();
        Logger.i(getClass().getName(), "sending POST  request to URL: " + path);
        Logger.i(getClass().getName(), "post parameters: " + query);
        Logger.i(getClass().getName(), "response code: " + responseCode);

        if (responseCode != HttpURLConnection.HTTP_OK) {
            Logger.e(getClass().getName(), "connection is failed");
            return null;
        }

        InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
        InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String result = readBuffer(bufferedReader);

        bufferedReader.close();
        inputStreamReader.close();
        in.close();
        httpURLConnection.disconnect();
        return result;
    }

    /**
     * try to retrieve data from remote server
     * dump server: http://www.posttestserver.com/
     *
     * @return data from server which is presented by a string
     * @throws IOException
     */
    protected String sendPost(String path, Map<String, String> params) throws IOException {
        URL url = new URL(path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        String query = encodeQueryString(params);


        //== add header
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Length", "" + Integer.toString(query.getBytes().length));

        //== set post request
        httpURLConnection.setDoOutput(true);
        DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        dataOutputStream.writeBytes(query);
        dataOutputStream.flush();
        dataOutputStream.close();

        int responseCode = httpURLConnection.getResponseCode();
        Logger.i(getClass().getName(), "sending POST  request to URL: " + path);
        Logger.i(getClass().getName(), "post parameters: " + query);
        Logger.i(getClass().getName(), "response code: " + responseCode);

        if (responseCode != HttpURLConnection.HTTP_OK) {
            Logger.e(getClass().getName(), "connection is failed");
            return null;
        }

        InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
        InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String result = readBuffer(bufferedReader);

        //== close streams
        bufferedReader.close();
        inputStreamReader.close();
        in.close();
        httpURLConnection.disconnect();
        return result;
    }

    /**
     * read data from buffer
     *
     * @return data which is presented by a string
     * @throws IOException
     */
    private String readBuffer(BufferedReader reader) throws IOException {
        if (reader == null) return null;
        StringBuilder builder = new StringBuilder();
        int tamp;
        while ((tamp = reader.read()) != -1) {
            builder.append((char) tamp);
        }
        return builder.toString();
    }

    private String encodeQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        final char PARAMETER_DELIMITER = '&';
        final char PARAMETER_EQUALS_CHAR = '=';

        StringBuilder builder = new StringBuilder();
        if (params != null) {
            boolean firstParameter = true;
            for (String key : params.keySet()) {
                if (!firstParameter) {
                    builder.append(PARAMETER_DELIMITER);
                }
                builder.append(key)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(URLEncoder.encode(params.get(key), "UTF-8"));

                if (firstParameter)
                    firstParameter = false;
            }
        }

        return builder.toString();
    }

    protected String getTag() {
        return getClass().getName();
    }

    @Override
    protected Object doInBackground(String... params) {
        return null;
    }
}
