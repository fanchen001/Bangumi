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
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.service.TaihanService;
import com.fanchen.imovie.util.JavaScriptUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * TaihanImpl
 * Created by fanchen on 2018/4/19.
 */
public class TaihanImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try{
            List<Video> videos = new ArrayList<>();
            for (Node sub : node.list("ul > li.col-md-2.col-sm-3.col-xs-4")){
                String title = sub.text("h2");
                String cover = baseUrl + sub.attr("p > a > img", "data-original");
                if (TextUtils.isEmpty(cover))
                    continue;
                String hd = sub.text("h4");
                String continu = sub.text("p > a > span.continu");
                String url = baseUrl + sub.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(TaihanService.class.getName());
                video.setCover(cover);
                video.setId(sub.attr("a", "href","/",2));
                video.setTitle(title);
                video.setUrl(url);
                video.setDanmaku(continu);
                video.setType(hd);
                videos.add(video);
            }
            home.setList(videos);
            home.setSuccess(true);
        }catch (Exception e){
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
                banner.setServiceClass(TaihanService.class.getName());
                banner.setCover(baseUrl + n.attr("img", "src"));
                banner.setTitle(n.text("div.carousel-caption"));
                String href = n.attr("href");
                if(href.startsWith("http")){
                    banner.setUrl(href);
                    banner.setId(n.attr("href","/",4));
                }else{
                    banner.setId(n.attr("href","/",2));
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
                if(TextUtils.isEmpty(topTitle))
                    topTitle = n.text("h2").replace("更多","");
                String topUrl = baseUrl + n.attr("h2 > a", "href");
                String topId = n.attr("h2 > a", "href", "-", 3);
                List<Video> videos = new ArrayList<>();
                VideoTitle videoTitle = new VideoTitle();
                videoTitle.setTitle(topTitle);
                videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                videoTitle.setId(topId);
                videoTitle.setUrl(topUrl);
                videoTitle.setList(videos);
                videoTitle.setServiceClass(TaihanService.class.getName());
                for (Node sub : new Node(n.getElement().nextElementSibling()).list("ul > li")) {
                    String title = sub.text("h2");
                    String cover = baseUrl + sub.attr("p > a > img", "data-original");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    String hd = sub.text("h4");
                    String continu = sub.text("p > a > span.continu");
                    String url = baseUrl + sub.attr("a", "href");
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(TaihanService.class.getName());
                    video.setCover(cover);
                    video.setId(sub.attr("a", "href","/",2));
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setDanmaku(continu);
                    video.setType(hd);
                    videos.add(video);
                }
                if (videos.size() > 0)
                    titles.add(videoTitle);
                videoTitle.setMore(videoTitle.getList().size() != 10);
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
            for (Node sub : node.list("li.col-md-2.col-sm-3.col-xs-4")) {
                String title = sub.text("h2");
                String cover = baseUrl + sub.attr("p > a > img", "data-original");
                if (TextUtils.isEmpty(cover))
                    continue;
                String hd = sub.text("h4");
                String continu = sub.text("p > a > span.continu");
                String url = baseUrl + sub.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(TaihanService.class.getName());
                video.setCover(cover);
                video.setId(sub.attr("a", "href","/",2));
                video.setTitle(title);
                video.setUrl(url);
                video.setDanmaku(continu);
                video.setType(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div.page-header.ff-playurl-line");
            for (Node n : node.list("ul.list-unstyled.row.text-center.ff-playurl-line.ff-playurl")) {
                for (Node sub : n.list("li")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(TaihanService.class.getName());
                    episode.setId(sub.attr("a", "href","/",3));
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
            details.setCover(baseUrl + node.attr("div.media-left > a > img", "data-original"));
            details.setLast(node.textAt("dl.dl-horizontal > dd", 0));
            details.setExtras(node.textAt("dl.dl-horizontal > dd", 1));
            details.setDanmaku(node.textAt("dl.dl-horizontal > dd", 2));
            details.setTitle(node.text("div.media-body > h2"));
            details.setIntroduce(node.text("span.vod-content-default.text-justify"));
            details.setEpisodes(episodes);
            details.setRecomm(videos);
            details.setServiceClass(TaihanService.class.getName());
            details.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return details;
    }
    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls urls = new VideoPlayUrls();
        try{
            String match = JavaScriptUtil.match("\\{\"[=?/.,:\\w\\d\"\\\\]+\\}", html, 0);
            if(!TextUtils.isEmpty(match)){
                String urlString = new JSONObject(match).getString("url");
                Map<String,String> url = new HashMap<>();
                urls.setUrls(url);
                urls.setUrlType(VideoPlayUrls.URL_M3U8);
                urls.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                url.put("标清",urlString);
                urls.setSuccess(true);
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return urls;
    }

}
