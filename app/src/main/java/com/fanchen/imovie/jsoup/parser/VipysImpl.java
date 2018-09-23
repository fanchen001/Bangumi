package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.VipysService;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * VipysImpl
 * Created by fanchen on 2017/10/28.
 */
public class VipysImpl implements IVideoMoreParser {

    private KankanwuImpl kankanwu = new KankanwuImpl(VipysService.class.getName(), false);

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return kankanwu.more(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return kankanwu.search(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return kankanwu.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        VideoDetails details = (VideoDetails) kankanwu.details(retrofit, baseUrl, html);
        List<VideoEpisode> episodes = (List<VideoEpisode>) details.getEpisodes();
        if (episodes == null) return details;
        List<VideoEpisode> newEpisodes = new ArrayList<>();
        for (VideoEpisode episode : episodes) {
            if (!episode.getTitle().contains("迅雷") && !episode.getTitle().contains("网盘")) {
                newEpisodes.add(episode);
            }
        }
        details.setEpisodes(newEpisodes);
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls iPlayUrls = (VideoPlayUrls) kankanwu.playUrl(retrofit, baseUrl, html);
        Map<String, String> urls = iPlayUrls.getUrls();
        if (urls != null && !urls.isEmpty()) return iPlayUrls;
        try {
            Node node = new Node(html);
            for (Node n : node.list("script")) {
                String src = n.attr("src");
                if (!src.startsWith("http") || !src.contains("player")) continue;
                String url2String = StreamUtil.url2String(src);
                int start = url2String.indexOf("{");
                int end = url2String.indexOf("}");
                if (start != -1 && end != -1) {
                    JSONObject jsonObject = new JSONObject(url2String.substring(start, end + 1));
                    String apiurl = jsonObject.optString("apiurl");
                    String url = jsonObject.optString("url");
                    urls = new HashMap<>();
                    if(url.contains(".mp4") || url.contains(".avi") || url.contains(".rm")){
                        urls.put("标清",  RetrofitManager.warpUrl(baseUrl,url));
                        iPlayUrls.setUrlType(IPlayUrls.URL_FILE);
                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                    }else if(url.contains(".m3u")){
                        urls.put("标清", RetrofitManager.warpUrl(baseUrl, url));
                        iPlayUrls.setUrlType(IPlayUrls.URL_M3U8);
                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                    }else{
                        urls.put("标清", RetrofitManager.warpUrl(baseUrl,apiurl + url));
                        iPlayUrls.setUrlType(IPlayUrls.URL_WEB);
                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                    }
                }
            }
            iPlayUrls.setUrls(urls);
            iPlayUrls.setReferer(baseUrl);
            iPlayUrls.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return iPlayUrls;
    }

}
