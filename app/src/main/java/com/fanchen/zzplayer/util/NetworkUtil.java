package com.fanchen.zzplayer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

/**
 * Created by fanchen on 2017/5/3.
 */
public class NetworkUtil {
    /**
     * 网络是否可用
     *
     * @return true-有网络
     */
    public static boolean isNetworkAvailable(@NonNull Context cxt) {
        ConnectivityManager manager = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean connected = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            connected = true;
        }
        return connected;
    }
}
