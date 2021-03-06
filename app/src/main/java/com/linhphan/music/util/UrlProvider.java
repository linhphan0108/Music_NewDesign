package com.linhphan.music.util;

import com.linhphan.music.R;

/**
 * Created by linhphan on 10/22/15.
 */
public class UrlProvider {

    public static final String PREFIX_DOWNLOAD_PATH = "http://download.chiasenhac.com/mp3/vietnam/";
    public static final String SURfFIX_DOWNLOAD_PATH = "_download.html";
    public static final String SEARCH_PATH = "http://search.chiasenhac.com/search.php?s=";

    public static String getUrlFromCategoryCode(int categoryCode){
        int category = DrawerNavigationUtil.getMenuItemId(categoryCode);
        return getUrl(category);
    }

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
            case R.id.menu_item_rap_en:
                return "http://chiasenhac.com/mp3/us-uk/u-rap-hiphop/";
            case R.id.menu_item_dance_en:
                return "http://chiasenhac.com/mp3/us-uk/u-dance-remix/";

            //================== korean music
            case R.id.menu_item_pop_korea:
                return "http://chiasenhac.com/mp3/korea/k-pop/";
            case R.id.menu_item_rap_korea:
                return "http://chiasenhac.com/mp3/korea/k-rap-hiphop/";
            case R.id.menu_item_dance_korea:
                return "http://chiasenhac.com/mp3/korea/k-dance-remix/";

            //=================== chinese music
            case R.id.menu_item_pop_china:
                return "http://chiasenhac.com/mp3/chinese/c-pop/";
            case R.id.menu_item_rap_china:
                return "http://chiasenhac.com/mp3/chinese/c-rap-hiphop/";
            case R.id.menu_item_dance_china:
                return "http://chiasenhac.com/mp3/chinese/c-dance-remix/";

            default:
                return null;
        }
    }
}
