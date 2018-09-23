package com.fanchen.imovie.jsoup.parser;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IBangumiTimeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.tucao.TucaoApiVideo;
import com.fanchen.imovie.entity.tucao.TucaoBanner;
import com.fanchen.imovie.entity.tucao.TucaoBaseVideo;
import com.fanchen.imovie.entity.tucao.TucaoDetails;
import com.fanchen.imovie.entity.tucao.TucaoEpisode;
import com.fanchen.imovie.entity.tucao.TucaoHome;
import com.fanchen.imovie.entity.tucao.TucaoTitle;
import com.fanchen.imovie.entity.tucao.TucaoPlayUrls;
import com.fanchen.imovie.entity.tucao.TucaoTimeLine;
import com.fanchen.imovie.entity.tucao.TucaoTimeLineTitle;
import com.fanchen.imovie.entity.tucao.TucaoVideo;
import com.fanchen.imovie.jsoup.IBangumiParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.view.pager.IBanner;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/9/18.
 */
public class TucaoImpl implements IBangumiParser {

    @Override
    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        TucaoHome index = new TucaoHome();
        try {
            List<Node> list = node.list("div.index_pos9 > ul > li > a");
            if(list == null || list.size() == 0){
                list = node.list("div.newcatfoucs > div.pic > a");
            }
            index.setHomeBanner(getTucaoBanners(list));
            index.setHomeResult(getTucaoHomeResults(node));
            index.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            index.setSuccess(false);
        }
        return index;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
        TucaoDetails details = new TucaoDetails();
        try{
            JSONObject jsonObject = new JSONObject(html);
            if(jsonObject.has("result")){
                jsonObject = jsonObject.getJSONObject("result");
                details.setTitle(jsonObject.has("title") ? jsonObject.getString("title") : "");
                details.setCover(jsonObject.has("thumb") ? jsonObject.getString("thumb") : "");
                details.setUp(jsonObject.has("user") ? jsonObject.getString("user") : "");
                details.setId(jsonObject.has("hid") ? jsonObject.getString("hid") : "");
                details.setIntroduce(jsonObject.has("description") ? jsonObject.getString("description") : "暂无介绍");
                details.setDanmaku(jsonObject.has("mukio") ? jsonObject.getString("mukio") : "");
                details.setUpdate(jsonObject.has("create") ? jsonObject.getString("create") : "");
                details.setPlay(jsonObject.has("play") ? jsonObject.getString("play") : "");
                List<TucaoEpisode> episodes = new ArrayList<>();
                details.setEpisodes(episodes);
                if(jsonObject.has("video")){
                    JSONArray videos = jsonObject.getJSONArray("video");
                    for (int i = 0 ; i < videos.length() ; i ++){
                        JSONObject object = videos.getJSONObject(i);
                        String file = object.has("file") ? object.getString("file") : "";
                        if(TextUtils.isEmpty(file))continue;
                        TucaoEpisode episode = new TucaoEpisode();
                        episode.setTitle(object.has("title") ? object.getString("title") : "");
                        episode.setId(object.has("vid") ? object.getString("vid") : "");
                        episode.setUrl(file);
                        episode.setPlayType(object.has("type") ? object.getString("type").contains("video") ? IVideoEpisode.PLAY_TYPE_VIDEO : IVideoEpisode.PLAY_TYPE_URL : IVideoEpisode.PLAY_TYPE_URL);
                        episode.setExtend(object.has("type") ? object.getString("type") : "");
                        episodes.add(episode);
                    }
                }
            }
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
        TucaoHome more = new TucaoHome();
        try {
            List<TucaoVideo> tucaoVideos = new ArrayList<>();
            List<Node> list = node.list("div.list > ul > li > div.box");
            if(list != null && list.size() > 0){
                for (Node n : list) {
                    String cover = n.attr("a.pic > img", "src");
                    String title = n.attr("a.pic > img", "alt");
                    String url = n.attr("a.pic", "href");
                    String id = "";
                    if(!TextUtils.isEmpty(url)){
                        String[] split = url.split("/");
                        id = split[split.length - 1];
                    }
                    String play = n.text("div.info > em.play > i");
                    String danmaku = n.text("div.info > em.tm > i");
                    String update = n.text("div.info > em.comment > i");
                    String up = n.text("b.user > a");
                    TucaoVideo video = new TucaoVideo();
                    video.setTitle(title);
                    video.setCover(cover);
                    video.setUrl(url);
                    video.setId(id);
                    video.setPlay(play);
                    video.setDanmaku(danmaku);
                    video.setUpdate(update);
                    video.setUp(up);
                    tucaoVideos.add(video);
                }
            }else{
                for (Node n : node.list("div.lists.tip > ul > li")){
                    TucaoVideo tucaoVideo = getTucaoVideo(n);
                    tucaoVideos.add(tucaoVideo);
                }
            }
            more.setList(tucaoVideos);
            more.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            more.setSuccess(false);
        }
        return more;
    }

