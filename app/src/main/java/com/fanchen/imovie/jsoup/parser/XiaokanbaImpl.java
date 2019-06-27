package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoBanner;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.VideoTitle;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.WeilaiService;
import com.fanchen.imovie.retrofit.service.XiaokanbaService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * XiaokanbaImpl
 * Created by fanchen on 2017/10/16.
 */
public class XiaokanbaImpl implements IVideoMoreParser {

    private String serviceName = XiaokanbaService.class.getName();

    public XiaokanbaImpl() {
    }

    public XiaokanbaImpl(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        VideoHome root = new VideoHome();
        try {
            List<IVideo> videos = new ArrayList<>();
            List<Node> list = node.list("div.hy-video-details.active.clearfix");
            if(list == null || list.isEmpty()) {
                for (Node n : node.list("div.item > ul.clearfix > li")) {
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(serviceName);
                    String original = n.attr("a", "data-original");
                    if(original.startsWith("http")){
                        video.setCover(original);
                    }else{
                        video.setCover(baseUrl + original);
                    }
                    video.setTitle(n.attr("a", "title"));
                    String text = n.text("span.score");
                    String text1 = n.text("div.subtitle.text-muted.text-muted.text-overflow.hidden-xs");
                    if(TextUtils.isEmpty(text))text = n.textAt("span.text-muted", 0);
                    if(TextUtils.isEmpty(text1))text1 = n.textAt("span.text-muted", 1);
                    video.setExtras("评分:" + (TextUtils.isEmpty(text) ? "0.0" : text));
                    video.setLast(text1);
                    String href = n.attr("a", "href");
                    String[] split = href.split("/");
                    if(WeilaiService.class.getName().equals(serviceName)){
                        video.setId(RetrofitManager.warpUrl(baseUrl,href));
                    }else if (split.length == 2) {
                        video.setId(split[1]);
                    } else if (split.length == 3) {
                        video.setId(split[2].replace("index", "").replace(".html", ""));
                    }
                    if (href.startsWith(".")) {
                        video.setUrl(baseUrl + href.replace(".", ""));
                    } else {
                        video.setUrl(baseUrl + href);
                    }
                    videos.add(video);
                }
            }else{
                for (Node n : list) {
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(serviceName);
                    String style = n.attr("a.videopic", "style").replace("background: url(", "").replace(")  no-repeat; background-position:50% 50%; background-size: cover;", "");
                    if(style.startsWith("http")){
                        video.setCover(style);
                    }else{
                        video.setCover(baseUrl + style);
                    }
                    video.setTitle(n.text("h3"));
                    video.setExtras(n.textAt("li.col-md-6.hidden-md.hidden-sm.hidden-xs.padding-0",0));
                    video.setLast(n.textAt("li.col-md-6.hidden-md.hidden-sm.hidden-xs.padding-0",1));
                    String href = n.attr("a.videopic","href");
                    String[] split = href.split("/");
                    if(WeilaiService.class.getName().equals(serviceName)){
                        video.setId(RetrofitManager.warpUrl(baseUrl,href));
                    }else if (split.length == 2) {
                        video.setId(split[1]);
                    } else if (split.length == 3) {
                        video.setId(split[2].replace("index", "").replace(".html", ""));
                    }
                    if (href.startsWith(".")) {
                        video.setUrl(baseUrl + href.replace(".", ""));
                    } else {
                        video.setUrl(baseUrl + href);
                    }
                    videos.add(video);
                }
            }
            root.setSuccess(true);
            root.setList(videos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        List<String> moreKeys = getMoreKeys();
        VideoHome root = new VideoHome();
        try {
            List<Node> slides = node.list("div.hy-video-slide");
            if (slides != null && !slides.isEmpty()) {
                List<VideoBanner> banners = new ArrayList<>();
                for (Node n : slides) {
                    VideoBanner banner = new VideoBanner();
                    banner.setTitle(n.attr("a", "title"));
                    banner.setUrl(baseUrl + n.attr("a", "href"));
                    banner.setServiceClass(serviceName);
                    String[] split1 = n.attr("a", "href").split("/");
                    if (split1.length == 2) {
                        banner.setId(split1[1]);
                    } else if (split1.length == 3) {
                        banner.setId(split1[2].replace("index", "").replace(".html", ""));
                    }
                    String replace = n.attr("a", "style").replace("padding-top: 60%; background: url(", "").replace(")  no-repeat; background-position:50% 50%; background-size: cover;", "");
                    if(replace.startsWith("http")){
                        banner.setCover(replace);
                    }else{
                        banner.setCover(baseUrl + replace);
                    }
                    banners.add(banner);
                }
                root.setHomeBanner(banners);
            }
            List<Node> list = node.list("div.hy-video-head");
            if (list != null && list.size() > 0) {
                int count = 0;
                List<VideoTitle> titles = new ArrayList<>();
                for (Node n : list) {
                    VideoTitle title = new VideoTitle();
                    title.setServiceClass(serviceName);
                    title.setUrl(baseUrl + n.attr("li > a", "href").replace(".", ""));
                    title.setTitle(n.text("h3"));
                    String href1 = n.attr("li > a", "href");
                    if (TextUtils.isEmpty(href1))
                        href1 = n.attr("a", "href");
                    String[] split1 = href1.split("/");
                    if(WeilaiService.class.getName().equals(serviceName) && split1.length == 3){
                        title.setMore(!moreKeys.contains(split1[1]));
                        title.setId(split1[1]);
                        title.setPageStart(2);
                    }else if (href1.startsWith(".") && split1.length == 2) {
                        title.setMore(!moreKeys.contains(split1[1]));
                        title.setId(split1[1]);
                    } else if (split1.length == 3) {
                        title.setMore(!moreKeys.contains(split1[2]));
                        title.setId(split1[2].replace("index", "").replace(".html", ""));
                    } else {
                        title.setMore(false);
                    }
                    title.setDrawable(SEASON[count++ % SEASON.length]);
                    List<Video> videos = new ArrayList<>();
                    title.setList(videos);
                    Node nextNode = new Node(n.getElement().nextElementSibling());
                    List<Node> nextList = nextNode.list("div.col-md-2.col-sm-3.col-xs-4");
                    if (nextList == null || nextList.isEmpty())
                        nextList = nextNode.list("div.col-md-3.col-sm-3.col-xs-4");
                    if (nextList == null || nextList.isEmpty())
                        nextList = nextNode.listTagClass("li", "col-md-2 col-sm-3 col-xs-4");
                    for (Node sub : nextList) {
                        Video video = new Video();
                        video.setHasDetails(true);
                        video.setServiceClass(serviceName);
                        String original = sub.attr("a", "data-original");
                        if(original.startsWith("http")){
                            video.setCover(original);
                        }else{
                            video.setCover(baseUrl + original);
                        }
                        String href = sub.attr("a", "href");
                        String[] split = href.split("/");
                        if(WeilaiService.class.getName().equals(serviceName)){
                            video.setId(RetrofitManager.warpUrl(baseUrl,href));
                        }else if (split.length == 2) {
                            video.setId(split[1]);
                        } else if (split.length == 3) {
                            video.setId(split[2].replace("index", "").replace(".html", ""));
                        }
                        video.setTitle(sub.attr("a", "title"));
                        if (href.startsWith(".")) {
                            video.setUrl(baseUrl + href.replace(".", ""));
                        } else {
                            video.setUrl(baseUrl + href);
                        }
                        String score = sub.text("a > span.score");
                        if (!TextUtils.isEmpty(score)) {
                            video.setDanmaku("评分:" + score);
                        } else {
                            video.setDanmaku(sub.text("span.note.textbg"));
                        }
                        videos.add(video);
                    }
                    if (!videos.isEmpty())
                        titles.add(title);
                }
                root.setSuccess(true);
                root.setHomeResult(titles);
            } else {
                List<IVideo> videos = new ArrayList<>();
                for (Node n : node.list("div.hy-video-list > div.item > ul.clearfix > li")) {
                    Video video = new Video();
                    video.setHasDetails(true);
                    video.setServiceClass(serviceName);
                    String original = n.attr("a", "data-original");
                    if(original.startsWith("http")){
                        video.setCover(original);
                    }else if(!TextUtils.isEmpty(original)){
                        video.setCover(baseUrl + original);
                    }
                    String title = n.attr("a", "title");
                    if(!TextUtils.isEmpty(title)){
                        video.setTitle(title);
                    }else{
                        video.setTitle(n.text("h5"));
                    }
                    String href = n.attr("a", "href");
                    video.setUrl(href);
                    String[] split = href.split("/");
                    if(WeilaiService.class.getName().equals(serviceName)){
                        video.setId(RetrofitManager.warpUrl(baseUrl,href));
                    }else if (split.length == 2) {
                        video.setId(split[1]);
                    } else if (split.length == 3) {
                        video.setId(split[2].replace("index", "").replace(".html", ""));
                    }
                    if (href.startsWith(".")) {
                        video.setUrl(baseUrl + href.replace(".", ""));
                    } else {
                        video.setUrl(baseUrl + href);
                    }
                    String score = n.text("a > span.score");
                    String text = n.text("span.note.textbg");
                    if (!TextUtils.isEmpty(score)) {
                        video.setDanmaku("评分:" + score);
                    } else if(!TextUtils.isEmpty(text)) {
                        video.setDanmaku(n.text("span.note.textbg"));
                    }else{
                        video.setDanmaku(n.text("div.subtitle.text-muted.text-overflow.hidden-xs"));
                    }
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
            List<Video> reco = new ArrayList<>();
            List<VideoEpisode> episodes = new ArrayList<>();
            details.setTitle(node.text("div.head > h3"));
            details.setIntroduce(node.text("div.plot"));
            String s1 = node.textAt("ul > li", 0);
            String s2 = node.textAt("ul > li", 1);
            String s3 = node.textAt("ul > li", 0);
            if (TextUtils.isEmpty(s1)) {
                details.setLast(node.textAt("span.text-muted", 3));
            } else {
                details.setLast(s1);
            }
            if (TextUtils.isEmpty(s2)) {
                details.setExtras(node.textAt("span.text-muted", 4));
            } else {
                details.setExtras(s2);
            }
            if (TextUtils.isEmpty(s3)) {
                details.setAuthor(node.textAt("span.text-muted", 1));
            } else {
                details.setAuthor(s3);
            }
            String style = node.attr("a.videopic", "style").replace("background: url(", "").replace(")  no-repeat; background-position:50% 50%; background-size: cover;", "");
            if(style.startsWith("http")){
                details.setCover(style);
            }else{
                details.setCover(baseUrl + style);
            }
            List<Node> list = node.list("div.item > div.col-md-2.col-sm-3.col-xs-4");
            if (list == null || list.isEmpty())
                list = node.list("div.swiper-wrapper > div > div.item");
            for (Node n : list) {
                Video video = new Video();
                video.setHasDetails(true);
                video.setServiceClass(serviceName);
                String original = n.attr("a", "data-original");
                if(original.startsWith("http")){
                    video.setCover(original);
                }else{
                    video.setCover(baseUrl + original);
                }
                video.setTitle(n.attr("a", "title"));
                String score = n.text("a > span.score");
                if (!TextUtils.isEmpty(score)) {
                    video.setDanmaku("评分:" + score);
                } else {
                    video.setDanmaku(n.text("span.note.textbg"));
                }
                String href = n.attr("a", "href");
                String[] split = href.split("/");
                if(WeilaiService.class.getName().equals(serviceName)){
                    video.setId(RetrofitManager.warpUrl(baseUrl,href));
                }else if (split.length == 2) {
                    video.setId(split[1]);
                } else if (split.length == 3) {
                    video.setId(split[2].replace("index", "").replace(".html", ""));
                }
                if (href.startsWith(".")) {
                    video.setUrl(baseUrl + href.replace(".", ""));
                } else {
                    video.setUrl(baseUrl + href);
                }
                reco.add(video);
            }
            int source = 1;
            for (Node n : node.list("div[id^=playlist]")) {
                if(n.getElement().childNodeSize() > 0){
                    Element child = n.getElement().child(0);
                    if("div".equals(child.tagName())){
                        continue;
                    }
                }
                for (Node sub : n.list("ul > li > a")) {
                    VideoEpisode episode = new VideoEpisode();
                    episode.setServiceClass(serviceName);
                    String title = n.text("a.option");
                    if(!TextUtils.isEmpty(title)){
                        episode.setTitle(title  + ":" + sub.text());
                    }else{
                        episode.setTitle("播放源" + source + ":" + sub.text());
                    }
                    String href = sub.attr("href");
                    if(href.startsWith(".")){
                        episode.setUrl(baseUrl + href.substring(1));
                        episode.setId(href.split("/")[1]);
                    }else if(href.startsWith("/play")){
                        episode.setUrl(baseUrl + href);
                        episode.setId(href.split("/")[2]);
                    }else if(href.contains("/play") && href.split("/").length == 4){
                        episode.setUrl(baseUrl + href);
                        episode.setId(baseUrl + href);
                    }else if(href.startsWith("/")){
                        episode.setUrl(baseUrl + href);
                        episode.setId(href.split("/")[1]);
                    }else{
                        episode.setUrl(href);
                        episode.setId(href);
                    }
                    episodes.add(episode);
                }
                source++;
            }
            details.setServiceClass(serviceName);
            details.setEpisodes(episodes);
            details.setRecomm(reco);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {
            String regex = "var now=\"[\\w\\d`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\";";
            String match = JavaScriptUtil.match(regex, html, 0,9,2);
            LogUtil.e("playUrl","match -> " + match);
            //var now="https://135zyv6.xw0371.com/share/kHHbXapSDLQVkYgA";
            Map<String, String> playMap = new HashMap<>();
            playUrl.setUrlType(VideoPlayUrls.URL_WEB);
            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
            playUrl.setReferer(RetrofitManager.REQUEST_URL);
            if(!TextUtils.isEmpty(match)){
                playMap.put("标清", match);
            }else{
                playMap.put("标清", RetrofitManager.REQUEST_URL);
            }
            playUrl.setUrls(playMap);
            playUrl.setSuccess(true);

//            Map<String, String> map = new HashMap<>();
//            map.put("Referer", RetrofitManager.REQUEST_URL);
//            map.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
//            String url = baseUrl + node.attr("iframe", "src").replace(".", "");
//            byte[] bytes = StreamUtil.url2byte(url, map);
//            String videoUrl = "";
//            if (bytes != null && bytes.length > 0) {
//                html = new String(bytes);
//                if (html.contains("ODflv")) {
//                    LogUtil.e("ODflv", "ODflv    解析");
//                    int startPosition = html.indexOf("$.post(\"api.php\", {");
//                    if (startPosition != -1) {
//                        html = html.substring(startPosition + 18);
//                        int endPosition = html.indexOf("\"},");
//                        if (endPosition != -1) {
//                            html = html.substring(0, endPosition + 2);
//                            JSONObject jsonObject = new JSONObject(html);
//                            String json = String.format("time=%s&key=%s&url=%s&type=%s", jsonObject.getString("time"), jsonObject.getString("key"), URLEncoder.encode(jsonObject.getString("url")), jsonObject.getString("type"));
//                            map.put("Referer", "https://p.Video.com/odflv/index.php?url=" + URLEncoder.encode(jsonObject.getString("url")));
//                            map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//                            byte[] bs = StreamUtil.doPoset("https://p.Video.com/odflv/api.php", json, map);
//                            if (bs != null && bs.length > 0) {
//                                videoUrl = new JSONObject(new String(bs)).getString("url");
//                            }
//                        }
//                    }
//                } else if (html.contains("DPlayer")) {
//                    LogUtil.e("DPlayer", "DPlayer    解析");
//                    int startPosition = html.indexOf("url: '");
//                    if (startPosition != -1) {
//                        html = html.substring(startPosition + 6);
//                        int endPosition = html.indexOf("',");
//                        if (endPosition != -1) {
//                            videoUrl = html.substring(0, endPosition);
//                        }
//                    }
//                } else if (html.contains("play_url")) {
//                    LogUtil.e("play_url", "play_url    解析");
//                    int startPosition = html.indexOf("play_url='");
//                    if (startPosition != -1) {
//                        html = html.substring(startPosition + 10);
//                        int endPosition = html.indexOf("',");
//                        if (endPosition != -1) {
//                            videoUrl = html.substring(0, endPosition);
//                        }
//                    }
//                } else if (html.contains("$.ajax")) {
//                    LogUtil.e("ajax", "ajax    解析");
//                    int startPosition = html.indexOf("url: '");
//                    if (startPosition != -1) {
//                        html = html.substring(startPosition + 6);
//                        int endPosition = html.indexOf("',");
//                        if (endPosition != -1) {
//                            String mUrl = "http:" + html.substring(0, endPosition);
//                            byte[] bytess = StreamUtil.url2byte(mUrl);
//                            if (bytess != null && bytess.length > 0) {
//                                String json = new String(bytess).replace("var tvInfoJs=", "");
//                                videoUrl = new JSONObject(json).getJSONObject("data").getJSONArray("vidl").getJSONObject(0).getString("m3u");
//                            }
//                        }
//                    }
//                } else if (html.contains("vParse_Play")) {
//                    LogUtil.e("vParse_Play", "vParse_Play    解析");
//                    int startPosition = html.indexOf("urls: [{\"u\":\"");
//                    if (startPosition != -1) {
//                        html = html.substring(startPosition + 13);
//                        int endPosition = html.indexOf("\",");
//                        if (endPosition != -1) {
//                            videoUrl = html.substring(0, endPosition);
//                        }
//                    }
//                }
//                if (!TextUtils.isEmpty(videoUrl)) {
//                    long millis = System.currentTimeMillis();
//                    if (videoUrl.contains("video.qq.com")) {
//                        videoUrl = "https:" + videoUrl + "&filename=video.mp4&callback=getvideo&_=" + millis;
//                        String guid = JavaScriptUtil.match("guid=[\\w\\d]+&", videoUrl, 0, 5, 1);
//                        String sdtfrom = JavaScriptUtil.match("sdtfrom=[\\w\\d]+&", videoUrl, 0, 8, 1);
//                        videoUrl = StreamUtil.url2String(videoUrl);
//                        if (!TextUtils.isEmpty(videoUrl)) {
//                            JSONObject jsonObject = new JSONObject(videoUrl.substring(9, videoUrl.length() - 1));
//                            String vkey = jsonObject.getString("key");
//                            String filename = jsonObject.getString("filename");
//                            videoUrl = String.format("http://36.250.4.15/vlive.qqvideo.tc.qq.com/AIenJ3VT8eg39eYtdbkbKkgK-16e2gf8Q5enMzE50BsY/%s?sdtfrom=%s&guid=%s&vkey=%s", filename, sdtfrom, guid, vkey);
//                        }
//                    }
//                    if (videoUrl.contains("http://cache.m.iqiyi.com/jp/tmts/")) {
//                        videoUrl = new JSONObject(StreamUtil.url2String(videoUrl).replace("var tvInfoJs=", "")).getJSONObject("data").getJSONArray("vidl").getJSONObject(0).getString("m3u");
//                    }
//                    if (videoUrl.contains("https://ups.youku.com") || videoUrl.contains("http://ups.youku.com")) {
//                        String vid = JavaScriptUtil.match("vid=[\\w\\d]+==&", videoUrl, 0, 4, 3);
//                        String ccode = JavaScriptUtil.match("ccode=[\\w\\d]+&", videoUrl, 0, 6, 1);
//                        videoUrl = "https://ups.youku.com/ups/get.json?callback=json" + millis + "&vid=" + vid + "&ccode=" + ccode + "&client_ip=" + SystemUtil.getHostIP() + "&utid=U7a%2FEW4SsSsCAdzKmCvvEJEf&client_ts=" + millis;
//                        videoUrl = StreamUtil.url2String(videoUrl);
//                        if (!TextUtils.isEmpty(videoUrl)) {
//                            videoUrl = new JSONObject(videoUrl.substring(18, videoUrl.length() - 1)).getJSONObject("data").getJSONArray("stream").getJSONObject(0).getString("m3u8_url");
//                        }
//                    }
//                    if (!TextUtils.isEmpty(videoUrl)) {
//                        Map<String, String> playMap = new HashMap<>();
//                        playMap.put("标清", videoUrl);
//                        if (videoUrl.contains("response-content-type=video/mp4") || videoUrl.contains(".mp4")) {
//                            playUrl.setUrlType(IPlayUrls.URL_FILE);
//                        }
//                        if (videoUrl.startsWith("http://cn-")) {
//                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_ZZPLAYER);
//                        }
//                        playUrl.setSuccess(true);
//                        playUrl.setUrls(playMap);
//                    }
//                }
//            }
//            if (!playUrl.isSuccess() || playUrl.getUrls() == null || playUrl.getUrls().isEmpty()) {
//                Map<String, String> playMap = new HashMap<>();
//                playMap.put("标清", url);
//                playUrl.setUrlType(IPlayUrls.URL_WEB);
//                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                playUrl.setSuccess(true);
//                playUrl.setUrls(playMap);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return search(retrofit, baseUrl, html);
    }


    private List<String> getMoreKeys() {
        List<String> list = new ArrayList<>();
        list.add("dianshiju");
        list.add("dongman");
        list.add("zongyi");
        list.add("dianying");
        return list;
    }

}
