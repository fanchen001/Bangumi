package com.fanchen.zzplayer.util;

/**
 * Created by fanchen on 2017/5/3.
 * 播放状态
 */
public class PlayState {
    public static final int IDLE = 0x00;
    public static final int PREPARE = 0x01;
    public static final int PLAY = 0x02;
    public static final int PAUSE = 0x03;
    public static final int STOP = 0x04;
    public static final int COMPLETE = 0x05;
    public static final int ERROR = 0x06;
}
