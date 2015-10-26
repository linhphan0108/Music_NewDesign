package com.linhphan.music.common;

import com.linhphan.music.R;
import com.linhphan.music.fragment.SongListFragment;

/**
 * Created by linhphan on 10/22/15.
 */
public class UrlProvider {

    public static final String PREFIX_DOWNLOAD_PATH = "http://download.chiasenhac.com/mp3/vietnam/";
    public static final String SURfFIX_DOWNLOAD_PATH = "_download.html";

    public static String getUrl(int category) {
        switch (category) {
            //================== vietnamese music
            case R.id.menu_item_hot_vi:
                return "http://chiasenhac.com/mp3/vietnam/";
            case R.id.menu_item_remix_vi:
                return "http://chiasenhac.com/mp3/vietnam/v-dance-remix/";
            case R.id.menu_item_rap_vi:
                return "http://chiasenhac.com/mp3/vietnam/v-rap-hiphop/";
            case R.id.menu_item_country_vi:
                return "http://chiasenhac.com/mp3/vietnam/v-truyen-thong/";

            //================== english music
            case R.id.menu_item_pop_en:
                return "http://chiasenhac.com/mp3/us-uk/u-pop/";
            case R.id.menu_item_remix_en:
//            break;// TODO: 24/10/2015 this must return an url
            case R.id.menu_item_rap_en:
                return "http://chiasenhac.com/mp3/us-uk/u-rap-hiphop/";
            case R.id.menu_item_dance_en:
                return "http://chiasenhac.com/mp3/us-uk/u-dance-remix/";

            //================== korean music
            case R.id.menu_item_pop_korea:
                return "http://chiasenhac.com/mp3/korea/k-pop/";
            case R.id.menu_item_remix_korea:
            case R.id.menu_item_rap_korea:
                return "http://chiasenhac.com/mp3/korea/k-rap-hiphop/";
//            break;// TODO: 24/10/2015 this must return an url
            case R.id.menu_item_dance_korea:
                return "http://chiasenhac.com/mp3/korea/k-dance-remix/";

            //=================== chinese music
            case R.id.menu_item_pop_china:
                return "http://chiasenhac.com/mp3/chinese/c-pop/";
            case R.id.menu_item_remix_china:
//                break;// TODO: 24/10/2015 this must return an url
            case R.id.menu_item_rap_china:
                return "http://chiasenhac.com/mp3/chinese/c-rap-hiphop/";
            case R.id.menu_item_dance_china:
                return "http://chiasenhac.com/mp3/chinese/c-dance-remix/";

            default:
                return null;
        }
    }
}
