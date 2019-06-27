package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.retrofit.service.S80Service;
import com.fanchen.imovie.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

/**
 * S80Impl
 * Created by fanchen on 2017/9/23.
 */
public class S80Impl implements IVideoParser {

    @Override
    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        VideoHome root = new VideoHome();
        try {
            List<IVideo> videos = new ArrayList<>();
            for (Node n : node.list("div.col-xs-4.col-md-2.list_mov")){
                String attr = n.attr("a > img", "data-original");
                if(TextUtils.isEmpty(attr)){
                    attr = n.attr("a > img", "src");
                }
                String cover = "http:" + attr;
                String title = n.text("div.list_mov_title > h4");
                String tip = n.text("div.list_mov_title > em");
                String score = n.text("a > span.poster-score");
                String url = baseUrl + n.attr("a", "href");
                String id = "";
                if(!TextUtils.isEmpty(url)){
                    String[] split = url.split("/");
                    id = split[split.length - 1];
                }
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(S80Service.class.getName());
                video.setTitle(title);
                video.setCover(cover);
                video.setExtras(tip);
                video.setDanmaku(score);
                video.setUrl(url);
                video.setId(id);
                videos.add(video);
            }
            root.setList(videos);
            root.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            root.setSuccess(false);
        }
        return root;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        VideoDetails details = new VideoDetails();
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            List<Video> videos = new ArrayList<>();
            for (Node n : node.list("tr > td > a")){
                String href = n.attr("href");
                if(!href.startsWith("thunder://")){
                    continue;
                }
                VideoEpisode episode = new VideoEpisode();
                episode.setServiceClass(S80Service.class.getName());
                episode.setPlayType(VideoEpisode.PLAY_TYPE_XUNLEI);
                episode.setTitle(n.text());
                episode.setUrl(href);
                episode.setId(n.attr("href", "/", 2));
                episodes.add(episode);
            }
            for (Node n : node.list("a.list-group-item")){
                String title = n.text("h4");
                String tip = n.text("p");
                String score = n.text("h4 > small");
                String url = baseUrl + n.attr("href");
                String id = "";
                if(!TextUtils.isEmpty(url)){
                    String[] split = url.split("/");
                    id = split[split.length - 1];
                }
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(S80Service.class.getName());
                video.setTitle(title);
                video.setExtras(tip);
                video.setDanmaku(score);
                video.setUrl(url);
                video.setId(id);
                videos.add(video);
            }
            details.setServiceClass(S80Service.class.getName());
            details.setCover("http:"+ node.attr("img.img-responsive.col-xs-6","src"));
            details.setEpisodes(episodes);
            details.setRecomm(videos);
            details.setIntroduce(node.text("div#movie_description"));
            details.setSuccess(true);
        }catch (Exception e){
            details.setSuccess(false);
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
        throw new RuntimeException("this method not impl");
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html) {
        VideoHome root = new VideoHome();
        Node node = new Node(html);
        try{
            List<IVideo> videos = new ArrayList<>();
            for (Node n : node.list("a.list-group-item")){
                String title = n.text("h4");
                String tip = n.text("em");
                String score = n.text("h4 > small");
                String url = baseUrl + n.attr("href");
                String id = "";
                if(!TextUtils.isEmpty(url)){
                    String[] split = url.split("/");
                    id = split[split.length - 1];
                }
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(S80Service.class.getName());
                video.setTitle(title);
                video.setExtras(tip);
                video.setDanmaku(score);
                video.setUrl(url);
                video.setId(id);
                videos.add(video);
            }
            root.setList(videos);
            root.setSuccess(true);
        }catch (Exception e){
            root.setSuccess(false);
            e.printStackTrace();
        }
        return root;
    }

}
