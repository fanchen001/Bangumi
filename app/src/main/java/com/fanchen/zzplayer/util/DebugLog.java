package com.fanchen.zzplayer.util;

import android.util.Log;

import com.fanchen.imovie.BuildConfig;


/**
 * Created by fanchen on 2017/4/28.
 */
public class DebugLog {
    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }
}
