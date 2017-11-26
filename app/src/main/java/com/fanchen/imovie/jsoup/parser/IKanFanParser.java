package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.ikanfan.IKanFanDetails;
import com.fanchen.imovie.entity.ikanfan.IKanFanEpisode;
import com.fanchen.imovie.entity.ikanfan.IKanFanHome;
import com.fanchen.imovie.entity.ikanfan.IKanFanPlayUrl;
import com.fanchen.imovie.entity.ikanfan.IKanFanVideo;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.util.LogUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/11/16.
 */
public class IKanFanParser implements IVideoParser {

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        IKanFanHome home = new IKanFanHome();
        try {
            List<IKanFanVideo> videos = new ArrayList<>();
            home.setResult(videos);
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.attr("a", "title");
                String cover = n.attr("a > div > img", "data-original");
                String clazz = n.textAt("div.list_info > p", 0);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a", "href");
                String id = n.attr("a", "href", "/", 2);
                IKanFanVideo video = new IKanFanVideo();
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
        IKanFanHome IKanFanHome = new IKanFanHome();
        try {
            List<IKanFanVideo> videos = new ArrayList<>();
            for (Node n : node.list("div > div > ul#vod_list > li")) {
                IKanFanVideo video = new IKanFanVideo();
                video.setCover(n.attr("a > div > img", "data-original"));
                video.setTitle(n.text("h2"));
                video.setClazz(n.text("p"));
                video.setId(n.attr("a", "href", "/", 2));
                video.setUrl(baseUrl + n.attr("a", "href"));
                video.setAuthor(n.text("a > div > label.score"));
                video.setType(n.text("a > div > label.title"));
                videos.add(video);
            }
            if (videos.size() > 0) {
                IKanFanHome.setSuccess(true);
                IKanFanHome.setResult(videos);
            }
        } catch (Exception e) {
            IKanFanHome.setSuccess(false);
            IKanFanHome.setMessage(e.toString());
            e.printStackTrace();
        }
        return IKanFanHome;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        IKanFanDetails details = new IKanFanDetails();
        try {
            List<IKanFanEpisode> episodes = new ArrayList<>();
            List<IKanFanVideo> videos = new ArrayList<>();
            for (Node n : node.list("div.ikf-item.like > div > a")) {
                String title = n.text("h3");
                String cover = n.attr("div > img", "data-original");
                if (TextUtils.isEmpty(cover) || !cover.startsWith("http"))
                    continue;
                String hd = n.text("p");
                String score = n.text("em");
                String url = baseUrl + n.attr("href");
                String id = n.attr("href", "/", 2);
                IKanFanVideo video = new IKanFanVideo();
                video.setCover(cover);
                video.setId(id);
                video.setTitle(title);
                video.setUrl(url);
                video.setAuthor(score);
                video.setType(hd);
                videos.add(video);
            }
            for (Node n : node.list("ul.playlist > li")) {
                IKanFanEpisode episode = new IKanFanEpisode();
                episode.setId(baseUrl + n.attr("a", "href"));
                episode.setUrl(baseUrl + n.attr("a", "href"));
                episode.setTitle(n.text());
                episodes.add(episode);
            }
            details.setCover(node.attr("div.ikf-detail > div > a > img", "src"));
            details.setClazz(node.textAt("div.detail > p", 0));
            details.setType(node.textAt("div.detail > p", 1));
            details.setAuthor(node.textAt("div.detail > p", 2));
            details.setTitle(node.text("div.detail > h1"));
            details.setIntroduce(node.text("p.pTxt"));
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
        IKanFanPlayUrl playUrl = new IKanFanPlayUrl();
        try {
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("标清", new Node(html).attr("a.pandata","href"));
            playUrl.setUrls(stringMap);
            playUrl.setUrlType(IPlayUrls.URL_WEB);
            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB_V);
            playUrl.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e("playUrl => ",new Gson().toJson(playUrl));
        return playUrl;
    }
}
