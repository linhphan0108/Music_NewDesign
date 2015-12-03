package com.linhphan.music.util;

import android.content.Context;

import com.linhphan.music.R;

/**
 * Created by linhphan on 11/18/15.
 */
public class DrawerNavigationUtil {
    public static final int DEFAULT_CATEGORY_CODE = 0;
    public static final int SEARCH_CATEGORY_CODE = 99999;
    public static final int CURRENT_CATEGORY_CODE = 88888;



    public static int getMenuItemId(int position) {
        switch (position) {
            //vietnamese music
            case CURRENT_CATEGORY_CODE:
                return R.id.menu_item_current_playing;

            case 0:
                return R.id.menu_item_hot_vi;
            case 1:
                return R.id.menu_item_remix_vi;
            case 2:
                return R.id.menu_item_rap_vi;
            case 3:
                return R.id.menu_item_country_vi;

            //english music
            case 4:
                return R.id.menu_item_pop_en;
            case 5:
                return R.id.menu_item_rap_en;
            case 6:
                return R.id.menu_item_dance_en;

            //korean music
            case 7:
                return R.id.menu_item_pop_korea;
            case 8:
                return R.id.menu_item_rap_korea;
            case 9:
                return R.id.menu_item_dance_korea;

            //chinese music
            case 10:
                return R.id.menu_item_pop_china;
            case 11:
                return R.id.menu_item_rap_china;
            case 12:
                return R.id.menu_item_dance_china;

            default:
                return -1;
        }
    }

    public static int getCategoryCode(int menuItemId) {
        switch (menuItemId) {
            case R.id.menu_item_current_playing:
                return CURRENT_CATEGORY_CODE;

            //vietnamese music
            case R.id.menu_item_hot_vi:
                return 0;
            case R.id.menu_item_remix_vi:
                return 1;
            case R.id.menu_item_rap_vi:
                return 2;
            case R.id.menu_item_country_vi:
                return 3;

            //english music
            case R.id.menu_item_pop_en:
                return 4;
            case R.id.menu_item_rap_en:
                return 5;
            case R.id.menu_item_dance_en:
                return 6;

            //korean music
            case R.id.menu_item_pop_korea:
                return 7;
            case R.id.menu_item_rap_korea:
                return 8;
            case R.id.menu_item_dance_korea:
                return 9;

            //chinese music
            case R.id.menu_item_pop_china:
                return 10;
            case R.id.menu_item_rap_china:
                return 11;
            case R.id.menu_item_dance_china:
                return 12;

            default:
                return -1;
        }
    }

    public static String getTitle(Context context, int categoryCode) {
        switch (categoryCode) {
            //vietnamese music
            case 0:
                return context.getString(R.string.music_type_hot);
            case 1:
                return context.getString(R.string.music_type_remix);
            case 2:
                return context.getString(R.string.music_type_rap);
            case 3:
                return context.getString(R.string.music_type_country);

            //english music
            case 4:
                return context.getString(R.string.music_type_pop);
            case 5:
                return context.getString(R.string.music_type_rap);
            case 6:
                return context.getString(R.string.music_type_dance);

            //korean music
            case 7:
                return context.getString(R.string.music_type_pop);
            case 8:
                return context.getString(R.string.music_type_rap);
            case 9:
                return context.getString(R.string.music_type_dance);

            //chinese music
            case 10:
                return context.getString(R.string.music_type_pop);
            case 11:
                return context.getString(R.string.music_type_rap);
            case 12:
                return context.getString(R.string.music_type_dance);

            case SEARCH_CATEGORY_CODE:
                return context.getString(R.string.music_type_search_result);

            default:
                return "unknown";
        }
    }
}
