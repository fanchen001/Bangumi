package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.Video;
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
import com.fanchen.imovie.retrofit.service.A4dyService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/11/2.
 */
public class A4dyImpl implements IVideoMoreParser {
    //


    private static final String BILIBILI = "http://h1.aayyc.com/h5/%s.m3u8?cid=%s&height=449";
    private static final String WEIYUN = "http://404erbh.com/%s/index.mp4?name=%s&height=449";
    private static final String OTHER = "http://h1.aayyc.com/ckplayer/%s/indexh5.m3u8?tvid=%s&height=449";
    private static final String ACFUN = "http://h1.aayyc.com/ckplayer/acfun/index-acfun.m3u8?vid=%s&height=449";
    private static final String LESHI = "http://h1.aayyc.com/ckplayer/letv/index.m3u8?vid=%s&height=449";
    private static final String PPTV = "http//h1.aayyc.com/ckplayer/pptv/index.m3u8?url=%s&height=449";
    private static final String MGTV = "http://h1.aayyc.com/ckplayer/mgtv/index.hchc?id=%s&height=449";
    private static final String YOUKU = "http://h1.aayyc.com/ckplayer/youku/index.m3u8?vid=%s&height=449";
    private static final String IQYI = "http://h1.aayyc.com/ckplayer/iqiyi/indexh5.m3u8?tvid=%s&height=449";
    private static final String YUN = "http://yingqian8.cn/weiyun/index.mp4?name=%s&height=449";
    private static final String YUNDUAN = "http://h1.aayyc.com/ckplayer/video1/index.m3u8?url=%s&height=449";
    //http://h1.aayyc.com/ckplayer/qqtencent/index.2fz?vid=2d798AQFnTJ8JjY7Oo3yg2SUXDRNIxoQsOqXIzIVrCqrhHgUmT+tQw&height=449
    private static final String TENCENT = "http://h1.aayyc.com/ckplayer/qqtencent/index.2fz?vid=%s&height=449";

//yunduan$$$iqiyi

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Video> videos = new ArrayList<>();
            home.setList(videos);
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.text("a > div > label.name");
                String cover = n.attr("a > div > img", "data-original");
                String update = n.textAt("div.list_info > p", 4);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a", "href");
                String id = n.attr("a", "href", "-", 3);
                Video video = new Video();
                video.setCover(cover);
                video.setId(id);
                video.setTitle(title);
                video.setUrl(url);
                video.setHasDetails(true);
                video.setServiceClass(A4dyService.class.getName());
                video.setDanmaku(author);
                video.setLast(update);
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
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                home.setHomeResult(titles);
                for (Node n : list) {
                    VideoTitle a4DyTitle = new VideoTitle();
                    a4DyTitle.setServiceClass(A4dyService.class.getName());
                    String topTitle = n.text("h2");
                    String topUrl = n.attr("i > a", "href");
                    if (!topUrl.equals("[matrix:link]")) {
                        String topId = n.attr("i > a", "href", "-", 3);
                        a4DyTitle.setId(topId);
                        a4DyTitle.setUrl(baseUrl + topUrl);
                        a4DyTitle.setMore(true);
                    } else {
                        a4DyTitle.setId("");
                        a4DyTitle.setUrl("");
                        a4DyTitle.setMore(false);
                    }
                    List<Video> videos = new ArrayList<>();
                    a4DyTitle.setTitle(topTitle);
                    a4DyTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    a4DyTitle.setList(videos);
                    titles.add(a4DyTitle);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")) {
                        String title = sub.text("a > div > label.name");
                        String cover = sub.attr("a > div > img", "data-original");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("a > div > label.title");
                        String url = baseUrl + sub.attr("a", "href");
                        String id = sub.attr("a", "href", "-", 3);
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(A4dyService.class.getName());
                        video.setCover(cover);
                        video.setId(id);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setExtras(hd);
                        videos.add(video);
                    }
                }
            } else {
                List<Video> videos = new ArrayList<>();
                for (Node n : node.list("div > div > ul > li")) {
                    String title = n.text("h2");
                    String cover = n.attr("a > div > img", "data-original");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    String hd = n.text("a > div > label.title");
                    String score = n.text("a > div > label.score");
                    String area = n.text("p");
                    String url = baseUrl + n.attr("a", "href");
                    String id = n.attr("a", "href", "-", 3);
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setCover(cover);
                    video.setId(id);
                    video.setServiceClass(A4dyService.class.getName());
                    video.setLast(score);
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
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            int count = 0;
            List<Node> list = node.list("div.play-title > span[id]");
            String episodeurl = "";
            for (Node n : node.list("div.play-box > ul")) {
                for (Node sub : n.list("li")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(A4dyService.class.getName());
                    episode.setId(sub.attr("a", "href", "/", 1));
                    episode.setUrl(episodeurl = baseUrl + sub.attr("a", "href"));
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
            details.setUpdate(node.textAt("div.vod-n-l > p", 5));
            details.setLast(node.textAt("div.vod-n-l > p", 1));
            details.setDanmaku(node.textAt("div.vod-n-l > p", 2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            if (!TextUtils.isEmpty(episodeurl)) {
                String s = StreamUtil.url2String(episodeurl);
                if (!TextUtils.isEmpty(s)) {
                    List<Video> videos = new ArrayList<>();
                    for (Node n : new Node(s).list("ul.list_tab_img > li")) {
                        String title = n.text("h2");
                        String cover = n.attr("a > div > img", "data-original");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        String hd = n.text("a > div > label.title");
                        String score = n.text("a > div > label.score");
                        String url = baseUrl + n.attr("a", "href");
                        String id = n.attr("a", "href", "-", 3);
                        Video video = new Video();
                        video.setCover(cover);
                        video.setId(id);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setDanmaku(score);
                        video.setLast(hd);
                        videos.add(video);
                    }
                    details.setRecomm(videos);
                }
            }
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        String url = RetrofitManager.REQUEST_URL;
        try {
            String[] split = RetrofitManager.REQUEST_URL.replace(".html", "").split("-");
            int count = 0;
            int xianlu = 0;
            if (split.length >= 5) {
                xianlu = Integer.valueOf(split[5]) - 1;
            }
            if (split.length >= 8) {
                count = Integer.valueOf(split[7]) - 1;
            }
            String from = JavaScriptUtil.match("mac_from='[\\d\\w$]+'", html, 0, 10, 1);
            String match = JavaScriptUtil.match("unescape[\\w\\d\\('%]+'\\);", html, 0);
            if (TextUtils.isEmpty(match)) {
                match = JavaScriptUtil.match("unescape\\([.\\-_@|$=?/,:;\\w\\d\\(\\)\\[\\]'%]+'\\);", html, 0);
            }
            String jscode = "function(){ return " + match + "}";
            String callFunction = JavaScriptUtil.callFunction(jscode);
            String[] fromSplit = from.split("\\$\\$\\$");
            String[] callSplit = callFunction.split("\\$\\$\\$");
            if (fromSplit.length == callSplit.length && callSplit.length > xianlu) {
                String[] strings = callSplit[xianlu].split("#");
                for (int i = 0; i < strings.length; i++) {
                    if (i != count) continue;
                    switch (fromSplit[xianlu]) {
                        case "sohu":
                        case "youku":
                            url = String.format(YOUKU, strings[i].split("\\$")[1]);
                            break;
                        case "mgtv":
                            url = String.format(MGTV,  strings[i].split("\\$")[1]);
                            break;
                        case "pptv":
                            url = String.format(PPTV,  strings[i].split("\\$")[1]);
                            break;
                        case "bilibili":
                            url = String.format(BILIBILI, fromSplit[xianlu], strings[i].split("\\$")[1]);
                            break;
                        case "weiyun":
                            url = String.format(WEIYUN, fromSplit[xianlu], strings[i].split("\\$")[1]);
                            break;
                        case "qqtencent":
                            url = String.format(TENCENT, strings[i].split("\\$")[1]);
                            break;
                        case "yunduan":
                        case "yun":
                            if (strings[i].split("\\$")[1].contains(".mp4")) {
                                url = String.format(YUN, strings[i].split("\\$")[1]);
                            } else {
                                url = String.format(YUNDUAN, strings[i].split("\\$")[1]);
                            }
                            break;
                        case "leshi":
                            url = String.format(LESHI, strings[i].split("\\$")[1]);
                            break;
                        case "iqiyi":
                            url = String.format(IQYI, strings[i].split("\\$")[1]);
                            break;
                        case "acfun":
                            url = String.format(ACFUN, strings[i].split("\\$")[1]);
                            break;
                        default:
                            if (strings[i].split("\\$")[1].startsWith("ftp:") || strings[i].split("\\$")[1].startsWith("xg:")) {
                                url = strings[i].split("\\$")[1];
                            } else {
                                url = String.format(OTHER, fromSplit[xianlu], strings[i].split("\\$")[1]);
                            }
                            break;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mapUrl = new HashMap<>();
            mapUrl.put("标清", url);
            playUrl.setSuccess(true);
            playUrl.setReferer("http://c.aaccy.com/");
            if ((url.contains(".mp4") || url.contains(".rm") || url.contains(".3gp")) && !url.contains("height=270")) {
                playUrl.setUrlType(VideoPlayUrls.URL_FILE);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
            } else {
                playUrl.setUrlType(VideoPlayUrls.URL_WEB);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
            }
            playUrl.setUrls(mapUrl);
        }
        return playUrl;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot) home(retrofit, baseUrl, html);
    }

}
