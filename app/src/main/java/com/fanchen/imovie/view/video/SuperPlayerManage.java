package com.fanchen.imovie.view.video;

import android.content.Context;

/**
 *
 * 类描述：获取唯一的视频控制器
 *
 * @author Super南仔
 * @time 2016-9-19
 */
public class SuperPlayerManage {
    public static SuperPlayerManage videoPlayViewManage;
    private SuperPlayerView videoPlayView;

    private SuperPlayerManage() {

    }

    public static SuperPlayerManage getSuperManage() {
        if (videoPlayViewManage == null) {
            videoPlayViewManage = new SuperPlayerManage();
        }
        return videoPlayViewManage;
    }

    public SuperPlayerView initialize(Context context) {
        if (videoPlayView == null) {
            videoPlayView = new SuperPlayerView(context);
        }
        return videoPlayView;
    }
}
