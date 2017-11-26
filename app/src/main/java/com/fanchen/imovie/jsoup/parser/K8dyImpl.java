package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.k8dy.K8dyBanner;
import com.fanchen.imovie.entity.k8dy.K8dyDetails;
import com.fanchen.imovie.entity.k8dy.K8dyEpisode;
import com.fanchen.imovie.entity.k8dy.K8dyHome;
import com.fanchen.imovie.entity.k8dy.K8dyPlayUrl;
import com.fanchen.imovie.entity.k8dy.K8dyTitle;
import com.fanchen.imovie.entity.k8dy.K8dyVideo;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/10/28.
 */
public class K8dyImpl implements IVideoMoreParser {

    private static final String URL_MAT = "https://ckplayer.jjddyy.com/jx.php?id=%s";

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        K8dyHome home = new K8dyHome();
        try {
            List<K8dyVideo> videos = new ArrayList<>();
            home.setResult(videos);
            for (Node n : node.list("ul#resize_list > li")){
                String title = n.text("a > div > label.name");
                String cover = n.attr("a > div > img", "src");
                String clazz = n.textAt("div.list_info > p", 0);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a","href");
                String id = n.attr("a","href","/",2);
                K8dyVideo video = new K8dyVideo();
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        K8dyHome home = new K8dyHome();
        try {
            List<Node> list = node.list("div[class^=modo_title]");
            if(list != null && list.size() > 2){
                List<Node> ullist = node.list("ul.focusList > li.con");
                if(ullist != null && ullist.size() > 0){
                    List<K8dyBanner> banners = new ArrayList<>();
                    for (Node n : ullist){
                        K8dyBanner banner = new K8dyBanner();
                        banner.setCover(n.attr("a > img","src"));
                        banner.setId(n.attr("a","href","/", 2));
                        banner.setTitle(n.text("a > span"));
                        banner.setUrl(baseUrl + n.attr("a","href"));
                        banners.add(banner);
                    }
                    home.setBanners(banners);
                }
                int count = 0;
                List<K8dyTitle> titles = new ArrayList<>();
                home.setList(titles);
                for (Node n : list){
                    String topTitle = n.text("h2");
                    String topUrl = n.attr("i > a", "href");
                    String topId = n.attr("i > a", "href","/",2).replace(".html","");
                    List<K8dyVideo> videos = new ArrayList<>();
                    K8dyTitle K8dyTitle = new K8dyTitle();
                    K8dyTitle.setTitle(topTitle);
                    K8dyTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    K8dyTitle.setId(topId);
                    K8dyTitle.setUrl(topUrl);
                    K8dyTitle.setList(videos);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")){
                        String title = sub.text("a > div > label.name");
                        if(TextUtils.isEmpty(title))
                            title = sub.text("h2");
                        String cover = sub.attr("a > div > img", "data-original");
                        if(TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("a > div > label.title");
                        String score = n.text("a > div > label.score");
                        String url = baseUrl + sub.attr("a","href");
                        String id = sub.attr("a","href","/",2);
                        K8dyVideo video = new K8dyVideo();
                        video.setCover(cover);
                        video.setId(id);
                        video.setAuthor(score);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                    if(videos.size() > 0)
                        titles.add(K8dyTitle);
                    K8dyTitle.setMore(K8dyTitle.getList().size() == 6);
                }
            }else{
                List<K8dyVideo> videos = new ArrayList<>();
                LogUtil.e("size","==>" + node.list("ul > li").size());
                for (Node n : node.list("ul#resize_list > li")){
                    String title = n.text("h2");
                    String cover = n.attr("a > div > img", "src");
                    if(TextUtils.isEmpty(cover))
                        continue;
                    String hd = n.text("a > div > label.title");
                    String score = n.text("a > div > label.score");
                    String url = baseUrl + n.attr("a","href");
                    String id = n.attr("a","href","/",2);
                    K8dyVideo video = new K8dyVideo();
                    video.setCover(cover);
                    video.setId(id);
                    video.setAuthor(score);
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setType(hd);
                    videos.add(video);
                }
                home.setResult(videos);
            }
            home.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        K8dyDetails details = new K8dyDetails();
        try {
            List<K8dyEpisode> episodes = new ArrayList<>();
            List<K8dyVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul.list_tab_img > li")){
                String title = n.text("h2");
                String cover = n.attr("a > div > img", "src");
                if(TextUtils.isEmpty(cover))
                    continue;
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = baseUrl + n.attr("a","href");
                String id = n.attr("a","href","/",2);
                K8dyVideo video = new K8dyVideo();
                video.setCover(cover);
                video.setId(id);
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
                    K8dyEpisode episode = new K8dyEpisode();
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    if(list.size() > count){
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    }else{
                        episode.setTitle(sub.text());
                    }
                    episodes.add(episode);
                }
                count ++ ;
            }
            details.setCover(node.attr("div.vod-n-img > img.loading", "src"));
            details.setClazz(node.textAt("div.vod-n-l > p", 0));
            details.setType(node.textAt("div.vod-n-l > p", 1));
            details.setAuthor(node.textAt("div.vod-n-l > p",2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            details.setRecoms(videos);
            details.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
        K8dyPlayUrl playUrl = new K8dyPlayUrl();
        try{
            String match = JavaScriptUtil.match("VideoInfoList=\"[\\w\\d$#第集]+\"", html, 0, 15, 1);
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
                            map.put(strings[0],String.format(URL_MAT,strings[1]));
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
