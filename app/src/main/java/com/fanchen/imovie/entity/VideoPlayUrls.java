package com.fanchen.imovie.entity;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoEpisode;

import java.util.Map;

/**
 * 重构后的 视频播放地址
 * Created by fanchen on 2017/9/28.
 */
public class VideoPlayUrls implements IPlayUrls {

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
        return referer == null ? "" : referer;
    }

    @Override
    public boolean m3u8Referer() {
        return m3u8Referer;
    }

    @Override
    public String getMainUrl() {
        Map<String, String> urls = getUrls();
        if (urls != null && !urls.isEmpty()) {
            String value = urls.entrySet().iterator().next().getValue();
            String replace = value.replace("\\/", "/");
            return decodeUnicode(replace);
        }
        return null;
    }

    @Override
    public boolean isDirectPlay() {
        return playType == IVideoEpisode.PLAY_TYPE_ZZPLAYER ||
                playType == IVideoEpisode.PLAY_TYPE_VIDEO ||
                playType == IVideoEpisode.PLAY_TYPE_VIDEO_M3U8;
    }

    @Override
    public boolean isSuccess() {
        String mainUrl = getMainUrl();
        return success && !TextUtils.isEmpty(mainUrl);
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

    static String decodeUnicode(String dataStr) {
        final StringBuilder buffer = new StringBuilder();
        try {
            String[] split = dataStr.split("\\\\u");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (i == 0 && (s.startsWith("http") || s.startsWith("xg") || s.startsWith("ftp"))) {
                    buffer.append(s);
                } else if (s.contains(".")) {
                    String[] contains = s.split("\\.");
                    String contain = contains[0];
                    if(contain.length() > 4){
                        char letter = (char) Integer.parseInt(contain.substring(0,4), 16); // 16进制parse整形字符串。
                        buffer.append(Character.valueOf(letter).toString());
                        buffer.append(contain, 4, contain.length());
                    }else{
                        char letter = (char) Integer.parseInt(contain, 16); // 16进制parse整形字符串。
                        buffer.append(Character.valueOf(letter).toString());
                    }
                    buffer.append(".").append(contains[1]);
                } else {
                    char letter = (char) Integer.parseInt(s, 16); // 16进制parse整形字符串。
                    buffer.append(Character.valueOf(letter).toString());
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
