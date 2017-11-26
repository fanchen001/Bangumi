package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.dm5.Dm5Details;
import com.fanchen.imovie.entity.dm5.Dm5Episode;
import com.fanchen.imovie.entity.dm5.Dm5Home;
import com.fanchen.imovie.entity.dm5.Dm5Title;
import com.fanchen.imovie.entity.dm5.Dm5PlayUrl;
import com.fanchen.imovie.entity.dm5.Dm5Video;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/10/2.
 */
public class Dm5Impl implements IVideoMoreParser {

    private static final String PLAYERURL = "https://xxxooo.duapp.com/ey.php?vid=%s";

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return more(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        Dm5Home root = new Dm5Home();
        try {
            int count = 0;
            List<Dm5Title> titles = new ArrayList<>();
            List<Node> list = node.list("div.smart-box-head");
            if (list != null && list.size() > 0) {
                for (Node n : list) {
                    Dm5Title title = new Dm5Title();
                    title.setTitle(n.text("h2.light-title.title"));
                    title.setMore(true);
                    title.setDrawable(SEASON[count++ % SEASON.length]);
                    title.setUrl(n.last("div.smart-control.pull-right > a").attr("href"));
                    String[] split = title.getUrl().split("/");
                    title.setId(split.length >= 6 ? split[5] : "");
                    List<Dm5Video> videos = new ArrayList<>();
                    title.setList(videos);
                    titles.add(title);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div.video-item")) {
                        Dm5Video video = new Dm5Video();
                        video.setTitle(sub.text("div.item-head"));
                        video.setCover(sub.attr("div > div > a > img", "data-original"));
                        video.setId(sub.attr("div > div > a", "href", "/", 4));
                        video.setUrl(sub.attr("div > div > a", "href"));
                        video.setUpdate(sub.text("span.item-date"));
                        video.setAuthor(sub.text("span.item-author"));
                        video.setExtras(sub.text("span.rating-bar.bgcolor2.time_dur"));
                        videos.add(video);
                    }
                }
                root.setTitles(titles);
            } else {
                List<Dm5Video> videos = new ArrayList<>();
                for (Node sub : node.list("div[id^=post-]")) {
                    Dm5Video video = new Dm5Video();
                    video.setTitle(sub.text("div.item-head"));
                    video.setCover(sub.attr("div > div > a > img", "data-original"));
                    video.setUrl(sub.attr("div > div > a", "href"));
                    video.setId(sub.attr("div > div > a", "href", "/", 4));
                    video.setUpdate(sub.text("span.item-date"));
                    video.setAuthor(sub.text("span.item-author"));
                    video.setExtras(sub.text("div.item-content.hidden"));
                    videos.add(video);
                }
                root.setList(videos);
            }
            root.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            root.setSuccess(false);
        }
        return root;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        Dm5Details details = new Dm5Details();
        try {
            details.setIntroduce(node.text("div.video-conent > div.item-content.toggled > p"));
            details.setExtras(node.text("div.video-conent > div.item-tax-list"));
            List<Dm5Episode> episodes = new ArrayList<>();
            details.setEpisodes(episodes);
            for (Node n : node.list("td > a.multilink-btn.btn.btn-sm.btn-default.bordercolor2hover.bgcolor2hover")) {
                Dm5Episode episode = new Dm5Episode();
                episode.setTitle(n.text());
                episode.setId(n.attr("href", "/", 4));
                episode.setUrl(n.attr("href"));
                episodes.add(episode);
            }
            details.setSuccess(true);
        } catch (Exception e) {
            details.setSuccess(false);
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        Dm5Home root = new Dm5Home();
        try {
            List<Dm5Video> videos = new ArrayList<>();
            for (Node sub : node.list("div[id^=post-]")) {
                Dm5Video video = new Dm5Video();
                video.setTitle(sub.text("div.item-head"));
                video.setCover(sub.attr("div > div > a > img", "data-original"));
                video.setUrl(sub.attr("div > div > a", "href"));
                video.setId(sub.attr("div > div > a", "href", "/", 4));
                video.setExtras(sub.text("div.item-content.hidden"));
                video.setUpdate(sub.text("span.item-date"));
                video.setAuthor(sub.text("span.item-author"));
                videos.add(video);
            }
            root.setList(videos);
            root.setSuccess(true);
        } catch (Exception e) {
            root.setSuccess(false);
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        Dm5PlayUrl playUrl = new Dm5PlayUrl();
        try {
            String fUrl = node.attr("iframe", "src");
            if (TextUtils.isEmpty(fUrl)) return playUrl;
            int aIndexOf = fUrl.indexOf("a=");
            if (aIndexOf != -1) {
                Map<String, String> map = new HashMap<>();
                String substring = fUrl.substring(aIndexOf + 2);
                int indexOf = substring.indexOf("&");
                if (indexOf != -1) {
                    map.put("标清", String.format(PLAYERURL, substring.substring(0, indexOf)));
                } else {
                    map.put("标清", String.format(PLAYERURL, substring));
                }
                playUrl.setUrls(map);
                playUrl.setSuccess(true);
            }
        } catch (Exception e) {
            playUrl.setSuccess(false);
            e.printStackTrace();
        }
        return playUrl;
    }
}
