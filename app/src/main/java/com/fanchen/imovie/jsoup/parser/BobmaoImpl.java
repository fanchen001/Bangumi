package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.BobmaoService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.SecurityUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 山猫视频
 * Created by fanchen on 2017/12/23.
 */
public class BobmaoImpl implements IVideoMoreParser {

    private XiaokanbaImpl xiaokanba = new XiaokanbaImpl(BobmaoService.class.getName());

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return xiaokanba.more(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return xiaokanba.search(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return xiaokanba.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        return xiaokanba.details(retrofit, baseUrl, html);
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {
            String match1 = JavaScriptUtil.match("base64decode\\(\"[\"%\\w\\W\\d$]+=\"\\);", html, 0, 14, 3);
            if (!TextUtils.isEmpty(match1)) {
                LogUtil.e("BobmaoImpl", "match1 -> " + match1);
                String decode = new String(SecurityUtil.decode(match1));
                Map<String, String> map = new HashMap<>();
                map.put("标清", decode);
                playUrl.setReferer(RetrofitManager.REQUEST_URL);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setUrls(map);
                playUrl.setSuccess(true);
            } else {
                String match2 = JavaScriptUtil.match("var VideoInfoList=[\"%\\w\\W\\d$]+\"<", html, 0, 19, 2);
                LogUtil.e("BobmaoImpl", "match2 -> " + match2);
                String[] split = match2.split("\\$\\$\\$");
                String[] splitUrl = RetrofitManager.REQUEST_URL.split("-");
                if (split.length > Integer.valueOf(splitUrl[1])) {
                    String[] urls = split[Integer.valueOf(splitUrl[1])].split("\\$\\$");
                    for (int j = 1; j < urls.length; j += 2) {
                        String[] ids = urls[j].split("#");
                        for (int k = 0; k < ids.length; k++) {
                            if (k == Integer.valueOf(splitUrl[2].replace(".html", ""))) {
                                String[] strings = ids[k].split("\\$");
                                Map<String, String> map = new HashMap<>();
                                if (strings[1].startsWith("ftp:") || strings[1].startsWith("xg:")) {
                                    map.put(strings[0], strings[1]);
                                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                                    playUrl.setUrlType(IPlayUrls.URL_XIGUA);
                                } else if (strings[1].contains(".m3u8")) {
                                    map.put(strings[0], strings[1]);
                                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                                    playUrl.setUrlType(IPlayUrls.URL_M3U8);
                                } else {
                                    map.put(strings[0], strings[1]);
                                    playUrl.setUrlType(IPlayUrls.URL_WEB);
                                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                                }
                                playUrl.setUrls(map);
                                LogUtil.e("playUrl", "string -> " + playUrl.getMainUrl());
                                playUrl.setSuccess(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

}
