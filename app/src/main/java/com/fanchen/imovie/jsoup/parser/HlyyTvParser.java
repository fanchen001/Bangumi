package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoBase;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.ITvParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.HlyyTvService;
import com.fanchen.imovie.util.JavaScriptUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * KuaikanTvParser
 * Created by fanchen on 2018/8/6.
 */
public class HlyyTvParser implements ITvParser {

    @Override
    public List<IBaseVideo> liveList(Retrofit retrofit, String baseUrl, String html) {
        List<IBaseVideo> videos = new ArrayList<>();
        List<String> videoTitles = new ArrayList<>();
        try {
            for (Node node : new Node(html).list("ul.nav-type > li > div > a")) {
                if (!node.attr("href").contains("/tv/")) {
                    continue;
                }
                VideoBase videoBase = new VideoBase();
                videoBase.setId(node.attr("href", "/", 2));
                videoBase.setUrl(RetrofitManager.warpUrl(baseUrl, node.attr("href")));
                videoBase.setTitle(node.text());
                videoBase.setServiceClass(HlyyTvService.class.getName());
                if (!videoTitles.contains(videoBase.getTitle())) {
                    videoTitles.add(videoBase.getTitle());
                    videos.add(videoBase);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videos;
    }

    @Override
    public IPlayUrls liveUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {
            Map<String, String> mapUrl = new HashMap<>();
            playUrl.setReferer(baseUrl);
            playUrl.setUrls(mapUrl);
            String match = JavaScriptUtil.match("var playCode =[\\u4e00-\\u9fa5\\(\\)\\{\\}\\[\\]\\\"\\w\\d`~！!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+;", html, 0, 14, 1);
            if (!TextUtils.isEmpty(match)) {
                JSONArray array = new JSONArray(match);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Node pCode = new Node(object.optString("pCode"));
                    String video = pCode.attr("video", "src");
                    if (TextUtils.isEmpty(video))
                        video = pCode.attr("iframe", "src");
                    if (!TextUtils.isEmpty(video)) {
                        mapUrl.put(object.optString("name"), video);
                        playUrl.setUrlType(IPlayUrls.URL_M3U8);
                        playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                        playUrl.setSuccess(true);
                    }
                }
            }
            if (mapUrl.isEmpty()) {
                mapUrl.put("标清", RetrofitManager.REQUEST_URL);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

}
