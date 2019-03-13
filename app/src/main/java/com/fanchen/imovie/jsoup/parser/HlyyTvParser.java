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
import com.fanchen.imovie.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;


//import com.fanchen.imovie.util.LogUtil;
//http://www.hlyy.cc/zxtv/m/hn1.html

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
            for (Node node : new Node(html).list("table > tbody > tr > td > a")) {
                VideoBase videoBase = new VideoBase();
                videoBase.setId(baseUrl + "/zxtv/m/" + node.attr("href"));
                videoBase.setUrl(baseUrl + "/zxtv/m/" + node.attr("href"));
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
            String attr = new Node(html).attr("iframe", "src");
            Map<String, String> mapUrl = new HashMap<>();
            playUrl.setReferer(baseUrl);
            playUrl.setUrls(mapUrl);
            if (!TextUtils.isEmpty(attr)) {
                if(attr.startsWith("//")){
                    attr = "http:" + attr;
                }else if(attr.startsWith("/")){
                    attr = baseUrl + attr;
                }
                mapUrl.put("标清", attr);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setSuccess(true);
            } else if (html.contains("ftp:")) {
                int i = html.indexOf("ftp:");
                html = html.substring(i);
                i = html.indexOf(",");
                html = html.substring(0, i - 1);
                mapUrl.put("标清", html);
                playUrl.setUrlType(IPlayUrls.URL_XIGUA);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                playUrl.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

}