    @Override
    public IBangumiMoreRoot ranking(Retrofit retrofit,String baseUrl,String json) {
        TucaoHome ranking = new TucaoHome();
        Gson gson = new Gson();
        try{
            List<TucaoApiVideo> tucaoVideos = new ArrayList<>();
            JSONObject object = new JSONObject(json);
            if(object.has("code") && "200".equals(object.getString("code"))){
                if(object.has("result")){
                    JSONObject result = object.getJSONObject("result");
                    Iterator<String> keys = result.keys();
                    int count = 0;
                    while (keys.hasNext()){
                        String s = result.get(keys.next()).toString();
                        TucaoApiVideo tucaoApiVideoItem = gson. fromJson(s, TucaoApiVideo.class);
                        tucaoApiVideoItem.setDrawable(RANK_SEASON[count ++ % RANK_SEASON.length]);
                        tucaoVideos.add(tucaoApiVideoItem);
                    }
                }
                ranking.setList(tucaoVideos);
                ranking.setSuccess(true);
            }else{
                ranking.setSuccess(false);
            }
        }catch (Exception e){
            ranking.setSuccess(false);
            e.printStackTrace();
        }
        return ranking;
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String json) {
        TucaoHome search = new TucaoHome();
        Gson gson = new Gson();
        try{
            List<TucaoApiVideo> tucaoVideos = new ArrayList<>();
            JSONObject object = new JSONObject(json);
            if(object.has("code") && "200".equals(object.getString("code"))){
                if(object.has("result")){
                    Object result = object.get("result");
                    if(result instanceof JSONArray){
                        JSONArray resultArray = (JSONArray) result;
                        for (int i = 0 ; i < resultArray.length() ; i ++){
                            String s = resultArray.get(i).toString();
                            TucaoApiVideo tucaoApiVideoItem = gson. fromJson(s, TucaoApiVideo.class);
                            tucaoVideos.add(tucaoApiVideoItem);
                        }
                    }else if(result instanceof JSONObject){
                        JSONObject resultObject = (JSONObject) result;
                        Iterator<String> keys = resultObject.keys();
                        int count = 0;
                        while (keys.hasNext()){
                            String s = resultObject.get(keys.next()).toString();
                            TucaoApiVideo tucaoApiVideoItem = gson. fromJson(s, TucaoApiVideo.class);
                            tucaoApiVideoItem.setDrawable(RANK_SEASON[count ++ % RANK_SEASON.length]);
                            tucaoVideos.add(tucaoApiVideoItem);
                        }
                    }
                }
                search.setList(tucaoVideos);
                search.setSuccess(true);
            }else{
                search.setSuccess(false);
            }
        }catch (Exception e){
            search.setSuccess(false);
            e.printStackTrace();
        }
        return search;
    }

