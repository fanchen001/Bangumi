package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoBanner;
import com.fanchen.imovie.entity.VideoTitle;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.HaliHaliService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * HaliHaliParser
 * Created by fanchen on 2017/11/16.
 */
public class HaliHaliParser implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Video> videos = new ArrayList<>();
            home.setList(videos);
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.attr("a", "title");
                String cover = n.attr("a > div > img", "data-original");
                String clazz = n.textAt("div.list_info > p", 0);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a", "href");
                String id = n.attr("a", "href", "/", 2);
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(HaliHaliService.class.getName());
                video.setCover(cover);
                video.setId(id);
                video.setTitle(title);
                video.setUrl(url);
                video.setAuthor(author);
                video.setClazz(clazz);
                video.setType(type);
                videos.add(video);
            }
            home.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome videoHome = new VideoHome();
        try {
            List<VideoTitle> titles = new ArrayList<>();
            List<VideoBanner> banners = new ArrayList<>();
            int count = 0;
            for (Node n : node.list("div.swiper-wrapper > div.swiper-slide")) {
                VideoBanner banner = new VideoBanner();
                banner.setCover(n.attr("a", "data-background"));
                banner.setId( n.attr("a", "href", "/", 2));
                banner.setTitle(n.text("a > div > div.sub_title"));
                banner.setUrl(baseUrl + n.attr("a", "href"));
                banner.setServiceClass(HaliHaliService.class.getName());
                banners.add(banner);
            }
            if (banners.size() > 0) {
                videoHome.setHomeBanner(banners);
            }
            LogUtil.e("home","section -> " + node.list("section.mod.margin-t-15").size());
            for (Node n : node.list("section.mod.margin-t-15")) {
                String topTitle = n.text("span.mod-head-name");
                if (TextUtils.isEmpty(topTitle) || "热点推送" .equals(topTitle)|| "小哈推荐" .equals(topTitle)) continue;
                String topUrl = baseUrl + n.attr("iv.mod-head.clearfix > a", "href");
                String topId = n.attr("div.mod-head.clearfix > a", "href", "/", 1);
                List<Video> videos = new ArrayList<>();
                VideoTitle videoTitle = new VideoTitle();
                videoTitle.setTitle(topTitle);
                videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                videoTitle.setId(topId);
                if(!topTitle.contains("最新")){
                    videoTitle.setMore(true);
                }
                videoTitle.setPageStart(2);
                videoTitle.setUrl(topUrl);
                videoTitle.setList(videos);
                videoTitle.setServiceClass(HaliHaliService.class.getName());
                for (Node sub : n.list("div > div > ul > li")) {
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(HaliHaliService.class.getName());
                    video.setCover(sub.attr("a > div._item-pic._item-lazy", "data-echo"));
                    video.setTitle(sub.text("a > div.video-con"));
                    video.setClazz(sub.text("a > div > div.video-duration"));
                    video.setId(sub.attr("a", "href", "/", 2));
                    video.setUrl(baseUrl + sub.attr("a", "href"));
                    video.setAuthor(sub.text("a > div > div.video-duration"));
                    videos.add(video);
                }
                if (videos.size() > 0) {
                    titles.add(videoTitle);
                }
            }
            LogUtil.e("home","titles -> " + new Gson().toJson(titles));
            if (titles.size() == 0) {
                List<Video> videos = new ArrayList<>();
                for (Node sub : node.list("ul > li.video-item")) {
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(HaliHaliService.class.getName());
                    video.setCover(sub.attr("a > div._item-pic._item-lazy", "data-echo"));
                    video.setTitle(sub.text("a > div.video-con"));
                    video.setClazz(sub.text("a > div > div.video-duration"));
                    video.setId(sub.attr("a", "href", "/", 2));
                    video.setUrl(baseUrl + sub.attr("a", "href"));
                    video.setAuthor(sub.text("a > div > div.video-duration"));
                    videos.add(video);
                }
                videoHome.setSuccess(true);
                videoHome.setList(videos);
            } else {
                videoHome.setSuccess(true);
                videoHome.setHomeResult(titles);
            }
        } catch (Exception e) {
            videoHome.setSuccess(false);
            videoHome.setMessage(e.toString());
            e.printStackTrace();
        }
        return videoHome;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoDetails details = new VideoDetails();
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            List<Video> videos = new ArrayList<>();
            for (Node sub : node.list("ul > li.video-item")) {
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(HaliHaliService.class.getName());
                video.setCover(sub.attr("a > div._item-pic._item-lazy", "data-echo"));
                video.setTitle(sub.text("a > div.video-con"));
                video.setClazz(sub.text("a > div > div.video-duration"));
                video.setId(sub.attr("a", "href", "/", 2));
                video.setUrl(baseUrl + sub.attr("a", "href"));
                video.setAuthor(sub.text("a > div > div.video-duration"));
                videos.add(video);
            }
            for (Node n : node.list("div.detail-video-select > ul > li")) {
                for (Node sub : n.list("li")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(HaliHaliService.class.getName());
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    episode.setTitle(sub.text());
                    episodes.add(episode);
                }
            }
            details.setServiceClass(HaliHaliService.class.getName());
            details.setCover(node.attr("div.detail-img > img", "src"));
            details.setClazz(node.textAt("div.detail-media > ul.desc > li", 1));
            details.setType(node.textAt("div.detail-media > ul.desc > li", 2));
            details.setAuthor(node.textAt("div.detail-media > ul.desc > li", 0));
            details.setTitle(node.text("div.detail-media > h1.media-title.ellipsis-2"));
            details.setIntroduce(node.text("p.detail-intro-txt.ellipsis-2"));
            details.setEpisodes(episodes);
            details.setRecomm(videos);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {
            Map<String, String> stringMap = new HashMap<>();
            if (html.contains("var zanpiancms_player")) {
                html = html.substring(html.indexOf("var zanpiancms_player"));
                if (html.contains("http")) {
                    html = html.substring(html.indexOf("http"));
                    html = html.substring(0, html.indexOf("\""));
                    if (!html.contains("halihali") && !(html.contains(".mp4") || html.contains(".m3u"))) {
                        html = "https://halihali.duapp.com/mdparse/index.php?id=" + html;
                    }
                    if (html.startsWith("/")) {
                        html = baseUrl + html;
                    } else if (html.startsWith("//")) {
                        html = "https" + html;
                    }
                    if(html.contains("=")){
                        stringMap.put("标清", html.split("=")[1]);
                    }else{
                        stringMap.put("标清", html);
                    }
                    playUrl.setUrls(stringMap);
                    playUrl.setReferer(RetrofitManager.REQUEST_URL);
                    if(html.contains(".mp4") || html.contains(".m3u")){
                        playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                        playUrl.setUrlType(IPlayUrls.URL_M3U8);
                    }else{
                        playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                        playUrl.setUrlType(IPlayUrls.URL_WEB);
                    }
                    playUrl.setSuccess(true);
                }
            }
            if (stringMap.isEmpty()) {
                stringMap.put("标清", RetrofitManager.REQUEST_URL);
                playUrl.setUrls(stringMap);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB_V);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setSuccess(true);
//            }
            }
            //var zanpiancms_player = {"url":"https:\/\/player.qinmoe.com\/play\/MzYwLzE1Mjg1NjEwMDgyNDAwMjM5"

