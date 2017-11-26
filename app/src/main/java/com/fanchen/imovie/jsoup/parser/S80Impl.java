package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.s80.S80Details;
import com.fanchen.imovie.entity.s80.S80Episode;
import com.fanchen.imovie.entity.s80.S80Home;
import com.fanchen.imovie.entity.s80.S80Video;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/9/23.
 */
public class S80Impl implements IVideoParser {

    @Override
    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        S80Home root = new S80Home();
        try {
            List<S80Video> videos = new ArrayList<>();
            for (Node n : node.list("div.col-xs-4.col-md-2.list_mov")){
                String cover = "http:" + n.attr("a > img", "data-original");
                String title = n.text("div.list_mov_title > h4");
                String tip = n.text("div.list_mov_title > em");
                String score = n.text("a > span.poster-score");
                String url = baseUrl + n.attr("a", "href");
                String id = "";
                if(!TextUtils.isEmpty(url)){
                    String[] split = url.split("/");
                    id = split[split.length - 1];
                }
                S80Video video = new S80Video();
                video.setTitle(title);
                video.setCover(cover);
                video.setExtras(tip);
                video.setGrade(score);
                video.setUrl(url);
                video.setId(id);
                videos.add(video);
            }
            root.setResult(videos);
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
        S80Details details = new S80Details();
        try {
            List<S80Episode> episodes = new ArrayList<>();
            List<S80Video> videos = new ArrayList<>();
            for (Node n : node.list("tr > td > a")){
                String href = n.attr("href");
                String title = n.text();
                if(!href.startsWith("thunder://") || "迅雷下载".equals(title.trim()) || "迅雷".equals(title.trim())){
                    continue;
                }
                S80Episode episode = new S80Episode();
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
                S80Video video = new S80Video();
                video.setTitle(title);
                video.setExtras(tip);
                video.setGrade(score);
                video.setUrl(url);
                video.setId(id);
                videos.add(video);
            }
            details.setCover("http:"+ node.attr("img.img-responsive.col-xs-6","src"));
            details.setEpisodes(episodes);
            details.setRecoms(videos);
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
        S80Home root = new S80Home();
        Node node = new Node(html);
        try{
            List<S80Video> videos = new ArrayList<>();
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
                S80Video video = new S80Video();
                video.setTitle(title);
                video.setExtras(tip);
                video.setGrade(score);
                video.setUrl(url);
                video.setId(id);
                videos.add(video);
            }
            root.setResult(videos);
            root.setSuccess(true);
        }catch (Exception e){
            root.setSuccess(false);
            e.printStackTrace();
        }
        return root;
    }

}