    @Override
    public IBangumiTimeRoot timeLine(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        TucaoTimeLine lineRoot = new TucaoTimeLine();
        try{
            List<TucaoTimeLineTitle> lineTitles = new ArrayList<>();
            List<Node> list = node.list("div.gray_show.xf_list > table > tbody > tr > td > ul");
            List<Node> titleList = node.list("div.xf_tabs > ul > li");
            if(list.size() == titleList.size()){
                for (int i = 0 ; i < list.size() ; i ++){
                    Node titleN = titleList.get(i);
                    Node n = list.get(i);
                    List<TucaoBaseVideo> baseVideos = new ArrayList<>();
                    TucaoTimeLineTitle lineTitle = new TucaoTimeLineTitle();
                    lineTitle.setTitle(titleN.text());
                    lineTitle.setIsNow("now".equals(titleN.getElement().attr("class")));
                    lineTitle.setDrawable(TIME_SEASON[i % TIME_SEASON.length]);
                    lineTitle.setList(baseVideos);
                    for (Node sub : n.list("li")){
                        String cover = sub.attr("a > img", "src");
                        String title = sub.text("a > em");
                        String url = "http://www.tucao.tv/" + sub.attr("a", "href");
                        TucaoBaseVideo baseVideo = new TucaoBaseVideo();
                        baseVideo.setTitle(title);
                        baseVideo.setCover(cover);
                        baseVideo.setUrl(url);
                        baseVideos.add(baseVideo);
                    }
                    lineTitles.add(lineTitle);
                }
                lineRoot.setList(lineTitles);
                lineRoot.setSuccess(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            lineRoot.setSuccess(false);
            lineRoot.setMessage(e.toString());
        }
        return lineRoot;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
        TucaoPlayUrls urls = new TucaoPlayUrls();
        try{
            String match = JavaScriptUtil.match("http[,&%=?.\\w\\d:/-]+", html, 0);
            if(!TextUtils.isEmpty(match)){
                Map<String,String> url = new HashMap<>();
                urls.setUrls(url);
                url.put("标清",match);
                urls.setSuccess(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return urls;
    }

    @NonNull
    private List<TucaoBanner> getTucaoBanners(List<Node> nodes) {
        List<TucaoBanner> banners = new ArrayList<>();
        for (Node n : nodes) {
            //横幅banner
            String cover = n.attr("img", "src");
            String title = n.attr("img", "alt");
            String url = n.attr("href");
            String id = "";
            if(!TextUtils.isEmpty(url)){
                String[] split = url.split("/");
                id = split[split.length - 1];
            }
            String info = n.text("p");
            TucaoBanner banner = new TucaoBanner();
            banner.setTitle(title);
            banner.setCover(cover);
            banner.setUrl(url);
            banner.setId(id);
            banner.setExtInfo(info);
            banner.setBannerType(id.contains("h") ? IBanner.TYPE_NATIVE : IBanner.TYPE_WEB);
            banners.add(banner);
        }
        return banners;
    }

    @NonNull
    private List<TucaoTitle> getTucaoHomeResults(Node node) {
        List<TucaoTitle> homeResults = new ArrayList<>();
        int count = 0;
        for (Node n : node.list("h2.title_red")) {
            //下方数据实体
            if (new Node(n.getElement().nextElementSibling()).list("ul > li").size() == 0) {
                continue;
            }
            TucaoTitle result = new TucaoTitle();
            result.setUrl(n.attr("a", "href") + "index_%d.html");
            String[] split = result.getUrl().split("/");
            result.setId(split.length >= 5 ? split[4] : "");
            result.setTitle(n.text("a:eq(1)"));
            result.setMore(true);
            result.setDrawable(SEASON[count++ % SEASON.length]);
            Element element = n.getElement().nextElementSibling();
            List<TucaoVideo> videoResults = new ArrayList<>();
            result.setList(videoResults);
            homeResults.add(result);
            for (Node sub : new Node(element).list("ul > li")) {
                TucaoVideo video = getTucaoVideo(sub);
                videoResults.add(video);
            }
        }
        return homeResults;
    }

    @NonNull
    private TucaoVideo getTucaoVideo(Node sub) {
        String cover = sub.attr("a > img", "src");
        String title = sub.attr("a > img", "alt");
        String url = sub.attr("a", "href");
        String id = "";
        if(!TextUtils.isEmpty(url)){
            String[] split = url.split("/");
            id = split[split.length - 1];
        }
        String play = sub.text("a > em.play");
        String danmaku = sub.text("a > em.tm");
        String update = sub.attr("a > img", "update");
        String up = sub.attr("a > img", "user");
        TucaoVideo video = new TucaoVideo();
        video.setTitle(title);
        video.setCover(cover);
        video.setUrl(url);
        video.setId(id);
        video.setPlay(play);
        video.setDanmaku(danmaku);
        video.setUpdate(update);
        video.setUp(up);
        return video;
    }

}
