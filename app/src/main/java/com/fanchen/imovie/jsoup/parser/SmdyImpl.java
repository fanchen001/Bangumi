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
import com.fanchen.imovie.retrofit.service.SmdyService;
import com.fanchen.imovie.retrofit.service.ZhandiService;
import com.fanchen.imovie.retrofit.service.ZzyoService;
import com.fanchen.imovie.retrofit.service.ZzzvzService;
import com.fanchen.imovie.util.LogUtil;

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

    private boolean isAgent = false;
    private boolean isReferer = false;
    private String clazz = SmdyService.class.getName();

    public SmdyImpl() {
    }

    public SmdyImpl(String clazz) {
        this(clazz, false,false);
    }

    public SmdyImpl(String clazz, boolean isAgent,boolean isReferer) {
        this.clazz = clazz;
        this.isAgent = isAgent;
        this.isReferer = isReferer;
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
            List<IVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.text("div > h2");
                String cover = n.attr("a > div > img", "data-original");
                if (TextUtils.isEmpty(cover))
                    continue;
                if(cover.contains("=")){
                    cover = cover.split("=")[1];
                }
                String score = n.textAt("div > p", 1);
                String author = n.textAt("div > p", 2);
                String hd = n.textAt("div > p", 3);
                String area = n.textAt("div > p", 4);
                String url = RetrofitManager.warpUrl(baseUrl,n.attr("a", "href"));
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(clazz);
                if(isReferer)
                    video.setUrlReferer(baseUrl);
                video.setAgent(isAgent);
                video.setCover(cover);
                if (clazz.equals(SmdyService.class.getName()) || clazz.equals(ZzyoService.class.getName())) {
                    video.setId(url);
                } else {
                    video.setId(n.attr("a", "href", "/", 2).replace(".html", ""));
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
        List<String> moreKeys = getMoreKeys();
        VideoHome home = new VideoHome();
        try {
            List<Node> modo_title = node.list("div.modo_title.top");
            if (modo_title != null && modo_title.size() > 0) {
                List<VideoBanner> banners = new ArrayList<>();
                for (Node n : node.list("ul.focusList > li")) {
                    VideoBanner banner = new VideoBanner();
                    banner.setServiceClass(clazz);
                    String cover = n.attr("a > img", "data-src");
                    if(cover.contains("=")){
                        cover = cover.split("=")[1];
                    }
                    banner.setCover(cover);
                    if(isReferer)
                        banner.setUrlReferer(baseUrl);
                    if (clazz.equals(SmdyService.class.getName()) || clazz.equals(ZzyoService.class.getName())) {
                        banner.setId(RetrofitManager.warpUrl(baseUrl,n.attr("a", "href")));
                    } else {
                        banner.setId(n.attr("a", "href", "/", 4).replace(".html",""));
                        if (TextUtils.isEmpty(banner.getId())) {
                            banner.setId(n.attr("a", "href", "/", 2).replace(".html",""));
                        }
                    }
                    banner.setAgent(isAgent);
                    banner.setTitle(n.text("a > span"));
                    banner.setUrl(RetrofitManager.warpUrl(baseUrl,n.attr("a", "href")));
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
                    if("star".equals(topId))continue;
                    videoTitle.setUrl(topUrl);
                    videoTitle.setMore(!moreKeys.contains(topId));
                    if(ZhandiService.class.getName().equals(clazz)){
                        videoTitle.setMore(!topId.contains(".html"));
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
                        if(cover.contains("=")){
                            cover = cover.split("=")[1];
                        }
                        String hd = sub.text("a > div > label.title");
                        String score = sub.text("a > div > label.score");
                        String author = sub.text("p");
                        String url = RetrofitManager.warpUrl(baseUrl,sub.attr("a", "href"));
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(clazz);
                        video.setCover(cover);
                        if (clazz.equals(SmdyService.class.getName()) || clazz.equals(ZzyoService.class.getName())) {
                            video.setId(url);
                        } else {
                            video.setId(sub.attr("a", "href", "/", 2).replace(".html", ""));
                        }
                        if(isReferer)
                            video.setUrlReferer(baseUrl);
                        video.setAgent(isAgent);
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
                List<IVideo> videos = new ArrayList<>();
                for (Node n : node.list("div > div > ul > li")) {
                    String title = n.text("h2");
                    if (TextUtils.isEmpty(title))
                        title = n.text("a > div > label.name");
                    String cover = n.attr("a > div > img", "data-original");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    if(cover.contains("=")){
                        cover = cover.split("=")[1];
                    }
                    String score = n.text("a > div > label.score");
                    String author = n.text("p");
                    String hd = n.text("a > div > label.title");
                    String area = n.text("p");
                    String url = RetrofitManager.warpUrl(baseUrl,n.attr("a", "href"));
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(clazz);
                    video.setCover(cover);
                    if (clazz.equals(SmdyService.class.getName()) || clazz.equals(ZzyoService.class.getName())) {
                        video.setId(url);
                    } else {
                        video.setId(n.attr("a", "href", "/", 2).replace(".html", ""));
                    }
                    if(isReferer)
                        video.setUrlReferer(baseUrl);
                    video.setAgent(isAgent);
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
                if (TextUtils.isEmpty(cover))
                    continue;
                if(cover.contains("=")){
                    cover = cover.split("=")[1];
                }
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = RetrofitManager.warpUrl(baseUrl,n.attr("a", "href"));
                Video video = new Video();
                video.setCover(cover);
                video.setServiceClass(clazz);
                if (clazz.equals(SmdyService.class.getName()) || clazz.equals(ZzyoService.class.getName())) {
                    video.setId(url);
                } else {
                    video.setId(n.attr("a", "href", "/", 2).replace(".html", ""));
                }
                if(isReferer)
                    video.setUrlReferer(baseUrl);
                video.setAgent(isAgent);
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
                        if (clazz.equals(SmdyService.class.getName()) && !list.get(count).text().contains("西瓜")) {
                            continue;
                        }
                    }
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(clazz);
                    episode.setId(RetrofitManager.warpUrl(baseUrl,sub.attr("a", "href")));
                    episode.setUrl(RetrofitManager.warpUrl(baseUrl,sub.attr("a", "href")));
                    if (list.size() > count) {
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    } else {
                        episode.setTitle(sub.text());
                    }
                    episodes.add(episode);
                }
                count++;
            }
            String cover = node.attr("div.vod-n-img > img.loading", "data-original");
            if(cover.contains("=")){
                cover = cover.split("=")[1];
            }
            details.setCover(cover);
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
            } else if (!TextUtils.isEmpty(playerUrl)) {
                if (playerUrl.startsWith("//")) {
                    playerUrl = "http:" + playerUrl;
                } else if (playerUrl.startsWith("/")) {
                    playerUrl = baseUrl + playerUrl;
                }
                if(playerUrl.contains("=") && playerUrl.split("=")[1].contains(".m3u")){
                    url.put("标清", playerUrl.split("=")[1]);
                    urls.setUrlType(VideoPlayUrls.URL_M3U8);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                }else{
                    url.put("标清", playerUrl);
                    urls.setUrlType(VideoPlayUrls.URL_WEB);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                }
                urls.setReferer(baseUrl);
                urls.setUrls(url);
                urls.setSuccess(true);
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

    private List<String> getMoreKeys() {
        List<String> list = new ArrayList<>();
        list.add("Film");
        list.add("TV");
        list.add("dongman");
        list.add("show");
        list.add("Variety");
        list.add("Tv");
        list.add("Cartoon");
        list.add("Variety");
        list.add("dianshiju");
        list.add("dianying");
        list.add("zongyi");
        list.add("weidianying");
        list.add("film");
        list.add("tv");
        list.add("wei");
        list.add("dongman");
        list.add("zongyi");
        list.add("start");
        list.add("Dianshiju");
        return list;
    }
}
