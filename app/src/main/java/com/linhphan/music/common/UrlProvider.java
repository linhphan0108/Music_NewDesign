package com.linhphan.music.common;

/**
 * Created by linhphan on 10/22/15.
 */
public class UrlProvider {

    public static final String REFIX_DOWNLOAD_PATH = "http://download.chiasenhac.com/mp3/vietnam/";
    public static final String SURFIX_DOWNLOAD_PATH = "_download.html";

    //================== vietnamese music
    public static String getHotMusicViUrl() {
        return "http://chiasenhac.com/mp3/vietnam/";
    }

    public static String getRemixMusicViUrl() {
        return "http://chiasenhac.com/mp3/vietnam/v-dance-remix/";
    }

    public static String getCountryMusicViUrl() {
        return "http://chiasenhac.com/mp3/vietnam/v-truyen-thong/";
    }

    public static String getRapMusicViUrl() {
        return "http://chiasenhac.com/mp3/vietnam/v-rap-hiphop/";
    }

    //================== english music
    public static String getPopMusicEnUrl() {
        return "http://chiasenhac.com/mp3/us-uk/u-pop/";
    }

    public static String getRapMusicEnUrl() {
        return "http://chiasenhac.com/mp3/us-uk/u-rap-hiphop/";
    }

    public static String getDanceMusicEnUrl() {
        return "http://chiasenhac.com/mp3/us-uk/u-dance-remix/";
    }

    //================== Korean music
    public static String getPopMusicKoreanUrl() {
        return "http://chiasenhac.com/mp3/korea/k-pop/";
    }

    public static String getRapMusicKoreanUrl() {
        return "http://chiasenhac.com/mp3/korea/k-rap-hiphop/";
    }

    public static String getDanceMusicKoreanUrl() {
        return "http://chiasenhac.com/mp3/korea/k-dance-remix/";
    }

    //================== chinese music
    public static String getPopMusicChineseUrl() {
        return "http://chiasenhac.com/mp3/chinese/c-pop/";
    }

    public static String getRapMusicChineseUrl() {
        return "http://chiasenhac.com/mp3/chinese/c-rap-hiphop/";
    }

    public static String getDanceMusicChineseUrl() {
        return "http://chiasenhac.com/mp3/chinese/c-dance-remix/";
    }
}
