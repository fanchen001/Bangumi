package com.fanchen.imovie.entity.tucao;

import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoEpisode;

import java.util.Map;

/**
 * TucaoPlayUrls
 * Created by fanchen on 2017/9/28.
 */
public class TucaoPlayUrls implements IPlayUrls {

    private boolean success;
    private String message;
    private Map<String, String> urls;

    @Override
    public Map<String, String> getUrls() {
        return urls;
    }

    @Override
    public int getPlayType() {
        return IVideoEpisode.PLAY_TYPE_VIDEO;
    }

    @Override
    public int getUrlType() {
        return URL_FILE;
    }

    @Override
    public String getReferer() {
        return "http://www.tucao.tv/";
    }

    @Override
    public boolean m3u8Referer() {
        return false;
    }

    @Override
    public String getMainUrl() {
        if (urls == null || urls.isEmpty()) return "";
        return urls.values().iterator().next();
    }

    @Override
    public boolean isDirectPlay() {
        String mainUrl = getMainUrl();
        return mainUrl.startsWith("http://") || mainUrl.startsWith("https://");
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }


}
