package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.IKanFanService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * IKanFanParser
 * Created by fanchen on 2017/11/16.
 */
public class IKanFanParser implements IVideoMoreParser {

    private KankanwuImpl kankanwu = new KankanwuImpl(IKanFanService.class.getName(), false, true);

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
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {
            Map<String, String> stringMap = new HashMap<>();
            if (html.contains("var zanpiancms_player")) {
                String match = JavaScriptUtil.match("zanpiancms_player = \\{[\\{\\}\\[\\]\\\"\\w\\d第集`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\\}", html, 0, 20, 0);
                LogUtil.e("IKanFanParser", "match -> " + match);
                if (JavaScriptUtil.isJson(match)) {
                    JSONObject object = new JSONObject(match);
                    if (object.has("url") &&  object.has("apiurl")) {
                        String url = object.getString("url");
                        String apiurl = object.getString("apiurl");
                        playUrl.setSuccess(true);
                        if (url.startsWith("http") && url.contains(".m3u")) {
                            stringMap.put("标清", url);
                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                            playUrl.setUrlType(IPlayUrls.URL_M3U8);
                        } else if (url.startsWith("http") && (url.contains(".mp4") || url.contains(".avi") || url.contains(".rm") || url.contains("wmv"))) {
                            stringMap.put("标清", url);
                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                            playUrl.setUrlType(IPlayUrls.URL_FILE);
                        } else if (!TextUtils.isEmpty(apiurl) && apiurl.startsWith("http")) {
                            stringMap.put("标清", apiurl + url);
                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                            playUrl.setUrlType(IPlayUrls.URL_WEB);
                        } else {
                            playUrl.setSuccess(false);
                        }
                        playUrl.setUrls(stringMap);
                        playUrl.setReferer(RetrofitManager.REQUEST_URL);
                    }
                }
            }
            if (stringMap.isEmpty()) {
                stringMap.put("标清", RetrofitManager.REQUEST_URL);
                playUrl.setUrls(stringMap);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setSuccess(true);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return playUrl;
    }

//    @Override
//    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoHome home = new VideoHome();
//        try {
//            List<Video> videos = new ArrayList<>();
//            home.setList(videos);
//            for (Node n : node.list("ul#resize_list > li")) {
//                String title = n.attr("a", "title");
//                String cover = n.attr("a > div > img", "data-original");
//                String clazz = n.textAt("div.list_info > p", 0);
//                String type = n.textAt("div.list_info > p", 1);
//                String author = n.textAt("div.list_info > p", 2);
//                String url = baseUrl + n.attr("a", "href");
//                String id = n.attr("a", "href", "/", 2);
//                Video video = new Video();
//                video.setCover(cover);
//                video.setHasDetails(true);
//                video.setServiceClass(IKanFanService.class.getName());
//                video.setId(id);
//                video.setTitle(title);
//                video.setUrl(url);
//                video.setAuthor(author);
//                video.setClazz(clazz);
//                video.setType(type);
//                videos.add(video);
//            }
//            home.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return home;
//    }
//
//    @Override
//    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoHome VideoHome = new VideoHome();
//        try {
//            List<Video> videos = new ArrayList<>();
//            for (Node n : node.list("div > div > ul#vod_list > li")) {
//                Video video = new Video();
//                video.setHasDetails(true);
//                video.setServiceClass(IKanFanService.class.getName());
//                video.setCover(n.attr("a > div > img", "data-original"));
//                video.setTitle(n.text("h2"));
//                video.setClazz(n.text("p"));
//                video.setId(n.attr("a", "href", "/", 2));
//                video.setUrl(baseUrl + n.attr("a", "href"));
//                video.setAuthor(n.text("a > div > label.score"));
//                video.setType(n.text("a > div > label.title"));
//                videos.add(video);
//            }
//            if (videos.size() > 0) {
//                VideoHome.setSuccess(true);
//                VideoHome.setList(videos);
//            }
//        } catch (Exception e) {
//            VideoHome.setSuccess(false);
//            VideoHome.setMessage(e.toString());
//            e.printStackTrace();
//        }
//        return VideoHome;
//    }
//
//    @Override
//    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoDetails details = new VideoDetails();
//        try {
//            List<VideoEpisode> episodes = new ArrayList<>();
//            List<Video> videos = new ArrayList<>();
//            for (Node n : node.list("div.ikf-item.like > div > a")) {
//                String title = n.text("h3");
//                String cover = n.attr("div > img", "data-original");
//                if (TextUtils.isEmpty(cover) || !cover.startsWith("http"))
//                    continue;
//                String hd = n.text("p");
//                String score = n.text("em");
//                String url = baseUrl + n.attr("href");
//                String id = n.attr("href", "/", 2);
//                Video video = new Video();
//                video.setHasDetails(true);
//                video.setServiceClass(IKanFanService.class.getName());
//                video.setCover(cover);
//                video.setId(id);
//                video.setTitle(title);
//                video.setUrl(url);
//                video.setAuthor(score);
//                video.setType(hd);
//                videos.add(video);
//            }
//            for (Node n : node.list("ul.playlist > li")) {
//                VideoEpisode episode = new VideoEpisode();
//                episode.setServiceClass(IKanFanService.class.getName());
//                episode.setId(baseUrl + n.attr("a", "href"));
//                episode.setUrl(baseUrl + n.attr("a", "href"));
//                episode.setTitle(n.text());
//                episodes.add(episode);
//            }
//            details.setServiceClass(IKanFanService.class.getName());
//            details.setCover(node.attr("div.ikf-detail > div > a > img", "src"));
//            details.setClazz(node.textAt("div.detail > p", 0));
//            details.setType(node.textAt("div.detail > p", 1));
//            details.setAuthor(node.textAt("div.detail > p", 2));
//            details.setTitle(node.text("div.detail > h1"));
//            details.setIntroduce(node.text("p.pTxt"));
//            details.setEpisodes(episodes);
//            details.setRecomm(videos);
//            details.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return details;
//    }
//
//    @Override
//    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
//        VideoPlayUrls playUrl = new VideoPlayUrls();
//        Map<String, String> stringMap = new HashMap<>();
//        playUrl.setUrls(stringMap);
//        try {
//            String match = JavaScriptUtil.match("var playConfig = \\{[;:,./\\-@#=\\'\\\" \\w\\d\\?&]+\\}", html, 0, 17, 0);
//            LogUtil.e("match","match===>" + match);
//            if (!TextUtils.isEmpty(match)) {
//                JSONObject jsonObject = new JSONObject(match);
//                String playname = "", ckUrl = "", pv = "";
//                if (jsonObject.has("playname")) {
//                    playname = jsonObject.getString("playname");
//                }
//                if (jsonObject.has("ckUrl")) {
//                    ckUrl = jsonObject.getString("ckUrl");
//                }
//                if (jsonObject.has("pv")) {
//                    pv = jsonObject.getString("pv");
//                }
//                if(pv.contains("=") && pv.contains("@")){
//                    stringMap.put("标清", pv.split("@")[0]);
//                    playUrl.setSuccess(true);
//                    playUrl.setReferer(RetrofitManager.REQUEST_URL);
//                    playUrl.setUrlType(IPlayUrls.URL_WEB);
//                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                }else if (!pv.contains("=")) {
//                    String s1 = pv.split(",")[0];
//                    if(s1.startsWith("http")){
//                        String s = ckUrl + playname + "&id=" + s1;
//                        stringMap.put("标清", s);
//                    }else{
//                        if(s1.contains("_") && playname.contains("qiyi")){
//                            s1 = s1.split("_")[0];
//                        }
//                        String s = "https://www.ikanfan.cn/mdparse/?type=" + playname + "&id=" + s1;
//                        stringMap.put("标清", s);
//                    }
//                    playUrl.setSuccess(true);
//                    playUrl.setReferer(RetrofitManager.REQUEST_URL);
//                    playUrl.setUrlType(IPlayUrls.URL_WEB);
//                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (stringMap.isEmpty()) {
//            stringMap.put("标清", RetrofitManager.REQUEST_URL);
//            playUrl.setUrls(stringMap);
//            playUrl.setUrlType(IPlayUrls.URL_WEB);
//            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB_V);
//            playUrl.setSuccess(true);
//        }
//        return playUrl;
//    }
}
