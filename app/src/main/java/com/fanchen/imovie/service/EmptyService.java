package com.fanchen.imovie.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * 一個空的服務
 * 主要是web頁面需要運行在一個新的進程裡面，第一次打開需要創建進程，造成1-2s的黑屏
 * 使用這個服務來佔用一個進程，下次第一次使用web的時候就不會有黑屏了
 *
 * 给 API >= 18 的平台上用的灰色保活手段
 * Created by fanchen on 2017/11/6.
 */
public class EmptyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final static int GRAY_SERVICE_ID = 1001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (Build.VERSION.SDK_INT < 18) {
                startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
            } else {
                Intent innerIntent = new Intent(this, GrayInnerService.class);
                startService(innerIntent);
                startForeground(GRAY_SERVICE_ID, new Notification());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            try {
                startForeground(GRAY_SERVICE_ID, new Notification());
                stopForeground(true);
                stopSelf();
            }catch (Exception e){
                e.printStackTrace();
            }
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }

}
