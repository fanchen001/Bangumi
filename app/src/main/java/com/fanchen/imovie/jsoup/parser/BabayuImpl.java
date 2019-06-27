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
import com.fanchen.imovie.retrofit.service.BabayuService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * BabayuImpl
 * Created by fanchen on 2017/10/28.
 */
public class BabayuImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<IVideo> videos = new ArrayList<>();
            home.setList(videos);
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.text("a > div > label.name");
                String cover = n.attr("a > div > img", "data-original");
                String clazz = n.textAt("div.list_info > p", 0);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(BabayuService.class.getName());
                video.setCover(cover);
                video.setId(url);
                video.setTitle(title);
                video.setUrl(url);
                video.setDanmaku(author);
                video.setLast(clazz);
                video.setExtras(type);
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
        VideoHome home = new VideoHome();
        try {
            List<Node> list = node.list("div.modo_title.top");
            if (list != null && list.size() > 0) {
                List<Node> ullist = node.list("ul.focusList > li.con > a");
                if (ullist != null && ullist.size() > 0) {
                    List<VideoBanner> banners = new ArrayList<>();
                    for (Node n : ullist) {
                        VideoBanner banner = new VideoBanner();
                        String attr = n.attr("img", "data-src");
                        if(TextUtils.isEmpty(attr)){
                            attr = n.attr("img", "src");
                        }
                        LogUtil.e("home","=>" + attr);
                        banner.setCover(attr);
                        banner.setId(n.attr("a", "href"));
                        banner.setTitle(n.text("span"));
                        banner.setServiceClass(BabayuService.class.getName());
                        banner.setUrl(n.attr("href"));
                        banners.add(banner);
                    }
                    home.setHomeBanner(banners);
                }
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                home.setHomeResult(titles);
                for (Node n : list) {
                    String topTitle = n.text("h2");
                    String topUrl = n.attr("i > a", "href");
                    String topId = n.attr("i > a", "href", "/", 1);
                    List<Video> videos = new ArrayList<>();
                    VideoTitle videoTitle = new VideoTitle();
                    videoTitle.setTitle(topTitle);
                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    videoTitle.setId(topId);
                    videoTitle.setUrl(topUrl);
                    videoTitle.setList(videos);
                    videoTitle.setServiceClass(BabayuService.class.getName());
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")) {
                        String title = sub.text("a > div > label.name");
                        if (TextUtils.isEmpty(title))
                            title = sub.attr("a", "title");
                        String cover = sub.attr("a > div > img", "data-original");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("a > div > label.title");
                        String url = baseUrl + sub.attr("a", "href");
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(BabayuService.class.getName());
                        video.setCover(cover);
                        video.setId(url);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setExtras(hd);
                        videos.add(video);
                    }
                    if (videos.size() > 0)
                        titles.add(videoTitle);
                    videoTitle.setMore(videoTitle.getList().size() == 6 || videoTitle.getList().size() == 3);
                }
            } else {
                List<IVideo> videos = new ArrayList<>();
                for (Node n : node.list("div > div > ul > li")) {
                    String title = n.text("h2");
                    if (TextUtils.isEmpty(title))
                        title = n.attr("a","title");
                    String cover = n.attr("a > div > img", "data-original");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    String hd = n.text("a > div > label.title");
                    String area = n.text("p");
                    String url = baseUrl + n.attr("a", "href");
                    Video video = new Video();
                    video.setServiceClass(BabayuService.class.getName());
                    video.setHasDetails(true);
                    video.setCover(cover);
                    video.setId(url);
                    video.setDanmaku(area);
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setExtras(hd);
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
        details.setServiceClass(BabayuService.class.getName());
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            List<Video> videos = new ArrayList<>();
            for (Node n : node.list("ul.list_tab_img > li")) {
                String title = n.text("h2");
                String cover = n.attr("a > div > img", "data-original");
                if (TextUtils.isEmpty(cover))
                    continue;
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = baseUrl + n.attr("a", "href");
                Video video = new Video();
                video.setCover(cover);
                video.setServiceClass(BabayuService.class.getName());
                video.setId(url);
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
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(BabayuService.class.getName());
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
            int start = html.indexOf("ftp:");
            String tempHtml = "";
            if (start >= 0) {
                tempHtml = html.substring(start);
                int end = tempHtml.indexOf(".mp4");
                if (end >= 0) {
                    tempHtml = tempHtml.substring(0, end + 4);
                }
            }
            Map<String, String> mapUrl = new HashMap<>();
            if (!TextUtils.isEmpty(tempHtml)) {
                mapUrl.put("标清", tempHtml.replace("ftp", "xg"));
                playUrl.setUrls(mapUrl);
                playUrl.setUrlType(IPlayUrls.URL_XIGUA);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                playUrl.setSuccess(true);
            } else {
                String attr = new Node(html).attr("iframe", "src");
                if (!TextUtils.isEmpty(attr)) {
                    mapUrl.put("标清", attr);
                    playUrl.setUrlType(IPlayUrls.URL_WEB);
                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                    playUrl.setUrls(mapUrl);
                    playUrl.setReferer(baseUrl);
                    playUrl.setSuccess(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot) home(retrofit, baseUrl, html);
    }

}
