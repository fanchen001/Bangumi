package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.a4dy.A4dyDetails;
import com.fanchen.imovie.entity.a4dy.A4dyEpisode;
import com.fanchen.imovie.entity.a4dy.A4dyHome;
import com.fanchen.imovie.entity.a4dy.A4dyPlayUrl;
import com.fanchen.imovie.entity.a4dy.A4dyTitle;
import com.fanchen.imovie.entity.a4dy.A4dyVideo;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.JavaScriptUtil;
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

    private static final String YOUKU = "http://h1.aayyc.com/ckplayer/%s/index.m3u8?vid=%s&height=270";
    private static final String MGTV = "http://h1.aayyc.com/ckplayer/%s/index.hchc?id=%s&height=270";
    private static final String BILIBILI = "http://h1.aayyc.com/h5/%s.m3u8?cid=%s&height=270";
    private static final String WEIYUN = "http://404erbh.com/%s/index.mp4?name=%s&height=270";
    private static final String TENCENT = "http://h1.aayyc.com/ckplayer/%s/indexvip.2fz?vid=%s&height=270";
    private static final String OTHER = "http://h1.aayyc.com/ckplayer/%s/indexh5.m3u8?tvid=%s&height=270";

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        A4dyHome home = new A4dyHome();
        try {
            List<A4dyVideo> videos = new ArrayList<>();
            home.setResult(videos);
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.text("a > div > label.name");
                String cover = n.attr("a > div > img", "data-original");
                String update = n.textAt("div.list_info > p", 4);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a", "href");
                String id = n.attr("a", "href", "-", 3);
                A4dyVideo video = new A4dyVideo();
                video.setCover(cover);
                video.setId(id);
                video.setTitle(title);
                video.setUrl(url);
                video.setAuthor(author);
                video.setUpdate(update);
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
        A4dyHome home = new A4dyHome();
        try {
            List<Node> list = node.list("div.modo_title.top");
            if (list != null && list.size() > 0) {
                int count = 0;
                List<A4dyTitle> titles = new ArrayList<>();
                home.setList(titles);
                for (Node n : list) {
                    A4dyTitle A4dyTitle = new A4dyTitle();
                    String topTitle = n.text("h2");
                    String topUrl = n.attr("i > a", "href");
                    if (!topUrl.equals("[matrix:link]")) {
                        String topId = n.attr("i > a", "href", "-", 3);
                        A4dyTitle.setId(topId);
                        A4dyTitle.setUrl(baseUrl + topUrl);
                        A4dyTitle.setMore(true);
                    } else {
                        A4dyTitle.setId("");
                        A4dyTitle.setUrl("");
                        A4dyTitle.setMore(false);
                    }
                    List<A4dyVideo> videos = new ArrayList<>();
                    A4dyTitle.setTitle(topTitle);
                    A4dyTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    A4dyTitle.setList(videos);
                    titles.add(A4dyTitle);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")) {
                        String title = sub.text("a > div > label.name");
                        String cover = sub.attr("a > div > img", "data-original");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("a > div > label.title");
                        String url = baseUrl + sub.attr("a", "href");
                        String id = sub.attr("a", "href", "-", 3);
                        A4dyVideo video = new A4dyVideo();
                        video.setCover(cover);
                        video.setId(id);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                }
            } else {
                List<A4dyVideo> videos = new ArrayList<>();
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
                    A4dyVideo video = new A4dyVideo();
                    video.setCover(cover);
                    video.setId(id);
                    video.setUpdate(score);
                    video.setAuthor(area);
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setType(hd);
                    videos.add(video);
                }
                home.setResult(videos);
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
        A4dyDetails details = new A4dyDetails();
        try {
            List<A4dyEpisode> episodes = new ArrayList<>();
            int count = 0;
            List<Node> list = node.list("div.play-title > span[id]");
            String episodeurl = "";
            for (Node n : node.list("div.play-box > ul")) {
                for (Node sub : n.list("li")) {
                    A4dyEpisode episode = new A4dyEpisode();
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
            details.setType(node.textAt("div.vod-n-l > p", 1));
            details.setAuthor(node.textAt("div.vod-n-l > p", 2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            if (!TextUtils.isEmpty(episodeurl)) {
                String s = StreamUtil.url2String(episodeurl);
                if (!TextUtils.isEmpty(s)) {
                    List<A4dyVideo> videos = new ArrayList<>();
                    for (Node n : new Node(s).list("ul.list_tab_img > li")) {
                        String title = n.text("h2");
                        String cover = n.attr("a > div > img", "data-original");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        String hd = n.text("a > div > label.title");
                        String score = n.text("a > div > label.score");
                        String url = baseUrl + n.attr("a", "href");
                        String id = n.attr("a", "href", "-", 3);
                        A4dyVideo video = new A4dyVideo();
                        video.setCover(cover);
                        video.setId(id);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setAuthor(score);
                        video.setType(hd);
                        videos.add(video);
                    }
                    details.setRecom(videos);
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
        A4dyPlayUrl playUrl = new A4dyPlayUrl();
        try {
            String[] split = RetrofitManager.REQUEST_URL.replace(".html", "").split("-");
            String url = "";
            int count = 0;
            int xianlu = 0;
            if (split.length >= 5) {
                xianlu = Integer.valueOf(split[5]) - 1;
            }
            if (split.length >= 8) {
                count = Integer.valueOf(split[7]) - 1;
            }
            String from = JavaScriptUtil.match("mac_from='[\\d\\w$]+'", html, 0, 10, 1);
            String jscode = "function(){ return " + JavaScriptUtil.match("unescape[\\w\\d\\('%]+'\\);", html, 0) + "}";
            String callFunction = JavaScriptUtil.callFunction(jscode);
            String[] fromSplit = from.split("\\$\\$\\$");
            String[] callSplit = callFunction.split("\\$\\$\\$");
            if (fromSplit.length != callSplit.length || callSplit.length <= xianlu)
                return playUrl;
            String[] strings = callSplit[xianlu].split("#");
            for (int i = 0; i < strings.length; i++) {
                if (i != count) continue;
                switch (fromSplit[xianlu]) {
                    case "sohu":
                    case "youku":
                        url = String.format(YOUKU, fromSplit[xianlu], strings[i].split("\\$")[1]);
                        break;
                    case "mgtv":
                        url = String.format(MGTV, fromSplit[xianlu], strings[i].split("\\$")[1]);
                        break;
                    case "bilibili":
                        url = String.format(BILIBILI, fromSplit[xianlu], strings[i].split("\\$")[1]);
                        break;
                    case "weiyun":
                        url = String.format(WEIYUN, fromSplit[xianlu], strings[i].split("\\$")[1]);
                        break;
                    case "qqtencent":
                        url = String.format(TENCENT, fromSplit[xianlu], strings[i].split("\\$")[1]);
                        break;
                    default:
                        url = String.format(OTHER, fromSplit[xianlu], strings[i].split("\\$")[1]);
                        break;

                }
            }
            if (!TextUtils.isEmpty(url)) {
                Map<String, String> mapUrl = new HashMap<>();
                mapUrl.put("标清", url);
                playUrl.setSuccess(true);
                playUrl.setPlayType(IPlayUrls.URL_WEB);
                playUrl.setUrls(mapUrl);
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
