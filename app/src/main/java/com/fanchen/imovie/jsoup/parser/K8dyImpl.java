package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoBanner;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.VideoTitle;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.K8dyService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.google.gson.Gson;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/10/28.
 */
public class K8dyImpl implements IVideoMoreParser {

    private static final String URL_MAT_1 = "https://ck.ee7e.com/jx.php?id=%s";
    private static final String URL_MAT_2 = "http://y.mt2t.com/lines?url=%s";

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<IVideo> videos = new ArrayList<>();
            home.setList(videos);
            for (Node n : node.list("ul#resize_list > li")){
                String title = n.text("a > div > label.name");
                String cover = n.attr("img", "src");
                if(cover.contains("=")){
                    cover = cover.split("=")[1];
                }
                String clazz = n.textAt("div.list_info > p", 0);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a","href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setAgent(true);
                video.setServiceClass(K8dyService.class.getName());
                video.setCover(cover);
                video.setId(url);
                video.setTitle(title);
                video.setUrl(url);
                video.setAuthor(author);
                video.setClazz(clazz);
                video.setType(type);
                videos.add(video);
            }
            home.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Node> list = node.listTagClass("div","modo_title top","modo_title ");
            if(list != null && list.size() > 2){
                List<Node> ullist = node.list("ul.focusList > li.con");
                if(ullist != null && ullist.size() > 0){
                    List<VideoBanner> banners = new ArrayList<>();
                    for (Node n : ullist){
                        VideoBanner banner = new VideoBanner();
                        String cover = n.attr("img", "src");
                        if(cover.contains("=")){
                            cover = cover.split("=")[1];
                        }
                        banner.setCover(cover);
                        banner.setId(baseUrl + n.attr("a", "href"));
                        banner.setTitle(n.text("a > span"));
                        banner.setUrl(baseUrl + n.attr("a", "href"));
                        banner.setAgent(true);
                        banner.setServiceClass(K8dyService.class.getName());
                        banners.add(banner);
                    }
                    home.setHomeBanner(banners);
                }
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                home.setHomeResult(titles);
                for (Node n : list){
                    String topTitle = n.text("h2");
                    String topUrl = n.attr("i > a", "href");
                    String topId = n.attr("i > a", "href","/",2).replace(".html", "");
                    List<Video> videos = new ArrayList<>();
                    VideoTitle videoTitle = new VideoTitle();
                    videoTitle.setTitle(topTitle);
                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    videoTitle.setId(topId);
                    videoTitle.setPageStart(2);
                    videoTitle.setUrl(topUrl);
                    videoTitle.setList(videos);
                    videoTitle.setServiceClass(K8dyService.class.getName());
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("li")){
                        String title = sub.text("a > div > label.name");
                        if(TextUtils.isEmpty(title))
                            title = sub.text("h2");
                        String cover = sub.attr("img", "data-original");
                        if(cover.contains("=")){
                            cover = cover.split("=")[1];
                        }
                        if(TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("a > div > label.title");
                        String score = n.text("a > div > label.score");
                        String url = baseUrl + sub.attr("a","href");
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(K8dyService.class.getName());
                        video.setCover(cover);
                        video.setId(url);
                        video.setAgent(true);
                        video.setAuthor(score);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                    if(videos.size() > 0)
                        titles.add(videoTitle);
                    videoTitle.setMore(videoTitle.getList().size() == 6);
                }
            }else{
                List<IVideo> videos = new ArrayList<>();
                for (Node n : node.list("ul#resize_list > li")){
                    String title = n.text("h2");
                    String cover = n.attr("img", "src");
                    if(cover.contains("=")){
                        cover = cover.split("=")[1];
                    }
                    if(TextUtils.isEmpty(cover))
                        continue;
                    String hd = n.text("a > div > label.title");
                    String score = n.text("a > div > label.score");
                    String url = baseUrl + n.attr("a","href");
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setAgent(true);
                    video.setServiceClass(K8dyService.class.getName());
                    video.setCover(cover);
                    video.setId(url);
                    video.setAuthor(score);
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setType(hd);
                    videos.add(video);
                }
                home.setList(videos);
            }
            home.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        LogUtil.e("Impl","n -> " + new Gson().toJson(home));
        return home;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        VideoDetails details = new VideoDetails();
        try {
            List<VideoEpisode> episodes = new ArrayList<>();
            List<Video> videos = new ArrayList<>();
            for (Node n : node.list("ul.list_tab_img > li")){
                String title = n.text("h2");
                String cover = n.attr("img", "src");
                if(cover.contains("=")){
                    cover = cover.split("=")[1];
                }
                if(TextUtils.isEmpty(cover))
                    continue;
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = baseUrl + n.attr("a","href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(K8dyService.class.getName());
                video.setCover(cover);
                video.setAgent(true);
                video.setId(url);
                video.setTitle(title);
                video.setUrl(url);
                video.setAuthor(score);
                video.setType(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div.play-title > span");
            for (Node n : node.list("ul.plau-ul-list")){
                for (Node sub : n.list("li")){
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(K8dyService.class.getName());
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    if(list.size() > count){
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    }else{
                        episode.setTitle(sub.text());
                    }
                    if(episode.getTitle().contains("迅雷")) {
                        episode.setPlayType(IVideoEpisode.PLAY_TYPE_XUNLEI);
                        String replace = episode.getUrl().replace(baseUrl, "");
                        episode.setUrl(replace);
                        episodes.add(episode);
                    }else if(!episode.getTitle().contains("网盘")){
                        episodes.add(episode);
                    }
                }
                count ++ ;
            }
            details.setServiceClass(K8dyService.class.getName());
            details.setCover(node.attr("div.vod-n-img > img.loading", "src","=",1));
            details.setClazz(node.textAt("div.vod-n-l > p", 0));
            details.setType(node.textAt("div.vod-n-l > p", 1));
            details.setAuthor(node.textAt("div.vod-n-l > p",2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            details.setRecomm(videos);
            details.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try{
            String match = JavaScriptUtil.match("VideoInfoList=\"[\\w\\d第集`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\"", html, 0, 15, 1);
            String[] split = match.split("\\$\\$\\$");
            String[] splitUrl = RetrofitManager.REQUEST_URL.split("-");
            if (split.length > Integer.valueOf(splitUrl[1])) {
                String[] urls = split[Integer.valueOf(splitUrl[1])].split("\\$\\$");
                for (int j = 1; j < urls.length; j += 2) {
                    String[] ids = urls[j].split("#");
                    for (int k = 0; k < ids.length; k++) {
                        if (k == Integer.valueOf(splitUrl[2].replace(".html", ""))) {
                            String[] strings = ids[k].split("\\$");
                            Map<String,String> map = new HashMap<>();
                            if(strings[1].startsWith("ftp:") || strings[1].startsWith("xg:")){
                                playUrl.setReferer(RetrofitManager.REQUEST_URL);
                                map.put(strings[0],strings[1]);
                            }else{
                                if(strings[1].startsWith("http")){
                                    playUrl.setReferer(String.format("http://yun.mt2t.com/yun?url=%s",strings[1]));
                                    map.put(strings[0],String.format(URL_MAT_2, URLEncoder.encode(strings[1])));
                                }else{
                                    playUrl.setReferer(RetrofitManager.REQUEST_URL);
                                    map.put(strings[0],String.format(URL_MAT_1,strings[1]));
                                }
                            }
                            playUrl.setUrls(map);
                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                            playUrl.setUrlType(IPlayUrls.URL_WEB);
                            playUrl.setSuccess(true);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return playUrl;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit,String baseUrl,String html) {
        return (IBangumiMoreRoot)home(retrofit,baseUrl,html);
    }

}
