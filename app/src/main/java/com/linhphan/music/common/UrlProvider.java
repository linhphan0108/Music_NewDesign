package com.linhphan.music.common;

/**
 * Created by linhphan on 10/22/15.
 */
public class UrlProvider {

    public static final String PREFIX_DOWNLOAD_PATH = "http://download.chiasenhac.com/mp3/vietnam/";
    public static final String SURfFIX_DOWNLOAD_PATH = "_download.html";

    public static String getUrl(MusicCategories category){
        switch (category){
            //================== vietnamese music
            case VI_HOT:
                return "http://chiasenhac.com/mp3/vietnam/";
            case VI_REMIX:
                return "http://chiasenhac.com/mp3/vietnam/v-dance-remix/";
            case VI_RAP:
                return "http://chiasenhac.com/mp3/vietnam/v-rap-hiphop/";
            case VI_COUNTRY:
                return "http://chiasenhac.com/mp3/vietnam/v-truyen-thong/";

            //================== english music
            case EN_POP:
                return "http://chiasenhac.com/mp3/us-uk/u-pop/";
            case EN_REMIX:
            case EN_RAP:
                return "http://chiasenhac.com/mp3/us-uk/u-rap-hiphop/";
            case EN_DANCE:
                return "http://chiasenhac.com/mp3/us-uk/u-dance-remix/";

            //================== Korean music
            case KOREAN_POP:
                return "http://chiasenhac.com/mp3/korea/k-pop/";
            case KOREAN_REMIX:
            case KOREAN_RAP:
                return "http://chiasenhac.com/mp3/korea/k-rap-hiphop/";
            case KOREAN_DANCE:
                return "http://chiasenhac.com/mp3/korea/k-dance-remix/";

            //================== chinese music
            case CHINESE_POP:
                return "http://chiasenhac.com/mp3/chinese/c-pop/";
            case CHINESE_REMIX:
            case CHINESE_RAP:
                return "http://chiasenhac.com/mp3/chinese/c-rap-hiphop/";
            case CHINESE_DANCE:
                return "http://chiasenhac.com/mp3/chinese/c-dance-remix/";

            default:
                return null;
        }
    }
}
