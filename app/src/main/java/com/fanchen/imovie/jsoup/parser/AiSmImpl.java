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
import com.fanchen.imovie.retrofit.service.AismService;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 神马电影
 * Created by fanchen on 2017/12/23.
 */
public class AiSmImpl implements IVideoMoreParser {

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
            for (Node n : node.list("ul#data_list > li")) {
                String title = n.text("div > span.sTit");
                String cover = n.attr("img", "data-src");
                if (TextUtils.isEmpty(cover))
                    continue;
                String score = n.textAt("div > p > span", 0);
                String author = n.textAt("div > p > span", 1);
                String hd = n.textAt("div > p > span", 2);
                String area = n.text("div > p > span", 3);
                String url = baseUrl + n.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(AismService.class.getName());
                video.setCover(cover);
                video.setId(url);
                video.setType(area);
                video.setDanmaku(score);
                video.setExtras(author);
                video.setTitle(TextUtils.isEmpty(title) ? n.text("h2") : title);
                video.setUrl(url);
                video.setType(hd);
                video.setUrlReferer(baseUrl);
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
            List<Node> ullist = node.list("ul#focusCon > li");
            if (ullist != null && ullist.size() > 0) {
                List<VideoBanner> banners = new ArrayList<>();
                for (Node n : ullist) {
                    VideoBanner banner = new VideoBanner();
                    banner.setServiceClass(AismService.class.getName());
                    banner.setCover(n.attr("a > img", "src"));
                    banner.setId(baseUrl + n.attr("a", "href"));
                    banner.setTitle(n.text("a > span"));
                    banner.setAgent(true);
                    banner.setUrl(baseUrl + n.attr("a", "href"));
                    banners.add(banner);
                }
                home.setHomeBanner(banners);
                List<Node> list = node.list("div.mod_a.globalPadding");
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                home.setHomeResult(titles);
                for (Node n : list) {
                    Node first = n.first("div.th_a");
                    String topTitle = first.text("span");
                    String topUrl = first.attr("a", "href");
                    String topId = topUrl.split("/")[1];
                    List<Video> videos = new ArrayList<>();
                    VideoTitle videoTitle = new VideoTitle();
                    videoTitle.setTitle(topTitle);
                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    videoTitle.setId(topId);
                    videoTitle.setUrl(topUrl);
                    videoTitle.setList(videos);
                    videoTitle.setServiceClass(AismService.class.getName());
                    for (Node sub : n.first("div.tb_a").list("ul > li")) {
                        String title = sub.text("div > a > span.sTit");
                        String cover = sub.attr("div > a > div > img", "data-src");
                        if (TextUtils.isEmpty(cover))
                            cover = sub.attr("div > a > img", "data-src");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("div > a > div > span");
                        String author = sub.text("div > a span.sDes");
                        String url = baseUrl + sub.attr("a", "href");
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(AismService.class.getName());
                        video.setCover(cover);
                        video.setId(url);
                        video.setUrlReferer(baseUrl);
                        video.setDanmaku(author);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                    if (videos.size() > 0)
                        titles.add(videoTitle);
                    videoTitle.setMore(false);
                }
            } else {
                List<IVideo> videos = new ArrayList<>();
                for (Node n : node.list("ul#data_list > li")) {
                    String title = n.text("div > a > span.sTit");
                    String cover = n.attr("div > a > img", "data-src");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    String update = n.attr("div > a > img", "alt");
                    String url = baseUrl + n.attr("a", "href");
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(AismService.class.getName());
                    video.setCover(cover);
                    video.setId(url);
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setType(update);
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
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.text("div > a > span");
                String cover = n.attr("div > a > img", "src");
                if (TextUtils.isEmpty(cover))
                    continue;
                String url = baseUrl + n.attr("div > a", "href");
                Video video = new Video();
                video.setCover(cover);
                video.setServiceClass(AismService.class.getName());
                video.setId(url);
                video.setDanmaku("");
                video.setExtras("");
                video.setTitle(title);
                video.setUrl(url);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("dl.tab2 > div > div.con.clearfix");
            for (Node n : node.list("dl.tab2 > dd > ul")) {
                for (Node sub : n.list("li")) {
                    if (sub.attr("a", "href").startsWith("ed2k") || sub.attr("a", "href").startsWith("magnet")
                            || sub.attr("a", "href").startsWith("ftp")) continue;
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(AismService.class.getName());
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    if (list.size() > count) {
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    } else {
                        episode.setTitle(sub.text());
                    }
                    if(episode.getTitle().contains("迅雷")) {
                        episode.setPlayType(IVideoEpisode.PLAY_TYPE_XUNLEI);
                        String replace = episode.getUrl().replace(baseUrl, "");
                        episode.setUrl(replace);
                        episodes.add(episode);
                    }else if (!episode.getTitle().contains("网盘") && !episode.getTitle().contains("下载")) {
                        episodes.add(episode);
                    }
                }
                count++;
            }
            details.setCover(node.attr("div.posterPic > a > img", "src"));
            details.setLast(node.textAt("ul#movie_info_ul > li", 0));
            details.setExtras(node.textAt("ul#movie_info_ul > li", 1));
            details.setDanmaku(node.textAt("ul#movie_info_ul > li", 2));
            details.setTitle(node.text("div.introTxt > h1"));
            details.setIntroduce(node.text("div.introTxt > div > p"));
            details.setEpisodes(episodes);
            details.setServiceClass(AismService.class.getName());
            details.setRecomm(videos);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        LogUtil.e("playUrl"," baseUrl -> " + baseUrl);
        VideoPlayUrls urls = new VideoPlayUrls();
        Node node = new Node(html);
        try {
            String src = node.attr("iframe", "src");
            String urlString = node.attr("iframe", "src", "=", 1);
            String[] split = urlString.split("~");
            Map<String, String> url = new HashMap<>();
            urls.setUrls(url);
            if (src.startsWith("ftp")) {
                url.put("标清", src);
                urls.setUrlType(VideoPlayUrls.URL_XIGUA);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
            } else if (split.length == 2) {
                if ("m3u8".equals(split[1])) {
                    if (split[0].contains("zuixinbo")) {
                        url.put("标清", split[0]);
                        urls.setUrlType(VideoPlayUrls.URL_M3U8);
                        urls.setPlayType(IVideoEpisode.PLAY_TYPE_ZZPLAYER);
                    } else {
                        url.put("标清", split[0]);
                        urls.setUrlType(VideoPlayUrls.URL_M3U8);
                        urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                    }
                } else {
                    if (src.startsWith("//")) {
                        src = "http:" + src;
                    } else if(src.startsWith("/")){
                        src = baseUrl + src;
                    }
                    byte[] bytes = StreamUtil.url2byte(src,StreamUtil.getHeader(baseUrl));
                    if (bytes != null && bytes.length > 1) {
                        String s = new String(bytes);
                        String videoUrl = new Node(s).attr("iframe", "src");
                        if (videoUrl.startsWith("//")) {
                            videoUrl = "http:" + videoUrl;
                        } else if(videoUrl.startsWith("/")){
                            videoUrl = "http://" + src.split("/")[2] + videoUrl;
                        }
                        url.put("标清", videoUrl);
                    } else {
                        url.put("标清", src);
                    }
                    urls.setUrlType(VideoPlayUrls.URL_WEB);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                }
            } else {
                if (src.startsWith("//")) {
                    src = "http:" + src;
                } else if(src.startsWith("/")){
                    src = baseUrl + src;
                }
                url.put("标清", src);
                urls.setUrlType(VideoPlayUrls.URL_WEB);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
            }
            urls.setM3u8Referer(true);
            urls.setReferer(baseUrl);
            urls.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

}
