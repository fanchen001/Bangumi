package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoBanner;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.VideoTitle;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.TaihanService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;

import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * TaihanImpl
 * Created by fanchen on 2018/4/19.
 */
public class TaihanImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot)home(retrofit,baseUrl,html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try{
            List<IVideo> videos = new ArrayList<>();
            for (Node sub : node.list("dl.fed-deta-info.fed-deta-padding.fed-line-top.fed-margin.fed-part-rows.fed-part-over")){
                String title = sub.text("h1.fed-part-eone.fed-font-xvi");
                String cover = baseUrl + sub.attr("dt > a.fed-list-pics.fed-lazy.fed-part-2by3", "data-original");
                if (TextUtils.isEmpty(cover))
                    continue;
                String hd = sub.text("span.fed-list-remarks.fed-font-xii.fed-text-white.fed-text-center");
                String continu = sub.text("span.fed-list-score.fed-font-xii.fed-back-green");
                String url = baseUrl + sub.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(TaihanService.class.getName());
                video.setCover(cover);
                video.setId(sub.attr("a", "href","/",2));
                video.setTitle(title);
                video.setUrl(url);
                video.setDanmaku(continu);
                video.setType(hd);
                videos.add(video);
            }
            home.setList(videos);
            home.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {

        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Node> ullist = node.list("ul.fed-swip-wrapper.fed-font-size > li > a");
            List<VideoBanner> banners = new ArrayList<>();
            for (Node n : ullist) {
                VideoBanner banner = new VideoBanner();
                banner.setServiceClass(TaihanService.class.getName());
                banner.setCover(baseUrl + n.attr("data-background"));
                banner.setTitle(n.text("div > span.fed-part-eone.fed-font-xviii.fed-swip-head"));
                String href = n.attr("href");
                if(href.startsWith("http")){
                    banner.setUrl(href);
                    banner.setId(n.attr("href","/",4));
                }else if(href.startsWith("/")){
                    banner.setId(n.attr("href","/",2));
                    banner.setUrl(baseUrl  + href);
                }else{
                    banner.setId(n.attr("href","/",1));
                    banner.setUrl(baseUrl  + "/" +  href);
                }
                banners.add(banner);
            }
            home.setHomeBanner(banners);
            int count = 0;
            List<VideoTitle> titles = new ArrayList<>();
            home.setHomeResult(titles);
            for (Node n : node.list("div.fed-list-head.fed-part-rows.fed-padding")) {
                String topTitle = n.text("h2 > a");
                if(TextUtils.isEmpty(topTitle))
                    topTitle = n.text("h2").replace("更多","");
                String topUrl = baseUrl + n.attr("h2 > a", "href");
                String topId = n.attr("a.fed-more", "href", "/", 2);
                List<Video> videos = new ArrayList<>();
                VideoTitle videoTitle = new VideoTitle();
                videoTitle.setTitle(topTitle);
                videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                videoTitle.setId(topId);
                videoTitle.setUrl(topUrl);
                videoTitle.setList(videos);
                videoTitle.setServiceClass(TaihanService.class.getName());
//                Element element = n.getElement().nextElementSibling();
//
//                        //
//                LogUtil.e("IHomeRoot","tagName => " + element.tagName());
//                LogUtil.e("IHomeRoot","tagName => " + element.nextElementSibling());
//                if(element != null && !element.tagName().equals("ul")){
//                    element = element.nextElementSibling().children().first();
//                }
                //fed-list-info fed-part-rows
                for (Node sub : new Node(n.getElement().parent()).list("ul.fed-list-info.fed-part-rows > li")) {
                    String title = sub.text("a.fed-list-title.fed-font-xiv.fed-text-center.fed-text-sm-left.fed-visible.fed-part-eone");
                    String cover = baseUrl + sub.attr("a", "data-original");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    String hd = sub.text("span.fed-list-remarks.fed-font-xii.fed-text-white.fed-text-center");
                    String continu = sub.text("span.fed-list-score.fed-font-xii.fed-back-green");
                    String url = baseUrl + sub.attr("a", "href");
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(TaihanService.class.getName());
                    video.setCover(cover);
                    video.setId(sub.attr("a", "href","/",2));
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setDanmaku(continu);
                    video.setType(hd);
                    videos.add(video);
                }
                if (videos.size() > 0)
                    titles.add(videoTitle);
                videoTitle.setMore(true);
            }
            if(!titles.isEmpty()){
                titles.get(0).setMore(false);
            }
            home.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoDetails details = new VideoDetails();
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            List<Video> videos = new ArrayList<>();
            for (Node sub : node.list("ul.fed-list-info.fed-part-rows > li")) {
                String title = sub.text("a.fed-list-title.fed-font-xiv.fed-text-center.fed-text-sm-left.fed-visible.fed-part-eone");
                String cover = baseUrl + sub.attr("a", "data-original");
                if (TextUtils.isEmpty(cover))
                    continue;
                String hd = sub.text("span.fed-list-remarks.fed-font-xii.fed-text-white.fed-text-center");
                String continu = sub.text("span.fed-list-score.fed-font-xii.fed-back-green");
                String url = baseUrl + sub.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(TaihanService.class.getName());
                video.setCover(cover);
                video.setId(sub.attr("a", "href","/",2));
                video.setTitle(title);
                video.setUrl(url);
                video.setDanmaku(continu);
                video.setType(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div.fed-drop-boxs.fed-drop-tops.fed-matp-v > ul.fed-part-rows > li.fed-drop-btns.fed-padding.fed-col-xs3.fed-col-md2");
            for (Node n : node.list("div.fed-drop-boxs.fed-drop-btms.fed-matp-v > div.fed-play-item.fed-drop-item.fed-visible > ul.fed-part-rows")) {
                boolean flag = false;
                for (Node sub : n.list("li.fed-padding.fed-col-xs3.fed-col-md2.fed-col-lg1")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(TaihanService.class.getName());
                    episode.setId(sub.attr("a", "href","/",2));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    if (list.size() > count) {
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    } else {
                        episode.setTitle(sub.text());
                    }
                    episodes.add(episode);
                    flag = true;
                }
                if(flag)count++;
            }
            details.setCover(baseUrl + node.attr("a.fed-list-pics.fed-lazy.fed-part-2by3", "data-original"));
            details.setLast(node.text("span.fed-list-remarks.fed-font-xii.fed-text-white.fed-text-center"));
            details.setExtras(node.textAt("li.fed-col-xs12.fed-col-md6.fed-part-eone", 0));
            details.setDanmaku(node.textAt("li.fed-col-xs12.fed-col-md6.fed-part-eone", 1));
            details.setUpdate(node.textAt("li.fed-col-xs12.fed-col-md6.fed-part-eone", 2));
            details.setTitle(node.text("h1.fed-part-eone.fed-font-xvi"));
            details.setIntroduce(node.text("p.fed-padding.fed-part-both.fed-text-muted"));
            details.setEpisodes(episodes);
            details.setRecomm(videos);
            details.setServiceClass(TaihanService.class.getName());
            details.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return details;
    }
    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls urls = new VideoPlayUrls();
        Map<String, String> stringMap = new HashMap<>();
        try{
//            String match = JavaScriptUtil.match("\\{[\\{\\}\\[\\]\\\"\\w\\d第集`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\\};", html, 0,0,1);
//            LogUtil.e("TaihanImpl","match = -> " + match);
//            if(JavaScriptUtil.isJson(match)){
//                JSONObject object = new JSONObject(match);
//                if( object.has("url")  &&  object.has("jiexi")){
//                    String url = object.getString("url");
//                    String jiexi = object.getString("jiexi");
//                    urls.setSuccess(true);
//                    if (url.startsWith("http") && url.contains(".m3u")) {
//                        stringMap.put("标清", url);
//                        urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
//                        urls.setUrlType(IPlayUrls.URL_M3U8);
//                    } else if (url.startsWith("http") && (url.contains(".mp4") || url.contains(".avi") || url.contains(".rm") || url.contains("wmv"))) {
//                        stringMap.put("标清", url);
//                        urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//                        urls.setUrlType(IPlayUrls.URL_FILE);
//                    } else if (!TextUtils.isEmpty(jiexi) && jiexi.startsWith("http")) {
//                        stringMap.put("标清", jiexi + url);
//                        urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                        urls.setUrlType(IPlayUrls.URL_WEB);
//                    } else {
//                        urls.setSuccess(false);
//                    }
//                    urls.setUrls(stringMap);
//                    urls.setReferer(RetrofitManager.REQUEST_URL);
//                }
//            }
//            if (stringMap.isEmpty()) {
                stringMap.put("标清", RetrofitManager.REQUEST_URL);
                urls.setUrls(stringMap);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                urls.setUrlType(IPlayUrls.URL_WEB);
                urls.setSuccess(true);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return urls;
    }

}
