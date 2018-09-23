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
import com.fanchen.imovie.retrofit.service.WeilaiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * WeilaiImpl
 * Created by fanchen on 2017/10/28.
 */
public class WeilaiImpl implements IVideoMoreParser {

    private KankanwuImpl kankanwu = new KankanwuImpl(WeilaiService.class.getName(), false);

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
            int start = html.indexOf("var ff_urls='{");
            if (start != -1) html = html.substring(start + 13);
            int end = html.indexOf("}';");
            String substring = html.substring(0, end + 1);
            JSONArray data = new JSONObject(substring).optJSONArray("Data");
            String payurl = "";
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                JSONArray playurls = jsonObject.optJSONArray("playurls");
                if (playurls == null || playurls.length() == 0) continue;
                JSONArray jsonArray = playurls.optJSONArray(0);
                String furl = jsonArray.getString(2);
                if (!RetrofitManager.REQUEST_URL.contains(furl)) continue;
                payurl = jsonArray.getString(1);
            }
            iPlayUrls.setReferer(baseUrl);
            if (TextUtils.isEmpty(payurl)) {
                iPlayUrls.setSuccess(false);
            } else {
                urls = new HashMap<>();
                if (payurl.contains(".mp4") || payurl.contains(".avi") || payurl.contains(".rm")) {
                    urls.put("标清", RetrofitManager.warpUrl(baseUrl,payurl));
                    iPlayUrls.setUrlType(IPlayUrls.URL_FILE);
                    iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                } else if (payurl.contains(".m3u")) {
                    urls.put("标清", RetrofitManager.warpUrl(baseUrl,payurl));
                    iPlayUrls.setUrlType(IPlayUrls.URL_M3U8);
                    iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                } else {
                    urls.put("标清", RetrofitManager.warpUrl(baseUrl,payurl));
                    iPlayUrls.setUrlType(IPlayUrls.URL_WEB);
                    iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                }
                iPlayUrls.setUrls(urls);
                iPlayUrls.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iPlayUrls;
    }

}
