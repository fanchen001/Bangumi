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
import com.fanchen.imovie.retrofit.service.JugouService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 聚狗影院
 * Created by fanchen on 2018/6/13.
 */
public class JugouImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot) home(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome root = new VideoHome();
        List<IVideo> videos = new ArrayList<>();
        try {
            for (Node n : node.list("div.item.clearfix")) {
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(JugouService.class.getName());
                String style = n.attr("dl > dt > a", "style").replace("background: url(", "").replace(")  no-repeat; background-position:50% 50%; background-size: cover;", "");
                if (TextUtils.isEmpty(style)) continue;
                video.setCover(style);
                video.setTitle(n.text("h3"));
                video.setUrl(RetrofitManager.warpUrl(baseUrl, n.attr("dl > dt > a", "href")));
                video.setId(n.attr("dl > dt > a", "href","/",2).replace("index", "").replace(".html", ""));
                video.setType(n.text("div.score"));
                video.setExtras(n.textAt("ul > li", 0));
                video.setDanmaku(n.textAt("ul > li", 1));
                videos.add(video);
            }
            root.setSuccess(true);
            root.setList(videos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome root = new VideoHome();
        try {
            List<Node> banList = node.list("div.swiper-wrapper > div > div.hy-video-slide > a");
            if (banList != null && !banList.isEmpty()) {
                List<VideoBanner> banners = new ArrayList<>();
                for (Node n : banList) {
                    String style = n.attr("style").replace("padding-top: 60%; background: url(", "").replace(")  no-repeat; background-position:50% 50%; background-size: cover;", "");
                    VideoBanner banner = new VideoBanner();
                    banner.setServiceClass(JugouService.class.getName());
                    banner.setCover(style);
                    banner.setId(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                    banner.setTitle(n.text("title"));
                    banner.setUrl(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                    banners.add(banner);
                }
                root.setHomeBanner(banners);
            }
            List<Node> list = node.list("div.hy-video-head");
            if (list != null && list.size() > 0) {
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                for (Node n : list) {
                    VideoTitle title = new VideoTitle();
                    title.setServiceClass(JugouService.class.getName());
                    String href = n.attr("li.active > a", "href");
                    String attr = n.attr("li.active > a", "href", "/", 2);
                    title.setUrl(RetrofitManager.warpUrl(baseUrl, href));
                    title.setTitle(n.text("h3"));
                    title.setPageStart(2);
                    if (attr.contains(".")) {
                        title.setId(attr.split("\\.")[0].replace("index", ""));
                    }
                    title.setDrawable(SEASON[count++ % SEASON.length]);
                    List<Video> videos = new ArrayList<>();
                    title.setList(videos);
                    Node nextNode = new Node(n.getElement().nextElementSibling());
                    List<Node> nextList = nextNode.list("div.col-md-2.col-sm-3.col-xs-4");
                    if (nextList == null || nextList.isEmpty())
                        nextList = nextNode.list("div.col-md-3.col-sm-3.col-xs-4");
                    for (Node sub : nextList) {
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(JugouService.class.getName());
                        video.setCover(sub.attr("a", "data-original"));
                        video.setTitle(sub.text("div.title"));
                        video.setUrl(RetrofitManager.warpUrl(baseUrl, sub.attr("a", "href")));
                        video.setId(sub.attr("a", "href", "/", 2).replace("index", "").replace(".html", ""));
                        video.setUpdate(sub.text("span.note.textbg"));
                        video.setType(sub.text("div.subtitle.text-muted.text-muted.text-overflow.hidden-xs"));
                        video.setDanmaku(sub.text("span.score"));
                        videos.add(video);
                    }
                    if (videos.size() > 0) {
                        titles.add(title);
                    }
                    title.setMore(title.getList().size() == 8 && !TextUtils.isEmpty(title.getId()));
                }
                root.setSuccess(true);
                root.setHomeResult(titles);
            } else {
                List<IVideo> videos = new ArrayList<>();
                for (Node n : node.list("div.item > ul.clearfix > div")) {
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(JugouService.class.getName());
                    video.setCover(n.attr("a", "src"));
                    video.setTitle(n.text("div.title"));
                    video.setUrl(RetrofitManager.warpUrl(baseUrl, n.attr("a", "href")));
                    video.setId(n.attr("a", "href","/",2).replace("index", "").replace(".html", ""));
                    video.setType(n.text("div.subtitle.text-muted.text-muted.text-overflow.hidden-xs"));
                    video.setDanmaku(n.text("a > span.score"));
                    videos.add(video);
                }
                root.setSuccess(true);
                root.setList(videos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoDetails details = new VideoDetails();
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            details.setTitle(node.text("div.head > h3"));
            details.setIntroduce(node.text("div.item > div.plot"));
            details.setLast(node.textAt("dd.clearfix > ul > li", 0));
            details.setAuthor(node.textAt("dd.clearfix > ul > li", 1));
            details.setUpdate(node.textAt("dd.clearfix > ul > li", 2));

            List<Video> reco = new ArrayList<>();
            List<Node> list = node.list("div.item > div.col-md-2.col-sm-3.col-xs-4");
            if (list == null || list.isEmpty())
                list = node.list("div.swiper-wrapper > div > div.item");
            for (Node n : list) {
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(JugouService.class.getName());
                String original = n.attr("a", "data-original");
                if (original.startsWith("http")) {
                    video.setCover(original);
                } else {
                    video.setCover(baseUrl + original);
                }
                video.setTitle(n.attr("a", "title"));
                String score = n.text("a > span.score");
                if (!TextUtils.isEmpty(score)) {
                    video.setDanmaku("评分:" + score);
                } else {
                    video.setDanmaku(n.text("span.note.textbg"));
                }
                String href = n.attr("a", "href");
                String[] split = href.split("/");
                if (split.length == 2) {
                    video.setId(split[1]);
                } else if (split.length == 3) {
                    video.setId(split[2].replace("index", "").replace(".html", ""));
                }
                if (href.startsWith(".")) {
                    video.setUrl(baseUrl + href.replace(".", ""));
                } else {
                    video.setUrl(baseUrl + href);
                }
                reco.add(video);
            }
            details.setRecomm(reco);

            int source = 1;
            for (Node n : node.list("div[id^=playlist]")) {
                for (Node sub : n.list("ul > li > a")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(JugouService.class.getName());
                    episode.setTitle("播放源" + source + ":" + sub.text());
                    String href = sub.attr("href");
                    if (href.startsWith(".")) {
                        episode.setUrl(baseUrl + href.substring(1));
                        episode.setId(href.split("/")[1].replace(".html", ""));
                    } else if (href.startsWith("/play")) {
                        episode.setUrl(baseUrl + href);
                        episode.setId(href.split("/")[2].replace(".html", ""));
                    } else if (href.startsWith("/")) {
                        episode.setUrl(baseUrl + href);
                        episode.setId(href.split("/")[1].replace(".html", ""));
                    } else {
                        episode.setUrl(href);
                        episode.setId(href);
                    }
                    episodes.add(episode);
                }
                source++;
            }
            details.setEpisodes(episodes);
            details.setServiceClass(JugouService.class.getName());
            details.setEpisodes(episodes);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls urls = new VideoPlayUrls();
        Map<String, String> stringMap = new HashMap<>();
        try {
            String match = JavaScriptUtil.match("var now=\"[\\{\\}\\[\\]\\\"\\w\\d第集`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\"\\;var pn", html, 0, 9, 8);
            LogUtil.e("JugouImpl", "match = -> " + match);
            if (!TextUtils.isEmpty(match)) {
                urls.setSuccess(true);
                if (match.startsWith("http") && match.contains(".m3u")) {
                    stringMap.put("标清", match);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                    urls.setUrlType(IPlayUrls.URL_M3U8);
                } else if (match.startsWith("http") && (match.contains(".mp4") || match.contains(".avi") || match.contains(".rm") || match.contains("wmv"))) {
                    stringMap.put("标清", match);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                    urls.setUrlType(IPlayUrls.URL_FILE);
                } else if (match.startsWith("http")) {
                    stringMap.put("标清", match);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                    urls.setUrlType(IPlayUrls.URL_WEB);
                } else {
                    urls.setSuccess(false);
                }
                urls.setUrls(stringMap);
                urls.setReferer(RetrofitManager.REQUEST_URL);
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
}
