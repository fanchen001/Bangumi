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
import com.fanchen.imovie.retrofit.service.CcyService;
import com.fanchen.imovie.retrofit.service.K8dyService;
import com.fanchen.imovie.retrofit.service.ZhandiService;
import com.fanchen.imovie.util.JavaScriptUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

//https://m.7ccy.com/
public class CcyImpl implements IVideoMoreParser {

    private String clazz = CcyService.class.getName();
    private boolean defM3u8 = true;
    private boolean isAgent = true;

    public CcyImpl() {
    }

    public CcyImpl(String clazz) {
        this(clazz,false);
    }

    public CcyImpl(String clazz, boolean defM3u8) {
        this(clazz,defM3u8,false);
    }

    public CcyImpl(String clazz, boolean defM3u8, boolean isAgent) {
        this.clazz = clazz;
        this.defM3u8 = defM3u8;
        this.isAgent = isAgent;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return search(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot) home(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Node> list = node.list("div.stui-pannel_hd");
            if (list != null && list.size() > 2) {
                List<Node> flickity = node.list("div.carousel.carousel_default.flickity-page > div > a");
                if(flickity == null || flickity.isEmpty()) flickity = node.list("div.carousel.carousel_wide.col-pd > div.wide > a");
                if(flickity != null && !flickity.isEmpty()){
                    List<VideoBanner> banners = new ArrayList<>();
                    for (Node n : flickity) {
                        VideoBanner banner = new VideoBanner();
                        String style = n.attr("style").replace("background: url(", "");
                        String replace = style.replace(") no-repeat; background-position:50% 50%; background-size: cover; padding-top: ","");
                        banner.setCover(replace.replace("45%;", "").replace("40%;", ""));
                        String href = n.attr("href", "/", 2);
                        if(href.contains(".html")){
                            if(clazz.equals(K8dyService.class.getName())){
                                banner.setId(href.replace(".html",""));
                            }else{
                                banner.setId(href);
                            }
                        }else{
                            banner.setId(n.attr("href"));
                        }
                        if(clazz.equals(ZhandiService.class.getName())){
                            banner.setId(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                        }
                        banner.setUrl(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                        banner.setTitle(n.attr("title"));
                        banner.setServiceClass(clazz);
                        banner.setBannerType(VideoBanner.TYPE_NATIVE);
                        banners.add(banner);
                    }
                    home.setHomeBanner(banners);
                }
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                home.setHomeResult(titles);
                for (Node n : node.list("div.stui-pannel_hd")) {
                    List<Video> videos = new ArrayList<>();
                    VideoTitle videoTitle = new VideoTitle();
                    videoTitle.setTitle(n.text("h3.title"));
                    if(TextUtils.isEmpty(n.text("h3.title").trim())){
                        videoTitle.setTitle(n.text("最新更新"));
                    }
                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    String[] hrefs = n.attr("h3 > a", "href").split("/");
                    if(hrefs.length == 3 && hrefs[2].contains(".html")){
                        videoTitle.setId(hrefs[2].replace(".html",""));
                    }else  if(hrefs.length == 2){
                        videoTitle.setId(hrefs[1]);
                    }else  if(hrefs.length == 3){
                        videoTitle.setId(hrefs[1]);
                    }else if(hrefs.length == 4){
                        videoTitle.setId(hrefs[2]);
                    }
                    videoTitle.setPageStart(2);
                    videoTitle.setUrl(RetrofitManager.warpUrl(baseUrl, n.attr("h3 > a", "href")));
                    videoTitle.setMore(false);
                    videoTitle.setList(videos);
                    videoTitle.setServiceClass(clazz);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("ul > li > div > a")) {
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(clazz);
                        video.setCover(sub.attr("data-original"));
                        String href = sub.attr("href", "/", 2);
                        if(href.contains(".html")){
                            video.setId(href.replace(".html", ""));
                        }else{
                            video.setId(sub.attr("href"));
                        }
                        if(clazz.equals(ZhandiService.class.getName())){
                            video.setId(RetrofitManager.warpUrl(baseUrl, sub.attr("href")));
                        }
                        video.setUrlReferer(baseUrl);
                        video.setDanmaku(sub.text("span.pic-text.text-right"));
                        video.setTitle(sub.attr("title"));
                        video.setUrl(RetrofitManager.warpUrl(baseUrl, sub.attr("href")));
                        video.setType(sub.attr("p.text.text-overflow.text-muted.hidden-xs"));
                        video.setAgent(isAgent);
                        videos.add(video);
                    }
                    if (!videos.isEmpty()) {
                        titles.add(videoTitle);
                    }
                }
            } else {
                List<IVideo> videos = new ArrayList<>();
                List<Node> list1 = node.list("div.stui-pannel_bd > ul > li > div > a");
                if(list1 == null || list1.isEmpty()) {
                    for (Node n : node.list("div > ul.clearfix > li")) {
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(clazz);
                        video.setCover(n.attr("a > img","src"));
                        String href = n.attr("a","href", "/", 3);
                        if(href.contains(".html")){
                            video.setId(href.replace(".html", ""));
                        }else{
                            video.setId(n.attr("href"));
                        }
                        if(clazz.equals(ZhandiService.class.getName())){
                            video.setId(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                        }
                        video.setUrlReferer(baseUrl);
                        video.setDanmaku(n.text("span.f1"));
                        video.setTitle(n.text("span.biaoti"));
                        video.setUrl(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                        video.setType(n.text("span.fr"));
                        video.setAgent(isAgent);
                        videos.add(video);
                    }
                }else{
                    for (Node n : list1) {
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(clazz);
                        video.setCover(n.attr("data-original"));
                        String href = n.attr("href", "/", 2);
                        if(href.contains(".html")){
                            video.setId(href.replace(".html", ""));
                        }else{
                            video.setId(n.attr("href"));
                        }
                        if(clazz.equals(ZhandiService.class.getName())){
                            video.setId(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                        }
                        video.setUrlReferer(baseUrl);
                        video.setDanmaku(n.text("span.pic-text.text-right"));
                        video.setTitle(n.attr("title"));
                        video.setUrl(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                        video.setType(n.attr("p.text.text-overflow.text-muted.hidden-xs"));
                        video.setAgent(isAgent);
                        videos.add(video);
                    }
                }
                home.setList(videos);
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
            details.setHost(baseUrl);
            details.setCanDownload(true);
            details.setServiceClass(this.clazz);
            details.setCover(node.attr("div.stui-content__thumb > a", "data-original"));
            details.setTitle(node.attr("div.stui-content__thumb > a","title"));
            details.setClazz(node.textAt("div.stui-content__detail > p.data", 0));
            details.setType(node.textAt("div.stui-content__detail > p.data", 1));
            details.setAuthor(node.textAt("div.stui-content__detail > p.data", 2));
            String text = node.text("span.detail-sketch");
            if(TextUtils.isEmpty(text))text = node.text("p.jianjie_y9_p.part");
            if(TextUtils.isEmpty(text))text = node.text("div.stui-pannel_bd > p.col-pd");
            //
            details.setIntroduce(text);
            List<Video> videos = new ArrayList<>();

            List<Node> list2 = node.list("ul.stui-vodlist__bd.clearfix > li > div > a");
            if(list2 == null || list2.isEmpty()) list2 = node.list("div.stui-pannel_bd > ul > li > div > a");
                for (Node n : list2){
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(clazz);
                    video.setCover(n.attr("data-original"));
                    video.setId(n.attr("a", "href", "/", 2).replace(".html", ""));
                    if(clazz.equals(ZhandiService.class.getName())){
                        video.setId(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                    }
                    video.setUrlReferer(baseUrl);
                    video.setDanmaku(n.text("span.pic-text.text-right"));
                    video.setTitle(n.attr("title"));
                    video.setUrl(RetrofitManager.warpUrl(baseUrl, n.attr("href")));
                    video.setType(n.attr("p.text.text-overflow.text-muted.hidden-xs"));
                    video.setAgent(isAgent);
                    videos.add(video);
                }
            List<VideoEpisode> episodes = new ArrayList<>();
            int count = 0;
            List<Node> list1 = node.list("ul.stui-content__playlist.cli.clearfix.column8 > li > a");
            if(list1 != null && !list1.isEmpty()){
                for (Node n : node.list("div.tab-content.stui-pannel_bd.col-pd.downlist > div > ul.stui-content__playlist.clearfix.column8")){
                    for (Node sub : n.list("li")) {
                        VideoEpisode episode = new VideoEpisode();
                        episode.setServiceClass(this.clazz);
                        episode.setId(baseUrl + sub.attr("a", "href"));
                        episode.setUrl(baseUrl + sub.attr("a", "href"));
                        if (list1.size() > count) {
                            episode.setTitle(list1.get(count).text() + "_" + sub.text());
                        } else {
                            episode.setTitle(sub.text());
                        }
                        episodes.add(episode);
                    }
                    count++;
                }
            }else{
                List<Node> list3 = node.list("ul.nav.nav-tabs.pull-right > li");
                if(list3 != null && !list3.isEmpty()){
                    for (Node n : node.list("div.tab-content.stui-pannel_bd.col-pd.stui-player__side > div > ul")){
                        for (Node sub : n.list("li")) {
                            VideoEpisode episode = new VideoEpisode();
                            episode.setServiceClass(this.clazz);
                            episode.setId(baseUrl + sub.attr("a", "href"));
                            episode.setUrl(baseUrl + sub.attr("a", "href"));
                            if (list3.size() > count) {
                                episode.setTitle(list3.get(count).text() + "_" + sub.text());
                            } else {
                                episode.setTitle(sub.text());
                            }
                            episodes.add(episode);
                        }
                        count++;
                    }
                    //
                }else{
                    List<Node> list = node.list("div.stui-pannel_hd > div.stui-pannel__head.bottom-line.active.clearfix > h3.title");
                    for (Node n : node.list("div.stui-pannel_bd.col-pd.clearfix > ul")){
                        for (Node sub : n.list("li")) {
                            VideoEpisode episode = new VideoEpisode();
                            episode.setServiceClass(this.clazz);
                            episode.setId(baseUrl + sub.attr("a", "href"));
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
                }
            }
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
        VideoPlayUrls url = new VideoPlayUrls();
        try {
            Map<String, String> map = new HashMap<>();
            String match = JavaScriptUtil.match("var cms_player =[\\u4e00-\\u9fa5\\(\\)\\{\\}\\[\\]\\\"\\w\\d`~！!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+;", html, 0, 16, 1);
            if (!TextUtils.isEmpty(match)) {
                String splitUrl = new JSONObject(match).getString("url");
                if(splitUrl.contains(".m3u8")){
                    map.put("m3u8", splitUrl);
                    url.setUrlType(IPlayUrls.URL_M3U8);
                    url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                }else{
                    map.put("标清", splitUrl);
                    url.setUrlType(IPlayUrls.URL_FILE);
                    url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                }
            } else {
                map.put("标清", RetrofitManager.REQUEST_URL);
                url.setUrlType(IPlayUrls.URL_WEB);
                url.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
            }
            url.setSuccess(true);
            url.setUrls(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
