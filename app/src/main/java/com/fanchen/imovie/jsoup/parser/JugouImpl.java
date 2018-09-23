package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoTitle;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.service.JugouService;

import java.util.ArrayList;
import java.util.List;

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
        List<Video> videos = new ArrayList<>();
        try {
            for (Node n : node.list("div.item.clearfix")){
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(JugouService.class.getName());
                String style = n.attr("dl > dt > a", "style").replace("background: url(","").replace(") no-repeat; background-position:50% 50%; background-size: cover;","");
                video.setCover(style);
                video.setTitle(n.text("h3"));
                video.setUrl(n.attr("dl > dt > a", "href"));
                video.setId(video.getUrl());
                video.setType(n.text("div.score"));
                video.setDanmaku(n.text("ul > li"));
                videos.add(video);
            }
            root.setSuccess(true);
            root.setList(videos);
        }catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome root = new VideoHome();
        try {
            List<Node> list = node.list("div.hy-video-head");
            if (list != null && list.size() > 0) {
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                for (Node n : list) {
                    VideoTitle title = new VideoTitle();
                    titles.add(title);
                    title.setServiceClass(JugouService.class.getName());
                    title.setUrl(baseUrl + "/" + n.attr("li > a", "href"));
                    title.setTitle(n.text("h3"));
                    title.setId(title.getUrl());
                    title.setDrawable(SEASON[count++ % SEASON.length]);
                    List<Video> videos = new ArrayList<>();
                    title.setList(videos);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div.col-md-2.col-sm-3.col-xs-4")) {
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(JugouService.class.getName());
                        video.setCover(sub.attr("a", "src"));
                        video.setTitle(sub.text("div.title"));
                        video.setUrl(baseUrl + "/" + sub.attr("a", "href"));
                        video.setId(video.getUrl());
                        video.setType(sub.text("div.subtitle.text-muted.text-muted.text-overflow.hidden-xs"));
                        video.setDanmaku(sub.text("a > span.score"));
                        videos.add(video);
                    }
                    if (videos.size() > 0) {
                        title.setMore(true);
                    }
                }
                root.setSuccess(true);
                root.setHomeResult(titles);
            } else {
                List<Video> videos = new ArrayList<>();
                for (Node n : node.list("div.item > ul.clearfix > div")) {
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(JugouService.class.getName());
                    video.setCover(n.attr("a", "src"));
                    video.setTitle(n.text("div.title"));
                    video.setUrl(baseUrl + "/" + n.attr("a", "href"));
                    video.setId(video.getUrl());
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
            details.setIntroduce(node.text("div.plot"));
            details.setLast("未知时间");
            details.setAuthor("未知演员");
            int source = 1;
            for (Node n : node.list("div[id^=playlist]")) {
                for (Node sub : n.list("ul > li > a")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(JugouService.class.getName());
                    episode.setTitle("播放源" + source + ":" + sub.text());
                    String href = sub.attr("href");
                    if (href.startsWith("/")) {
                        if (href.contains("http")) {
                            if (href.contains(".m3u")) {
                                episode.setPlayType(VideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                            } else {
                                episode.setPlayType(VideoEpisode.PLAY_TYPE_VIDEO_WEB);
                            }
                            episode.setUrl(href.split("=")[1]);
                        } else {
                            episode.setPlayType(VideoEpisode.PLAY_TYPE_VIDEO_WEB);
                            episode.setUrl(baseUrl + href);
                        }
                    } else if (href.contains(".m3u")) {
                        episode.setPlayType(VideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                        episode.setUrl(href);
                    } else if (href.startsWith("http")) {
                        episode.setPlayType(VideoEpisode.PLAY_TYPE_VIDEO_WEB);
                        if(href.contains("letv")){
                            episode.setUrl(String.format(" http://jx.918jx.com/jx.php?url=%s", href));
                        }else if(href.contains("http://v.qq.com")){
                            episode.setUrl(String.format("http://api.bbbbbb.me/vip/?url=%s", href));
                        }else if(href.contains("http://v.youku.com")||href.contains("iqiyi")){
                            episode.setUrl(String.format("http://17kyun.com/api.php?url=%s", href));
                        }else{
                            episode.setUrl(String.format("https://vip.xinjueqio.cn/odflv/?url=%s", href));
                        }
                    } else {
                        episode.setUrl(baseUrl + "/" + href);
                        episode.setPlayType(VideoEpisode.PLAY_TYPE_VIDEO_WEB);
                    }
                    episode.setId(sub.attr("href", "/", 1));
                    episodes.add(episode);
                }
                source++;
            }
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
        throw new RuntimeException("this method not impl");
    }
}
