package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.bumimi.BumimiDetails;
import com.fanchen.imovie.entity.bumimi.BumimiEpisode;
import com.fanchen.imovie.entity.bumimi.BumimiHome;
import com.fanchen.imovie.entity.bumimi.BumimiTitle;
import com.fanchen.imovie.entity.bumimi.BumimiPlayUrl;
import com.fanchen.imovie.entity.bumimi.BumimiVideo;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/9/24.
 */
public class BumimiImpl implements IVideoMoreParser {

    private static final String PLAYERURL = "https://www.ai577.com/playm3u8/index.php?type=&vid=%s";

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        BumimiHome root = new BumimiHome();
        try{
            List<BumimiVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul#resize_list > li")){
                String cover = n.attr("a > div > img", "data-original");
                String title = n.attr("a", "title");
                String url = baseUrl + n.attr("a", "href");
                String id = "";
                if (!TextUtils.isEmpty(url)) {
                    String[] split = url.split("/");
                    id = split[split.length - 1];
                }
                String score = n.text("div.list_info > p:eq(2)");
                String update = n.text("div.list_info > p:eq(5)");
                String p = n.text("p");
                BumimiVideo video = new BumimiVideo();
                video.setTitle(title);
                video.setCover(cover);
                video.setUrl(url);
                video.setId(id);
                video.setP(p);
                video.setExtras(update);
                video.setScore(score);
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

    @Override
    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        BumimiHome index = new BumimiHome();
        try {
            List<BumimiTitle> titles = new ArrayList<>();
            int count = 0;
            for (Node n : node.list("div.modo_title.top")){
                List<BumimiVideo> videos = new ArrayList<>();
                BumimiTitle homeTitle = new BumimiTitle();
                homeTitle.setTitle(n.text("h2"));
                String homeurl = n.attr("div.more > i > a", "href");
                homeTitle.setUrl(homeurl);
                homeTitle.setMore(homeurl.indexOf("/list/") == -1);
                if(homeTitle.isMore()){
                    String[] split = homeurl.split("/");
                    homeTitle.setId(split.length == 1 ?split [0] : split[1]);
                }
                homeTitle.setList(videos);
                homeTitle.setDrawable(SEASON[count++ % SEASON.length]);
                titles.add(homeTitle);
                for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")) {
                    String cover = sub.attr("a > div > img", "data-original");
                    if(TextUtils.isEmpty(cover))continue;;
                    String title = sub.text("a > div > label.name");
                    if(TextUtils.isEmpty(title))
                        title = sub.text("h2 > a");
                    String url = "" + sub.attr("a", "href");
                    String id = "";
                    if (!TextUtils.isEmpty(url)) {
                        String[] split = url.split("/");
                        id = split[split.length - 1];
                    }
                    String danmaku = sub.text("a > div > label.title");
                    BumimiVideo video = new BumimiVideo();
                    video.setCover(cover);
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setId(id);
                    video.setExtras(danmaku);
                    videos.add(video);
                }
            }
            index.setList(titles);
            index.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            index.setSuccess(false);
        }
        return index;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        BumimiDetails details = new BumimiDetails();
        try {
            List<BumimiEpisode> episodes = new ArrayList<>();
            List<BumimiVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul.list_tab_img > li")){
                String cover = n.attr("a > div > img", "data-original");
                if(TextUtils.isEmpty(cover))continue;;
                String title = n.text("a > div > label.name");
                if(TextUtils.isEmpty(title))
                    title = n.text("h2 > a");
                String url = "" + n.attr("a", "href");
                String id = "";
                if (!TextUtils.isEmpty(url)) {
                    String[] split = url.split("/");
                    id = split[split.length - 1];
                }
                String danmaku = n.text("a > div > label.title");
                BumimiVideo video = new BumimiVideo();
                video.setCover(cover);
                video.setTitle(title);
                video.setUrl(url);
                video.setId(id);
                video.setExtras(danmaku);
                videos.add(video);
            }
            for (Node n : node.list("ul.plau-ul-list > li")){
                String from = new Node(n.getElement().parent().parent()).attr("id");
                BumimiEpisode episode = new BumimiEpisode();
                episode.setTitle(from + ":" +n.text());
                episode.setUrl(baseUrl + n.attr("a", "href"));
                episode.setId(n.attr("a", "href", "/", 2));
                episodes.add(episode);
            }
            details.setEpisodes(episodes);
            details.setRecoms(videos);
            details.setExtras(node.text("div.vod-n-l > p:eq(2)"));
            details.setScore("0.0");
            details.setP(node.text("div.vod-n-l > p:eq(3)"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setSuccess(true);
        }catch (Exception e){
            details.setSuccess(false);
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        BumimiHome more = new BumimiHome();
        try {
            List<BumimiVideo> videos = new ArrayList<>();
            for (Node n : node.list("div.list_vod > ul > li")) {
                String cover = n.attr("a > div > img", "data-original");
                String title = n.attr("a > div > img", "alt");
                String url = baseUrl + n.attr("a", "href");
                String id = "";
                if (!TextUtils.isEmpty(url)) {
                    String[] split = url.split("/");
                    id = split[split.length - 1];
                }
                String score = n.text("a > div > label.score");
                String update = n.text("a > div > label.title");
                String p = n.text("p");
                BumimiVideo video = new BumimiVideo();
                video.setTitle(title);
                video.setCover(cover);
                video.setUrl(url);
                video.setId(id);
                video.setP(p);
                video.setExtras(update);
                video.setScore(score);
                videos.add(video);
            }
            more.setResult(videos);
            more.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            more.setSuccess(false);
        }
        return more;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        BumimiPlayUrl playUrl = new BumimiPlayUrl();
        try {
            String href = node.attr("div#zanpiancms_player > a", "href").substring(34);
            Map<String,String> url = new HashMap<>();
            playUrl.setUrls(url);
            url.put("标清", String.format(PLAYERURL, href));
            playUrl.setSuccess(true);
        }catch (Exception e){
            playUrl.setSuccess(false);
            e.printStackTrace();
        }
        return playUrl;
    }
}
