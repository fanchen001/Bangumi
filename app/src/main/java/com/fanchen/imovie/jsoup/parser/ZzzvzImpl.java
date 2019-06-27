package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.ZzzvzService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * ZzzvzImpl
 * Created by fanchen on 2018/7/27.
 */
public class ZzzvzImpl implements IVideoMoreParser{
    private KankanwuImpl smdy = new KankanwuImpl(ZzzvzService.class.getName(),false);

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return smdy.search(retrofit,baseUrl,html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return smdy.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        VideoDetails details = (VideoDetails) smdy.details(retrofit, baseUrl, html);
        List<VideoEpisode> episodes = (List<VideoEpisode>) details.getEpisodes();
        if(episodes != null && episodes.size() > 0){
            List<VideoEpisode> newEpisodes = new ArrayList<>();
            for (VideoEpisode episode : episodes){
                if(!episode.getTitle().contains("极速1")){
                    newEpisodes.add(episode);
                }
            }
            details.setEpisodes(newEpisodes);
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls urls = new VideoPlayUrls();
        Map<String, String> stringMap = new HashMap<>();
        try {
            String match = JavaScriptUtil.match("\\{[\\{\\}\\[\\]\\\"\\w\\d第集`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\\};", html, 0, 0, 1);
            LogUtil.e("ZzzvzImpl", "match = -> " + match);
            if (JavaScriptUtil.isJson(match)) {
                JSONObject object = new JSONObject(match);
                if( object.has("url")  &&  object.has("apiurl")){
                    String url = object.getString("url");
                    String apiurl = object.getString("apiurl");
                    urls.setSuccess(true);
                    if (url.startsWith("http") && url.contains(".m3u")) {
                        stringMap.put("标清", url);
                        urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                        urls.setUrlType(IPlayUrls.URL_M3U8);
                    } else if (url.startsWith("http") && (url.contains(".mp4") || url.contains(".avi") || url.contains(".rm") || url.contains("wmv"))) {
                        stringMap.put("标清", url);
                        urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                        urls.setUrlType(IPlayUrls.URL_FILE);
                    } else if (!TextUtils.isEmpty(apiurl)) {
                        stringMap.put("标清", apiurl + url);
                        urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                        urls.setUrlType(IPlayUrls.URL_WEB);
                    } else {
                        urls.setSuccess(false);
                    }
                    urls.setUrls(stringMap);
                    urls.setReferer(RetrofitManager.REQUEST_URL);
                }
            }
            if (stringMap.isEmpty()) {
                stringMap.put("标清", RetrofitManager.REQUEST_URL);
                urls.setUrls(stringMap);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                urls.setUrlType(IPlayUrls.URL_WEB);
                urls.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return smdy.more(retrofit, baseUrl, html);
    }
}
