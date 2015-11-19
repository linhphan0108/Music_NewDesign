package com.linhphan.music.util;

/**
 * Created by linhphan on 11/19/15.
 */
public enum RepeatMode {
    REPEAT_ALL(1),
    REPEAT_ONE(2),
    REPEAT_NONE(3);

    int value;

    RepeatMode(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
