package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoBanner;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.VideoTitle;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.BumimiService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.RegularUtil;
import com.fanchen.imovie.util.StreamUtil;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * BumimiImpl
 * Created by fanchen on 2017/9/24.
 */
public class BumimiImpl implements IVideoMoreParser {
    private static final String URL_MAT = "https://wydy8.1tbtech.com/parse/?id=%s";
    private static final String URL_IQIYI = "http://player.1tbtech.com/iqiyi.php?url=%s";
    private static final String URL_APOAPI = "https://wydy8.1tbtech.com/apoapi/?vid=%s";
    private static final String MIGU_MAT = "https://wydy8.1tbtech.com/bayun/playmigu.php?vid=%s";
    private static final String URL_ACFUN58 = "http://player.1tbtech.com/parse/acfun58.php?id=%s";
    IVideoMoreParser parser = new LL520Impl();

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<IVideo> videos = new ArrayList<>();
            home.setList(videos);
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.text("a > div > label.name");
                String cover = n.attr("a > div > img", "src");
                String clazz = n.textAt("div.list_info > p", 0);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(BumimiService.class.getName());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome home = new VideoHome();
        try {
            List<Node> list = node.list("div.modo_title.top");
            LogUtil.e("home", "div.modo_title.top = > " + (list != null ? list.size() : 0));
            if (list != null && list.size() > 0) {
                List<Node> ullist = node.list("ul.focusList > li.con");
                LogUtil.e("home", "ul.focusList > li.con = > " + (ullist != null ? ullist.size() : 0));
                if (ullist != null && ullist.size() > 0) {
                    List<VideoBanner> banners = new ArrayList<>();
                    for (Node n : ullist) {
                        VideoBanner banner = new VideoBanner();
                        banner.setServiceClass(BumimiService.class.getName());
                        banner.setCover(n.attr("a > img", "src"));
                        banner.setId(baseUrl + n.attr("a", "href"));
                        banner.setTitle(n.text("a > span"));
                        banner.setUrl(baseUrl + n.attr("a", "href"));
                        banners.add(banner);
                    }
                    home.setHomeBanner(banners);
                }
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                home.setHomeResult(titles);
                for (Node n : list) {
                    String topTitle = n.text("h2");
                    String topUrl = baseUrl + n.attr("i > a", "href");
                    String topId = n.attr("i > a", "href", "/", 1).replace(".html", "");
                    if (TextUtils.isEmpty(topId))
                        topId = n.attr("i > a", "href", "/", 2).replace(".html", "");
                    List<Video> videos = new ArrayList<>();
                    VideoTitle videoTitle = new VideoTitle();
                    videoTitle.setTitle(topTitle);
                    videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    videoTitle.setId(topId);
                    videoTitle.setPageStart(2);
                    videoTitle.setUrl(topUrl);
                    videoTitle.setList(videos);
                    videoTitle.setServiceClass(BumimiService.class.getName());
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")) {
                        String title = sub.text("a > div > label.name");
                        String cover = sub.attr("a > div > img", "src");
                        if (TextUtils.isEmpty(cover)) continue;
                        String score = n.text("a > div > label.score");
                        String hd = sub.text("a > div > label.title");
                        String url = baseUrl + sub.attr("a", "href");
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(BumimiService.class.getName());
                        video.setCover(cover);
                        video.setId(url);
                        video.setAuthor(score);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                    if (videos.size() > 0)
                        titles.add(videoTitle);
                    videoTitle.setMore(true);
                }
            } else {
                List<IVideo> videos = new ArrayList<>();
                for (Node n : node.list("div > div > ul > li")) {
                    String title = n.text("h2");
                    String cover = n.attr("a > div > img", "src");
                    if (TextUtils.isEmpty(cover))
                        continue;
                    String hd = n.text("a > div > label.title");
                    String score = n.text("a > div > label.score");
                    String url = baseUrl + n.attr("a", "href");
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(BumimiService.class.getName());
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
            for (Node n : node.list("ul.list_tab_img > li")) {
                String title = n.text("h2");
                String cover = n.attr("a > div > img", "src");
                if (TextUtils.isEmpty(cover))
                    continue;
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = baseUrl + n.attr("a", "href");
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(BumimiService.class.getName());
                video.setCover(cover);
                video.setId(url);
                video.setTitle(title);
                video.setUrl(url);
                video.setAuthor(score);
                video.setType(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div.play-title > span > a");
            for (Node n : node.list("div.play-box > ul.plau-ul-list")) {
                for (Node sub : n.list("li")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(BumimiService.class.getName());
                    episode.setId(baseUrl + sub.attr("a", "href"));
                    episode.setUrl(baseUrl + sub.attr("a", "href"));
                    if (list.size() > count) {
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    } else {
                        episode.setTitle(sub.text());
                    }
                    if(episode.getTitle().contains("迅雷")) {
                        episode.setPlayType(IVideoEpisode.PLAY_TYPE_XUNLEI);
                        String replace = episode.getUrl().replace(baseUrl, "");
                        episode.setUrl(replace);
                        episodes.add(episode);
                    }else if (!episode.getTitle().contains("网盘")) {
                        episodes.add(episode);
                    }
                }
                count++;
            }
            details.setServiceClass(BumimiService.class.getName());
            details.setCover(node.attr("div.vod-n-img > img.loading", "src"));
            details.setClazz(node.textAt("div.vod-n-l > p", 0));
            details.setType(node.textAt("div.vod-n-l > p", 1));
            details.setAuthor(node.textAt("div.vod-n-l > p", 2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            details.setRecomm(videos);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return (IBangumiMoreRoot) home(retrofit, baseUrl, html);
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoPlayUrls playUrl = new VideoPlayUrls();
        playUrl.setReferer(RetrofitManager.REQUEST_URL);
        Map<String, String> map = new HashMap<>();
        playUrl.setUrls(map);
        try {
            String src = baseUrl + node.attr("div > script", "src");
            String s = StreamUtil.url2String(src);
            LogUtil.e("playUrl","url2String => " + s);
            if (s.contains("unescape(")) {
                s = s.substring(s.indexOf("unescape("));
                if (s.contains("')")) {
                    s = s.substring(9, s.indexOf("')"));
                    String encode = JavaScriptUtil.unescape(s);
                    LogUtil.e("playUrl","unescape => " + encode);
                    String[] split = encode.split("\\$\\$\\$");
                    String[] splitUrl = RetrofitManager.REQUEST_URL.split("-");
                    if (split.length > Integer.valueOf(splitUrl[1]) - 1) {
                        String ssplitUrl = split[Integer.valueOf(splitUrl[1]) - 1];
                        if (ssplitUrl.contains("#")) {
                            String[] $s = ssplitUrl.split("#");
                            if ($s.length > Integer.valueOf(splitUrl[2].replace(".html","")) - 1) {
                                String sss = $s[Integer.valueOf(splitUrl[2].replace(".html","")) - 1];
                                String[] $sss = sss.split("\\$");
                                if ($sss[1].startsWith("ftp") || $sss[1].startsWith("xg")) {
                                    map.put($sss[0], $sss[1]);
                                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                                    playUrl.setUrlType(IPlayUrls.URL_XIGUA);
                                    playUrl.setSuccess(true);
                                }else if($sss[1].contains(".mp4") || $sss[1].contains(".3gp")|| $sss[1].contains(".flv")){
                                    map.put($sss[0], $sss[1]);
                                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                                    playUrl.setUrlType(IPlayUrls.URL_FILE);
                                    playUrl.setSuccess(true);
                                }if ($s[1].contains(".m3u")) {
                                    for (String ss : $s[1].split("&")){
                                        if(ss.contains(".m3u")){
                                            map.put($s[0], ss);
                                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                                            playUrl.setUrlType(IPlayUrls.URL_M3U8);
                                            playUrl.setSuccess(true);
                                        }
                                    }
                                } else {
                                    if($sss[1].startsWith("http")){
                                        if($sss[1].contains("qiyi")){
                                            map.put($sss[0], String.format(URL_IQIYI, $sss[1]));
                                        }else{
                                            map.put($sss[0], String.format(URL_MAT, $sss[1]));
                                        }
                                    }else{
                                        if(RegularUtil.isAllNumric($sss[1])){
                                            map.put($sss[0], String.format(MIGU_MAT, $sss[1]));
                                        }else if($sss[1].contains("=")){
                                            map.put($sss[0], String.format(URL_ACFUN58, $sss[1]));
                                        }else{
                                            map.put($sss[0], String.format(URL_APOAPI, $sss[1]));
                                        }
                                    }
                                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                                    playUrl.setUrlType(IPlayUrls.URL_WEB);
                                    playUrl.setSuccess(true);
                                }
                            }
                        } else {
                            String[] $s = ssplitUrl.split("\\$");
                            if ($s[1].startsWith("ftp") || $s[1].startsWith("xg")) {
                                map.put($s[0], $s[1]);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                                playUrl.setUrlType(IPlayUrls.URL_XIGUA);
                                playUrl.setSuccess(true);
                            } else if($s[1].contains(".mp4") || $s[1].contains(".3gp")|| $s[1].contains(".flv")){
                                map.put($s[0], $s[1]);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                                playUrl.setUrlType(IPlayUrls.URL_FILE);
                                playUrl.setSuccess(true);
                            } if ($s[1].contains(".m3u") ) {
                                for (String ss : $s[1].split("&")){
                                    if(ss.contains(".m3u")){
                                        map.put($s[0], ss);
                                        playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                                        playUrl.setUrlType(IPlayUrls.URL_M3U8);
                                        playUrl.setSuccess(true);
                                    }
                                }
                            } else {
                                if($s[1].startsWith("http")){
                                    if($s[1].contains("qiyi")){
                                        map.put($s[0], String.format(URL_IQIYI, $s[1]));
                                    }else{
                                        map.put($s[0], String.format(URL_MAT, $s[1]));
                                    }
                                }else{
                                    if(RegularUtil.isAllNumric($s[1])){
                                        map.put($s[0], String.format(MIGU_MAT, $s[1]));
                                    }else if($s[1].contains("=")){
                                        map.put($s[0], String.format(URL_ACFUN58, $s[1]));
                                    }else{
                                        map.put($s[0], String.format(URL_APOAPI, $s[1]));
                                    }
                                }
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                                playUrl.setUrlType(IPlayUrls.URL_WEB);
                                playUrl.setSuccess(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }
}
