package com.fanchen.imovie.entity.face;

import java.util.Map;

/**
 * Created by fanchen on 2017/9/28.
 */
public interface IPlayUrls extends IRoot{
    /***这个不支持下载*/
    int URL_M3U8 = 0;
    /***这个不支持下载*/
    int URL_WEB = 1;
    /*** 支持下载*/
    int URL_FILE = 2;

    /**
     *
     * @return
     */
    Map<String,String> getUrls();

    /**
     *
     * @return
     */
    int getPlayType();

    /**
     *
     * @return
     */
    int getUrlType();

    /**
     * URL_WEB
     * 有些使用web播放的视频需要这个参数
     * @return
     */
    String getReferer();
}
