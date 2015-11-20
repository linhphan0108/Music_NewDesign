package com.linhphan.music.util;

/**
 * Created by linhphan on 11/19/15.
 */
public enum RepeatMode {
    REPEAT_ALL(1),//repeat infinitely
    REPEAT(2),//repeat a song only
    REPEAT_ONE(3);//repeat a list once

    int value;

    RepeatMode(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
