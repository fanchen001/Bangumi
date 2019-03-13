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
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.SmdyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 神马电影
 * Created by fanchen on 2017/12/23.
 */
public class SmdyImpl implements IVideoMoreParser {

    private String clazz = SmdyService.class.getName();

    public SmdyImpl(){
    }

    public SmdyImpl(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot) home(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Video> videos = new ArrayList<>();
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.text("div > h2");
                String cover = n.attr("a > div > img", "data-original");
                if (TextUtils.isEmpty(cover))
                    continue;
                String score = n.textAt("div > p", 1);
                String author = n.textAt("div > p", 2);
                String hd = n.textAt("div > p", 3);
                String area = n.text("div > p", 4);
                String url = baseUrl + n.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(clazz);
                video.setCover(cover);
                if(clazz.equals(SmdyService.class.getName())){
                    video.setId(url);
                }else{
                    video.setId(n.attr("a", "href", "/", 2).replace(".html",""));
                }
                video.setType(area);
                video.setDanmaku(score);
                video.setExtras(author);
                video.setTitle(TextUtils.isEmpty(title) ? n.text("h2") : title);
                video.setUrl(url);
                video.setType(hd);
                videos.add(video);
            }
            home.setList(videos);
            home.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Node> modo_title = node.list("div.modo_title.top");
            if (modo_title != null && modo_title.size() > 0) {
                List<VideoBanner> banners = new ArrayList<>();
                for (Node n : node.list("ul.focusList > li")) {
                    VideoBanner banner = new VideoBanner();
                    banner.setServiceClass(clazz);
                    banner.setCover(n.attr("a > img", "data-src"));
                    banner.setId(n.attr("a", "href", "/", 4));
                    if(TextUtils.isEmpty(banner.getId())){
                        banner.setId(n.attr("a", "href", "/", 2));
                    }
                    banner.setTitle(n.text("a > span"));
                    banner.setUrl(baseUrl + n.attr("a", "href"));
                    banners.add(banner);
                }
                home.setHomeBanner(banners);
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                home.setHomeResult(titles);
                for (Node n : modo_title) {
                    String topTitle = n.text("h2");
                    String topUrl = n.attr("i > a", "href");
                    String topId = n.attr("i > a", "href", "-", 0).replace("/dytt/", "").replaceAll("/", "");
                    List<Video> videos = new ArrayList<>();
                    VideoTitle videoTitle = new VideoTitle();
                    videoTitle.setTitle(topTitle);
                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    videoTitle.setId(topId);
                    videoTitle.setUrl(topUrl);
                    if ("Film".equals(topId) || "TV".equals(topId) || "dongman".equals(topId) || "show".equals(topId)) {
                        videoTitle.setMore(false);
                    } else if ("Movie".equals(topId) || "Tv".equals(topId) || "Cartoon".equals(topId) || "Variety".equals(topId)) {
                        videoTitle.setMore(false);
                    } else {
                        videoTitle.setMore(true);
                    }
                    videoTitle.setList(videos);
                    videoTitle.setServiceClass(clazz);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")) {
                        String title = sub.text("a > div > label.name");
                        if (TextUtils.isEmpty(title))
                            title = sub.attr("a", "title");
                        String cover = sub.attr("a > div > img", "data-original");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("a > div > label.title");
                        String score = sub.text("a > div > label.score");
                        String author = sub.text("p");
                        String url = baseUrl + sub.attr("a", "href");
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(clazz);
                        video.setCover(cover);
                        if(clazz.equals(SmdyService.class.getName())){
                            video.setId(url);
                        }else{
                            video.setId(sub.attr("a", "href", "/", 2).replace(".html",""));
                        }
                        video.setDanmaku("评分:" + score);
                        video.setExtras("演员:" + author);
                        video.setTitle(TextUtils.isEmpty(title) ? sub.text("h2") : title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                    if (videos.size() > 0)
                        titles.add(videoTitle);
                }
            } else {
                List<Video> videos = new ArrayList<>();
                for (Node n : node.list("div > div > ul > li")) {
                    String title = n.text("h2");
                    if (TextUtils.isEmpty(title))
                        title = n.text("a > div > label.name");
                    String cover = n.attr("a > div > img", "data-original");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    String score = n.text("a > div > label.score");
                    String author = n.text("p");
                    String hd = n.text("a > div > label.title");
                    String area = n.text("p");
                    String url = baseUrl + n.attr("a", "href");
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(clazz);
                    video.setCover(cover);
                    if(clazz.equals(SmdyService.class.getName())){
                        video.setId(url);
                    }else{
                        video.setId(n.attr("a", "href", "/", 2).replace(".html",""));
                    }
                    video.setType(area);
                    video.setDanmaku("评分:" + score);
                    video.setExtras("演员:" + author);
                    video.setTitle(TextUtils.isEmpty(title) ? n.text("h2") : title);
                    video.setUrl(url);
                    video.setType(hd);
                    videos.add(video);
                }
                home.setList(videos);
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
            for (Node n : node.list("ul.list_tab_img > li")) {
                String title = n.text("h2");
                String cover = n.attr("a > div > img", "data-original");
                if (TextUtils.isEmpty(cover)) continue;
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = baseUrl + n.attr("a", "href");
                Video video = new Video();
                video.setCover(cover);
                video.setServiceClass(clazz);
                if(clazz.equals(SmdyService.class.getName())){
                    video.setId(url);
                }else{
                    video.setId(n.attr("a", "href","/",2).replace(".html",""));
                }
                video.setTitle(title);
                video.setUrl(url);
                video.setDanmaku(score);
                video.setExtras(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div.play-title > span[id]");
            for (Node n : node.list("div.play-box > ul")) {
                for (Node sub : n.list("li")) {
                    if (list.size() > count) {
                        if(clazz.equals(SmdyService.class.getName()) && !list.get(count).text().contains("西瓜")){
                            continue;
                        }
                    }
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(clazz);
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    if (list.size() > count) {
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    } else {
                        episode.setTitle(sub.text());
                    }
                    episodes.add(episode);
                }
                count++;
            }
            details.setCover(node.attr("div.vod-n-img > img.loading", "data-original"));
            details.setLast(node.textAt("div.vod-n-l > p", 0));
            details.setExtras(node.textAt("div.vod-n-l > p", 1));
            details.setDanmaku(node.textAt("div.vod-n-l > p", 2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            details.setServiceClass(clazz);
            details.setRecomm(videos);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls urls = new VideoPlayUrls();
        Map<String, String> url = new HashMap<>();
        urls.setUrls(url);
        try {
            int start = html.indexOf("ftp:");
            String playerUrl = new Node(html).attr("iframe", "src");
            String tempHtml = "";
            if (start >= 0) {
                tempHtml = html.substring(start);
                int end = tempHtml.indexOf(".mp4");
                if (end >= 0) {
                    tempHtml = tempHtml.substring(0, end + 4);
                }
                if (!TextUtils.isEmpty(tempHtml)) {
                    url.put("标清", tempHtml.replace("ftp", "xg"));
                    urls.setUrls(url);
                    urls.setUrlType(IPlayUrls.URL_XIGUA);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                    urls.setSuccess(true);
                }
            }else if(!TextUtils.isEmpty(playerUrl)){
                if(playerUrl.startsWith("//")){
                    playerUrl = "http:" + playerUrl;
                }else if(playerUrl.startsWith("/")){
                    playerUrl = baseUrl + playerUrl;
                }
                url.put("标清",playerUrl);
                urls.setUrlType(VideoPlayUrls.URL_WEB);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                urls.setReferer(baseUrl);
                urls.setUrls(url);
                urls.setSuccess(true);
            }else{
                urls.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (url.isEmpty()) {
                urls.setUrlType(VideoPlayUrls.URL_WEB);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                url.put("标清", RetrofitManager.REQUEST_URL);
            }
            urls.setSuccess(true);
        }
        return urls;
    }

}
