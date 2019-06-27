package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.VideoTitle;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.Dm5Service;
import com.fanchen.imovie.util.StreamUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/10/2.
 */
public class Dm5Impl implements IVideoMoreParser {

    private static final String PLAYERURL = "https://xxxooo.duapp.com/%s?vid=%s";

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return more(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome root = new VideoHome();
        try {
            int count = 0;
            List<VideoTitle> titles = new ArrayList<>();
            List<Node> list = node.list("div.smart-box-head");
            if (list != null && list.size() > 0) {
                for (Node n : list) {
                    VideoTitle title = new VideoTitle();
                    title.setServiceClass(Dm5Service.class.getName());
                    title.setTitle(n.text("h2.light-title.title"));
                    title.setMore(true);
                    title.setDrawable(SEASON[count++ % SEASON.length]);
                    title.setUrl(n.last("div.smart-control.pull-right > a").attr("href"));
                    String[] split = title.getUrl().split("/");
                    title.setId(split.length >= 6 ? split[5] : "");
                    List<Video> videos = new ArrayList<>();
                    title.setList(videos);
                    titles.add(title);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div.video-item")) {
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(Dm5Service.class.getName());
                        video.setTitle(sub.text("div.item-head"));
                        String attr = sub.attr("div > div > a > img", "data-original");
                        if (attr.startsWith("http")) {
                            video.setCover(attr);
                        } else {
                            video.setCover(baseUrl + attr);
                        }
                        video.setId(sub.attr("div > div > a", "href", "/", 4));
                        video.setUrl(sub.attr("div > div > a", "href"));
                        video.setUpdate(sub.text("span.item-date"));
                        video.setAuthor(sub.text("span.item-author"));
                        video.setExtras(sub.text("span.rating-bar.bgcolor2.time_dur"));
                        videos.add(video);
                    }
                }
                root.setHomeResult(titles);
            } else {
                List<IVideo> videos = new ArrayList<>();
                for (Node sub : node.list("div[id^=post-]")) {
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(Dm5Service.class.getName());
                    video.setTitle(sub.text("div.item-head"));
                    String attr = sub.attr("div > div > a > img", "data-original");
                    if (attr.startsWith("http")) {
                        video.setCover(attr);
                    } else {
                        video.setCover(baseUrl + attr);
                    }
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
        VideoDetails details = new VideoDetails();
        details.setServiceClass(Dm5Service.class.getName());
        try {
            details.setIntroduce(node.text("div.video-conent > div.item-content.toggled > p"));
            details.setExtras(node.text("div.video-conent > div.item-tax-list"));
            details.setCanDownload(true);
            List<VideoEpisode> episodes = new ArrayList<>();
            details.setEpisodes(episodes);
            for (Node n : node.list("td > a.multilink-btn.btn.btn-sm.btn-default.bordercolor2hover.bgcolor2hover")) {
                VideoEpisode episode = new VideoEpisode();
                episode.setTitle(n.text());
                episode.setServiceClass(Dm5Service.class.getName());
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
        VideoHome root = new VideoHome();
        try {
            List<IVideo> videos = new ArrayList<>();
            for (Node sub : node.list("div[id^=post-]")) {
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(Dm5Service.class.getName());
                video.setTitle(sub.text("div.item-head"));
                String attr = sub.attr("div > div > a > img", "data-original");
                if (attr.startsWith("http")) {
                    video.setCover(attr);
                } else {
                    video.setCover(baseUrl + attr);
                }
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
        VideoPlayUrls playUrl = new VideoPlayUrls();
        playUrl.setReferer(RetrofitManager.REQUEST_URL);
        Map<String, String> map = new HashMap<>();
        playUrl.setUrls(map);
        try {
            String fUrl = node.attr("iframe", "src");
            String url2String = StreamUtil.url2String(fUrl);
            if(!TextUtils.isEmpty(url2String)){
                node = new Node(url2String);
                String sfUrl = node.attr("source","src");
                if(!TextUtils.isEmpty(sfUrl)){
                    map.put("标清", sfUrl);
                    playUrl.setSuccess(true);
                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                    playUrl.setUrlType(IPlayUrls.URL_FILE);
                }
            }
            if (map.isEmpty() && !TextUtils.isEmpty(fUrl)) {
                map.put("标清", fUrl);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setUrlType(IPlayUrls.URL_M3U8);
                playUrl.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (map.isEmpty()) {
            map.put("标清", RetrofitManager.REQUEST_URL);
            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
            playUrl.setUrlType(IPlayUrls.URL_WEB);
            playUrl.setUrls(map);
            playUrl.setReferer(RetrofitManager.REQUEST_URL);
            playUrl.setSuccess(true);
        }
        return playUrl;
    }
}