//            String src = JavaScriptUtil.match("\"url\":\"[\\-@#=&:.\\\\/\\w\\d]+\",", html, 0, 7, 2);
//            String name = JavaScriptUtil.match("\"name\":\"[\\w\\d]+\",", html, 0, 8, 2);
//            if (TextUtils.isEmpty(src) || TextUtils.isEmpty(name)){
//                stringMap.put("标清", RetrofitManager.REQUEST_URL);
//                playUrl.setUrls(stringMap);
//                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB_V);
//                playUrl.setUrlType(IPlayUrls.URL_WEB);
//                playUrl.setSuccess(true);
//                return playUrl;
//            }
//            if ("xnflv".equals(name)) {
//                if (src.contains("&") && !src.startsWith("http")) {
//                    src = "http://xn.Video.tv/?type=" + src.replace("\\/", "/");
//                } else {
//                    src = "http://xn.Video.tv/?url=" + src.replace("\\/", "/");
//                }
//            } else {
//                if (!src.contains("&") && !src.startsWith("http")) {
//                    src = "http://m.Video.tv/weigao/api/opentv_mb.php?v=" + src + "&E-mail=88888";
//                } else if (src.contains("&") && !src.startsWith("http")) {
//                    src = "https://player.guolewan.com/mdparse/index.php?type=" + src.replace("\\/", "/");
//                } else {
//                    src = "https://player.guolewan.com/mdparse/index.php?id=" + src.replace("\\/", "/");
//                }
//            }
//            LogUtil.e("src == > ", src);
//            Map<String, String> map = new HashMap<>();
//            map.put("Referer", baseUrl);
//            map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
//            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//            map.put("Cookie", "vParse_YKCna=U7a/EW4SsSsCAdzKmCvvEJEf");
//            String src2s = StreamUtil.url2String(src, map);
//            if (!TextUtils.isEmpty(src2s)) {
//                String playerUrl = "https://player.guolewan.com" + JavaScriptUtil.match("getScript\\('[\\\\\\-%:/.?&=\\w\\d]+'\\)", src2s, 0, 11, 2);
//                map.put("Referer", src);
//                String playerUrl2s = StreamUtil.url2String(playerUrl, map);
//                if (!TextUtils.isEmpty(playerUrl2s)) {
//                    String json = JavaScriptUtil.match("\\(\\{[-\" :%&,'/=.?\\w\\d\\[\\]\\(\\)\\{\\}]+\\}\\)", playerUrl2s, 0, 1, 1);
//                    String purl = JavaScriptUtil.match("var purl = '[+?=&:.\\\\/\\w\\d]+';", playerUrl2s, 0, 12, 2);
//                    if (!TextUtils.isEmpty(purl)) {
//                        stringMap.put("标清", purl);
//                        playUrl.setUrls(stringMap);
//                        playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//                        playUrl.setUrlType(IPlayUrls.URL_M3U8);
//                        playUrl.setSuccess(true);
//                    } else if (!TextUtils.isEmpty(json)) {
//                        getJsonUrl(playUrl, json);
//                    }
//                }
//            }
//            if (!playUrl.isSuccess() && !TextUtils.isEmpty(src)) {
//                stringMap.put("标清", src);
//                playUrl.setUrls(stringMap);
//                playUrl.setUrlType(IPlayUrls.URL_WEB);
//                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                playUrl.setSuccess(true);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

    private void getJsonUrl(VideoPlayUrls playUrl, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("success") && jsonObject.getBoolean("success") && jsonObject.has("urls")) {
                JSONArray urls = jsonObject.getJSONArray("urls");
                if (urls != null && urls.length() > 0) {
                    JSONObject jsonObj = urls.getJSONObject(0);
                    if (jsonObj.has("u") && jsonObject.has("type")) {
                        String type = jsonObject.getString("type");
                        String u = jsonObj.getString("u");
                        Map<String, String> stringMap = new HashMap<>();
                        stringMap.put(type, u);
                        playUrl.setUrls(stringMap);
                        playUrl.setPlayType("mp4".equals(type) ? IVideoEpisode.PLAY_TYPE_ZZPLAYER : IVideoEpisode.PLAY_TYPE_VIDEO);
                        playUrl.setUrlType("mp4".equals(type) ? IPlayUrls.URL_FILE : IPlayUrls.URL_M3U8);
                        playUrl.setSuccess(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot) home(retrofit, baseUrl, html);
    }

}
