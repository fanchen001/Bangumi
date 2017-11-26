package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.kankan.KankanwuBanner;
import com.fanchen.imovie.entity.kankan.KankanwuDetails;
import com.fanchen.imovie.entity.kankan.KankanwuEpisode;
import com.fanchen.imovie.entity.kankan.KankanwuHome;
import com.fanchen.imovie.entity.kankan.KankanwuPlayUrl;
import com.fanchen.imovie.entity.kankan.KankanwuTitle;
import com.fanchen.imovie.entity.kankan.KankanwuVideo;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;
import com.fanchen.imovie.util.SystemUtil;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/10/28.
 */
public class KankanwuImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        KankanwuHome home = new KankanwuHome();
        try {
            List<KankanwuVideo> videos = new ArrayList<>();
            home.setResult(videos);
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.text("a > div > label.name");
                String cover = n.attr("a > div > img", "src");
                String clazz = n.textAt("div.list_info > p", 0);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a", "href");
                String id = n.attr("a", "href", "/", 2);
                KankanwuVideo video = new KankanwuVideo();
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
        KankanwuHome home = new KankanwuHome();
        try {
            List<Node> list = node.list("div.modo_title.top");
            if (list != null && list.size() > 0) {
                List<Node> ullist = node.list("ul.focusList > li.con");
                if(ullist != null && ullist.size() > 0){
                    List<KankanwuBanner> banners = new ArrayList<>();
                    for (Node n : ullist){
                        KankanwuBanner banner = new KankanwuBanner();
                        banner.setCover(n.attr("a > img","src"));
                        banner.setId(n.attr("a","href","/", 2));
                        banner.setTitle(n.text("a > span"));
                        banner.setUrl(baseUrl + n.attr("a","href"));
                        banners.add(banner);
                    }
                    home.setBanners(banners);
                }
                int count = 0;
                List<KankanwuTitle> titles = new ArrayList<>();
                home.setList(titles);
                for (Node n : list) {
                    String topTitle = n.text("h2");
                    String topUrl = n.attr("i > a", "href");
                    String topId = n.attr("i > a", "href", "/", 1);
                    List<KankanwuVideo> videos = new ArrayList<>();
                    KankanwuTitle kankanwuTitle = new KankanwuTitle();
                    kankanwuTitle.setTitle(topTitle);
                    kankanwuTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    kankanwuTitle.setId(topId);
                    kankanwuTitle.setUrl(topUrl);
                    kankanwuTitle.setList(videos);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")) {
                        String title = sub.text("a > div > label.name");
                        String cover = sub.attr("a > div > img", "src");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("a > div > label.title");
                        String url = baseUrl + sub.attr("a", "href");
                        String id = sub.attr("a", "href", "/", 2);
                        KankanwuVideo video = new KankanwuVideo();
                        video.setCover(cover);
                        video.setId(id);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                    if (videos.size() > 0)
                        titles.add(kankanwuTitle);
                    kankanwuTitle.setMore(kankanwuTitle.getList().size() == 6 || kankanwuTitle.getList().size() == 3);
                }
            } else {
                List<KankanwuVideo> videos = new ArrayList<>();
                for (Node n : node.list("div > div > ul > li")) {
                    String title = n.text("h2");
                    String cover = n.attr("a > div > img", "src");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    String hd = n.text("a > div > label.title");
                    String area = n.text("p");
                    String url = baseUrl + n.attr("a", "href");
                    String id = n.attr("a", "href", "/", 2);
                    KankanwuVideo video = new KankanwuVideo();
                    video.setCover(cover);
                    video.setId(id);
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
        KankanwuDetails details = new KankanwuDetails();
        try {
            List<KankanwuEpisode> episodes = new ArrayList<>();
            List<KankanwuVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul.list_tab_img > li")) {
                String title = n.text("h2");
                String cover = n.attr("a > div > img", "src");
                if (TextUtils.isEmpty(cover) || cover.contains("http://tu.lsmmr.com"))
                    continue;
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = baseUrl + n.attr("a", "href");
                String id = n.attr("a", "href", "/", 2);
                KankanwuVideo video = new KankanwuVideo();
                video.setCover(cover);
                video.setId(id);
                video.setTitle(title);
                video.setUrl(url);
                video.setAuthor(score);
                video.setType(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div.play-title > span[id]");
            for (Node n : node.list("div.play-box > ul")) {
                for (Node sub : n.list("li")) {
                    KankanwuEpisode episode = new KankanwuEpisode();
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
            details.setClazz(node.textAt("div.vod-n-l > p", 0));
            details.setType(node.textAt("div.vod-n-l > p", 1));
            details.setAuthor(node.textAt("div.vod-n-l > p", 2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            details.setRecoms(videos);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        KankanwuPlayUrl playUrl = new KankanwuPlayUrl();
        try {
            String attr = new Node(html).attr("iframe", "src", "=", 1);
            if (!TextUtils.isEmpty(attr)) {
                Map<String, String> mapUrl = new HashMap<>();
                mapUrl.put("标清", attr);
                playUrl.setUrls(mapUrl);
                playUrl.setUrlType(IPlayUrls.URL_M3U8);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                playUrl.setSuccess(true);
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
