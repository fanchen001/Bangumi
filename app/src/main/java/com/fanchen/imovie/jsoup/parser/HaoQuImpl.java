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
import com.fanchen.imovie.retrofit.service.HaoQuService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * HaoQuImpl
 * Created by fanchen on 2018/10/10.
 */
public class HaoQuImpl implements ITvParser{

    @Override
    public List<IBaseVideo> liveList(Retrofit retrofit, String baseUrl, String html) {
        List<IBaseVideo> videos = new ArrayList<>();
        List<String> videoTitles = new ArrayList<>();
        try {
            for (Node node : new Node(html).list("ul.xhbox.zblist > li > a")) {
                VideoBase videoBase = new VideoBase();
                videoBase.setId(baseUrl + node.attr("href"));
                videoBase.setUrl(baseUrl + node.attr("href"));
                videoBase.setTitle(node.text());
                videoBase.setServiceClass(HaoQuService.class.getName());
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
            String match = JavaScriptUtil.match("var sourid=\\'[0-9]+\\';", html, 0,12,2);
            String format = String.format("http://m.haoqu.net/e/extend/tv.php?id=%s", match);
            String s = StreamUtil.url2String(format);
            String url = JavaScriptUtil.match("\\$http[\\w\\d\\S]+\\$iframe", s, 0, 1, 7);
            if(TextUtils.isEmpty(url)){
                url = JavaScriptUtil.match("\\$http[\\w\\d\\S]+\\$m3u8", s, 0, 1, 5);
            }
            Map<String, String> mapUrl = new HashMap<>();
            playUrl.setReferer(baseUrl);
            playUrl.setUrls(mapUrl);
            if(!url.isEmpty()){
                mapUrl.put("标清", url);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

}
