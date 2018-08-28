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
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.BobmaoService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * 山猫视频
 * Created by fanchen on 2017/12/23.
 */
public class BobmaoImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot)home(retrofit,baseUrl,html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try{
            List<Video> videos = new ArrayList<>();
            LogUtil.e("size","size => " + node.list("div.column-model.clearfix > dl").size());
            for (Node n : node.list("div.column-model.clearfix > dl")){
                String title = n.text("dd > h2");
                String cover = n.attr("dt > a", "style").replace("background-image:url('","").replace("')","");
                if (TextUtils.isEmpty(cover))
                    continue;
                if(!cover.startsWith("http"))
                    cover = baseUrl + cover;
                String hd = n.text("dt > a > i.last");
                String url = baseUrl + n.attr("dt > a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(BobmaoService.class.getName());
                video.setCover(cover);
                video.setId(n.attr("dt > a", "href","/",2));
                video.setTitle(title);
                video.setUrl(url);
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
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Node> list = node.list("div.column-channel.column-data.column-w");
            if(list == null || list.size() <= 1){
                List<Video> videos = new ArrayList<>();
                for (Node n : node.list("div.column-model.clearfix > dl")){
                    String title = n.text("dd > h2");
                    String cover = n.attr("dt > a", "style").replace("background-image:url('","").replace("')","");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    if(!cover.startsWith("http"))
                        cover = baseUrl + cover;
                    String hd = n.text("dt > a > i.last");
                    String url = baseUrl + n.attr("dt > a", "href");
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(BobmaoService.class.getName());
                    video.setCover(cover);
                    video.setId( n.attr("dt > a", "href","/",2));
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setType(hd);
                    videos.add(video);
                }
                home.setList(videos);
            }else{
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                home.setHomeResult(titles);
                for (Node n : list) {
                    Node first = n.first("div.column-tit");
                    String topTitle = first.text("h3");
                    String topUrl = first.attr("ul > li.active > a", "href");
                    String topId = first.attr("ul > li.active > a", "href").replace(".html","").replace("/channel/index_","");
                    List<Video> videos = new ArrayList<>();
                    VideoTitle videoTitle = new VideoTitle();
                    videoTitle.setTitle(topTitle);
                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    videoTitle.setId(topId);
                    videoTitle.setUrl(topUrl);
                    videoTitle.setPageStart(2);
                    videoTitle.setList(videos);
                    videoTitle.setServiceClass(BobmaoService.class.getName());
                    for (Node sub : n.first("div.column-model.clearfix").list("dl")) {
                        String title = sub.text("dd > h2");
                        String cover = sub.attr("dt > a", "style").replace("background-image:url('","").replace("')","");
                        if (TextUtils.isEmpty(cover))
                            continue;
                        if(!cover.startsWith("http"))
                            cover = baseUrl + cover;
                        String hd = sub.text("dt > a > i.last");
                        String url = baseUrl + sub.attr("dt > a", "href");
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(BobmaoService.class.getName());
                        video.setCover(cover);
                        video.setId(sub.attr("dt > a", "href","/",2));
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                    if (videos.size() > 0)
                        titles.add(videoTitle);
                    videoTitle.setMore(videoTitle.getList().size() == 12);
                }
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
            for (Node n : node.list("div.column-model.clearfix > dl")) {
                String title = n.text("dd > h2");
                String cover = n.attr("dt > a", "style").replace("background-image:url('","").replace("')", "");
                if (TextUtils.isEmpty(cover))
                    continue;
                if(!cover.startsWith("http"))
                    cover = baseUrl + cover;
                String hd = n.text("dt > a > i.last");
                String url = baseUrl + n.attr("dt > a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(BobmaoService.class.getName());
                video.setCover(cover);
                video.setId(url);
                video.setTitle(title);
                video.setUrl(url);
                video.setType(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div#stab1 > div.playfrom.tab8.clearfix > ul > li");
            for (Node n : node.list("div.videourl.clearfix > ul")) {
                for (Node sub : n.list("li")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(BobmaoService.class.getName());
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    if (list.size() > count) {
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    } else {
                        episode.setTitle(sub.text());
                    }
                    if(!episode.getTitle().contains("搜狐")){
                        episodes.add(episode);
                    }
                }
                count++;
            }
            details.setCover(node.attr("div.detail-theme > div > dl > dt > a","style").replace("background-image:url('","").replace("')", ""));
            details.setLast(node.textAt("div.detail-theme > div > dl > dd > p", 0));
            details.setExtras(node.textAt("div.detail-theme > div > dl > dd > p", 1));
            details.setDanmaku(node.textAt("div.detail-theme > div > dl > dd > p", 2));
            details.setTitle(node.text("div.detail-theme > div > dl > dd > h1"));
            details.setIntroduce(node.text("div.des-con > p"));
            details.setEpisodes(episodes);
            details.setRecomm(videos);
            details.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {
            String unescape = JavaScriptUtil.match("unescape[\\w\\d\\('%]+'\\)", html, 0);
            if(TextUtils.isEmpty(unescape)){
                unescape = JavaScriptUtil.match("unescape\\(\\\"[.\\-_@|$=?/,:;\\w\\d\\(\\)\\[\\]'%]+\\\"\\)", html, 0);
            }
            LogUtil.e("unescape","unescape => " + unescape);

            String match = "function(){return " + unescape + ";}";
            String s = JavaScriptUtil.callFunction(match);
            LogUtil.e("unescape","s => " + s);
            String[] split = s.split("\\$\\$\\$");
            String[] splitUrl = RetrofitManager.REQUEST_URL.split("-");
            Map<String,String> map = new HashMap<>();
            playUrl.setUrls(map);
            if (split.length > Integer.valueOf(splitUrl[1])) {
                String[] urls = split[Integer.valueOf(splitUrl[1])].split("\\$\\$");
                for (int j = 1; j < urls.length; j += 2) {
                    String[] ids = urls[j].split("#");
                    for (int k = 0; k < ids.length; k++) {
                        if (k == Integer.valueOf(splitUrl[2].replace(".html", ""))) {
                            String[] strings = ids[k].split("\\$");
                            if(strings[1].startsWith("ftp") || strings[1].startsWith("xg")){
                                map.put(strings[0],strings[1]);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                                playUrl.setUrlType(IPlayUrls.URL_XIGUA);
                                playUrl.setSuccess(true);
                            }else if(strings[1].contains("m3u8")){
                                map.put(strings[0],strings[1]);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                                playUrl.setUrlType(IPlayUrls.URL_M3U8);
                                playUrl.setSuccess(true);
                            }else{
                                map.put(strings[0],String.format("https://www.bobmao.com/api/?url=%s",strings[1]));
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                                playUrl.setUrlType(IPlayUrls.URL_WEB);
                                playUrl.setSuccess(true);
                            }
                        }
                    }
                }
            }
            if(map.isEmpty()){
                map.put("标清",RetrofitManager.REQUEST_URL);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

}
