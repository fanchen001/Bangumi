package com.fanchen.imovie.entity;

import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoEpisode;

import java.util.Map;

/**
 * 重构后的 视频播放地址
 * Created by fanchen on 2017/9/28.
 */
public class VideoPlayUrls implements IPlayUrls{

    private boolean success;
    private String message;
    private Map<String, String> urls;
    private String cid;
    private boolean m3u8Referer = false;
    private int playType = IVideoEpisode.PLAY_TYPE_VIDEO;
    private int urlType = URL_FILE;
    private String referer = null;

    @Override
    public Map<String, String> getUrls() {
        return urls;
    }

    @Override
    public int getPlayType() {
        return playType;
    }

    @Override
    public int getUrlType() {
        return urlType;
    }

    @Override
    public String getReferer() {
        return referer;
    }

    @Override
    public boolean m3u8Referer() {
        return m3u8Referer;
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

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    public void setUrlType(int urlType) {
        this.urlType = urlType;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getCid() {
        return cid;
    }

    public void setM3u8Referer(boolean m3u8Referer) {
        this.m3u8Referer = m3u8Referer;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
