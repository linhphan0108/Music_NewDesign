package com.linhphan.androidboilerplate.api;

import android.content.Context;

import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.androidboilerplate.util.FileUtil;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by linhphan on 11/26/15.
 */
public class FileDownloadWorker extends BaseDownloadWorker {

    public FileDownloadWorker(Context mContext, DownloadCallback mCallback) {
        super(mContext, mCallback);
    }

    @Override
    protected Object doInBackground(String... params) {
        if (mException != null)
            return null;

        String path = "http://data.chiasenhac.com/downloads/1586/4/1585284-d7265ce3/128/Apology%20-%20iKON%20[MP3%20128kbps].mp3";
        File result;
        try {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            int responseCode = httpURLConnection.getResponseCode();
            Logger.i(getClass().getName(), "sending POST  request to URL: " + path);
            Logger.i(getClass().getName(), "response code: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Logger.e(getClass().getName(), "connection is failed");
                return null;
            }

            int contentLength = httpURLConnection.getContentLength();

            InputStream inputStream = httpURLConnection.getInputStream();
            File homeFolder = FileUtil.getOrCreateAppFolder(mContext.getString(R.string.app_name));
            File newFile = new File(homeFolder, "dump.mp3");
            OutputStream outputStream = new FileOutputStream(newFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            byte[] buffer = new byte[1024];
            long total = 0;
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                total += count;
                // publishing the progress....
                publishProgress((int)(total*100/contentLength));
                bufferedOutputStream.write(buffer, 0, count);
            }

            inputStream.close();
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            outputStream.close();

            return newFile.getAbsolutePath();//locate the new downloaded file

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
