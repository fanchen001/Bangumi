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
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.google.gson.Gson;

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

    private XiaokanbaImpl kankanwu = new XiaokanbaImpl(WeilaiService.class.getName());

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
            if (episode.getTitle().contains("迅雷")) {
                episode.setPlayType(IVideoEpisode.PLAY_TYPE_XUNLEI);
                String replace = episode.getUrl().replace(baseUrl, "");
                episode.setUrl(replace);
                episodes.add(episode);
            } else if (!episode.getTitle().contains("网盘")) {
                newEpisodes.add(episode);
            }
        }
        details.setEpisodes(newEpisodes);
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls iPlayUrls = new VideoPlayUrls();
        iPlayUrls.setReferer(baseUrl);
        iPlayUrls.setM3u8Referer(true);
        Map<String, String> urls = new HashMap<>();
        iPlayUrls.setUrls(urls);
        try {
            String match = JavaScriptUtil.match("unescape\\([\".\\-_@|$=?/,:;\\w\\d\\(\\)\\[\\]'%]+", html, 0);
            if (!TextUtils.isEmpty(match)) {
                String[] split = RetrofitManager.REQUEST_URL.replace(".html", "").split("-");
                int count = 0;
                int xianlu = 0;
                if (split.length >= 2) {
                    xianlu = Integer.valueOf(split[1].replace(".html","")) ;
                }
                if (split.length >= 3) {
                    count = Integer.valueOf(split[2].replace(".html","")) ;
                }
                String jscode = "function(){ return " + match + "}";
                String callFunction = JavaScriptUtil.callFunction(jscode);
                LogUtil.e("WeilaiImpl","callFunction -> " + callFunction);
                String[] callSplit = callFunction.split("\\$\\$\\$");
                if(callSplit.length > xianlu){
                    String[] countSplit = callSplit[xianlu].split("#");
                    LogUtil.e("WeilaiImpl","countSplit -> " + callSplit[xianlu]);
                    if(countSplit.length > count){
                        String[] urlSplit = countSplit[count].split("\\$\\$");
                        LogUtil.e("WeilaiImpl","urlSplit -> " + countSplit[count]);
                        if(urlSplit.length == 2){
                            String[] videoSplit = urlSplit[1].split("\\$");
                            LogUtil.e("WeilaiImpl","videoSplit -> " + urlSplit[1]);
                            if(videoSplit.length == 3){
                                String videoUrl = videoSplit[1];

                                if(videoUrl.startsWith("ftp:") || videoUrl.startsWith("xg:")){
                                    urls.put(videoSplit[0],videoUrl);
                                    iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                                    iPlayUrls.setUrlType(IPlayUrls.URL_XIGUA);
                                    iPlayUrls.setSuccess(true);
                                    LogUtil.e("WeilaiImpl","PLAY_TYPE_XIGUA -> " + videoUrl);
                                }else{
                                    videoUrl = RetrofitManager.warpUrl(baseUrl,videoUrl);
                                    if(videoUrl.contains(".m3u")){
                                        LogUtil.e("WeilaiImpl","PLAY_TYPE_VIDEO_M3U8 -> " + videoUrl);
                                        urls.put(videoSplit[0],videoUrl);
                                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                                        iPlayUrls.setUrlType(IPlayUrls.URL_M3U8);
                                        iPlayUrls.setSuccess(true);
                                    }else if(videoUrl.contains(".mp4") || videoUrl.contains(".avi") || videoUrl.contains(".rm") || videoUrl.contains(".wmv")){
                                        urls.put(videoSplit[0], videoUrl);
                                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                                        iPlayUrls.setUrlType(IPlayUrls.URL_FILE);
                                        iPlayUrls.setSuccess(true);
                                        LogUtil.e("WeilaiImpl","PLAY_TYPE_VIDEO -> " + videoUrl);
                                    } else {
                                        LogUtil.e("WeilaiImpl","PLAY_TYPE_WEB -> " + videoUrl);
                                        urls.put(videoSplit[0],videoUrl);
                                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                                        iPlayUrls.setUrlType(IPlayUrls.URL_WEB);
                                        iPlayUrls.setSuccess(true);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
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
                    if (payurl.contains(".mp4") || payurl.contains(".avi") || payurl.contains(".rm")) {
                        urls.put("标清", RetrofitManager.warpUrl(baseUrl, payurl));
                        iPlayUrls.setUrlType(IPlayUrls.URL_FILE);
                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                    } else if (payurl.contains(".m3u")) {
                        urls.put("标清", RetrofitManager.warpUrl("http://3.1.bjqydt.com:1024", payurl));
                        iPlayUrls.setUrlType(IPlayUrls.URL_M3U8);
                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                    } else {
                        urls.put("标清", RetrofitManager.warpUrl(baseUrl, payurl));
                        iPlayUrls.setUrlType(IPlayUrls.URL_WEB);
                        iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                    }
                    iPlayUrls.setSuccess(true);
                }
            }
            if (urls.isEmpty()) {
                urls.put("标清", RetrofitManager.REQUEST_URL);
                iPlayUrls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                iPlayUrls.setUrlType(IPlayUrls.URL_WEB);
                iPlayUrls.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iPlayUrls;
    }

}
