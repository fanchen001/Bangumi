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
import com.fanchen.imovie.retrofit.service.WandouService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 豌豆视频
 * Created by fanchen on 2017/12/23.
 */
public class WandouImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<IVideo> videos = new ArrayList<>();
            for (Node sub : node.list("ul > li.col-md-2.col-sm-3.col-xs-4")) {
                String title = sub.text("h2");
                String cover = warpUrl(sub.attr("p > a > img", "data-original"),baseUrl);
                if (TextUtils.isEmpty(cover))
                    continue;
                String hd = sub.text("h4");
                String continu = sub.text("p > a > span.continu");
                String url = warpUrl(sub.attr("a", "href"),baseUrl);
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(WandouService.class.getName());
                video.setCover(cover);
                video.setId(sub.attr("a", "href", "/", 2));
                video.setTitle(title);
                video.setUrl(url);
                video.setUrlReferer(baseUrl);
                video.setDanmaku(continu);
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
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return more(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Node> ullist = node.list("div.carousel-inner > div > a");
            List<VideoBanner> banners = new ArrayList<>();
            for (Node n : ullist) {
                VideoBanner banner = new VideoBanner();
                banner.setServiceClass(WandouService.class.getName());
                String src = n.attr("img", "src");
                if(TextUtils.isEmpty(src)){
                    src = n.attr("img", "data-original");
                }
                banner.setCover(warpUrl(src,baseUrl));
                banner.setTitle(n.text("div.carousel-caption"));
                String href = n.attr("href");
                if (href.startsWith("http")) {
                    banner.setUrl(href);
                    banner.setId(n.attr("href", "/", 4));
                } else {
                    banner.setId(n.attr("href", "/", 2));
                    banner.setUrl(baseUrl + href);
                }
                banners.add(banner);
            }
            home.setHomeBanner(banners);
            int count = 0;
            List<VideoTitle> titles = new ArrayList<>();
            home.setHomeResult(titles);
            for (Node n : node.list("div.page-header")) {
                String topTitle = n.text("h2 > a");
                if (TextUtils.isEmpty(topTitle))
                    topTitle = n.text("h2").replace("更多", "");
                String topUrl = warpUrl(n.attr("h2 > small > a.btn.btn-success.btn-xs", "href"), baseUrl);
                String topId = n.attr("h2 > small > a.btn.btn-success.btn-xs", "href", "/", 3).split("-")[0];
                List<Video> videos = new ArrayList<>();
                VideoTitle videoTitle = new VideoTitle();
                videoTitle.setTitle(topTitle);
                videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                videoTitle.setId(topId);
                videoTitle.setUrl(topUrl);
                videoTitle.setList(videos);
                videoTitle.setServiceClass(WandouService.class.getName());
                for (Node sub : new Node(n.getElement().nextElementSibling()).list("ul > li")) {
                    String title = sub.text("h2");
                    String src = sub.attr("a > img", "src");
                    if(TextUtils.isEmpty(src)){
                        src = sub.attr("a > img", "data-original");
                    }
                    if (TextUtils.isEmpty(src))
                        continue;
                    String hd = sub.text("h4");
                    String continu = sub.text("p > a > span.continu");
                    String url =  warpUrl(sub.attr("a", "href"),baseUrl);
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(WandouService.class.getName());
                    video.setCover( warpUrl( src,baseUrl));
                    video.setId(sub.attr("a", "href", "/", 2));
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setDanmaku(continu);
                    video.setType(hd);
                    videos.add(video);
                }
                if (videos.size() > 0)
                    titles.add(videoTitle);
                videoTitle.setMore(videoTitle.getList().size() != 10 && !TextUtils.isEmpty(topId));
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
        details.setServiceClass(WandouService.class.getName());
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            List<Video> videos = new ArrayList<>();
            for (Node sub : node.list("li.col-md-2.col-sm-3.col-xs-4")) {
                String title = sub.text("h2");
                String src = sub.attr("a > img", "src");
                if(TextUtils.isEmpty(src)){
                    src = sub.attr("a > img", "data-original");
                }
                if (TextUtils.isEmpty(src))
                    continue;
                String hd = sub.text("h4");
                String continu = sub.text("p > a > span.continu");
                String url =  warpUrl(sub.attr("a", "href"),baseUrl);
                Video video = new Video();
                video.setHasDetails(true);
                LogUtil.e("WandouImpl"," Cover -> " + warpUrl(src,baseUrl));
                video.setServiceClass(WandouService.class.getName());
                video.setCover( warpUrl(src,baseUrl));
                video.setId(sub.attr("a", "href", "/", 2));
                video.setTitle(title);
                video.setUrl(url);
                video.setUrlReferer(baseUrl);
                video.setDanmaku(continu);
                video.setType(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("ul.nav.nav-tabs.ff-playurl-tab > li");
            for (Node n : node.list("div.tab-content.ff-playurl-tab > ul")) {
                for (Node sub : n.list("li")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(WandouService.class.getName());
                    episode.setId(sub.attr("a", "href", "/", 3));
                    episode.setUrl( warpUrl(sub.attr("a", "href"),baseUrl));
                    if (list.size() > count) {
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    } else {
                        episode.setTitle(sub.text());
                    }
                    episodes.add(episode);
                }
                count++;
            }
            details.setCover( warpUrl( node.attr("div.media-left > a > img", "data-original"),baseUrl));
            details.setLast(node.textAt("dl.dl-horizontal > dd", 0));
            details.setExtras(node.textAt("dl.dl-horizontal > dd", 1));
            details.setDanmaku(node.textAt("dl.dl-horizontal > dd", 2));
            details.setTitle(node.text("div.media-body > h2"));
            details.setUrlReferer(baseUrl);
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
        VideoPlayUrls urls = new VideoPlayUrls();
        try {
            String match = JavaScriptUtil.match("\\{\"[_&\\-=\\?/.,:\\w\\d\"\\\\]+\\}", html, 0);
            if (!TextUtils.isEmpty(match)) {
                JSONObject jsonObject = new JSONObject(match);
                String urlString = jsonObject.getString("url");
                Map<String, String> url = new HashMap<>();
                urls.setUrls(url);
                urls.setM3u8Referer(true);
                if (urlString.contains(".m3u")) {
                    urls.setReferer(RetrofitManager.REQUEST_URL);
                    url.put("标清", urlString);
                    urls.setUrlType(VideoPlayUrls.URL_M3U8);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                }else if(jsonObject.has("jiexi")){
                    String jiexi = jsonObject.getString("jiexi");
                    urls.setUrlType(VideoPlayUrls.URL_WEB);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                    urls.setReferer(baseUrl);
                    url.put("标清", jiexi + urlString);
                } else {
                    urls.setUrlType(VideoPlayUrls.URL_WEB);
                    urls.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                    String[] split = urlString.split("/");
                    String format = String.format("https://www.fantasy.tv/videoAd/video.html?id=%s&channelId=%s&code=%s", split[4], split[5], split[6]);
                    urls.setReferer(format);
                    url.put("标清", format);
                }
                urls.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    private String warpUrl(String href,String baseUrl){
        if(href.startsWith("//"))
            href = "http" + href;
        else if(href.startsWith("/"))
            href = baseUrl + href;
        return href;
    }
}
