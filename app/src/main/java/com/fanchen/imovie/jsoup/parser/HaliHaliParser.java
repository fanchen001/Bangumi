package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.HaliHaliService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.StreamUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * HaliHaliParser
 * Created by fanchen on 2017/11/16.
 */
public class HaliHaliParser implements IVideoMoreParser {

    private CcyParser impl = new CcyParser(HaliHaliService.class.getName());

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return impl.search(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return impl.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        return impl.details(retrofit, baseUrl, html);
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls url = new VideoPlayUrls();
        try {
            String playdata = "";
            Map<String, String> map = new HashMap<>();
            for (Node n : new Node(html).list("script")) {
                String src = n.attr("src");
                if (src.contains("playdata")) {
                    playdata = StreamUtil.url2String(RetrofitManager.warpUrl(baseUrl, src));
                }
            }
            String match = JavaScriptUtil.match("unescape[\\u4e00-\\u9fa5\\(\\)\\{\\}\\[\\]\\\"\\w\\d`~！!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+;", html, 0, 9, 2);
            if (!TextUtils.isEmpty(playdata) && !TextUtils.isEmpty(match)) {
                String unescape = JavaScriptUtil.unescape(match);
                String[] split = unescape.split("\\$");
                if (split[1].contains(".m3u8")) {
                    map.put(split[0], split[1]);
                    url.setUrlType(IPlayUrls.URL_M3U8);
                    url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                } else {
                    map.put(split[0], split[1]);
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

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return impl.more(retrofit, baseUrl, html);
    }

//    @Override
//    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoHome home = new VideoHome();
//        try {
//            List<IVideo> videos = new ArrayList<>();
//            home.setList(videos);
//            LogUtil.e("HaliHaliParser","size ===> " + node.list("div.detail.detail-search > div.detail-wrap").size());
//            for (Node n : node.list("div.detail.detail-search > div.detail-wrap")) {
//                String title = n.attr("a", "title");
//                String cover = n.attr("div.detail-img._item-lazy._item-pic", "data-echo");
//                String clazz = n.textAt("ul.desc > li", 0);
//                String type = n.textAt("ul.desc > li", 1);
//                String author = n.textAt("ul.desc > li", 2);
//                String url = baseUrl + n.attr("a", "href");
//                String id = n.attr("a", "href", "/", 2);
//                Video video = new Video();
//                video.setHasDetails(true);
//                video.setServiceClass(HaliHaliService.class.getName());
//                video.setCover(cover);
//                video.setId(id);
//                video.setTitle(title);
//                video.setUrl(url);
//                video.setAuthor(author);
//                video.setClazz(clazz);
//                video.setType(type);
//                videos.add(video);
//                LogUtil.e("HaliHaliParser","videos ===>" + videos.size());
//            }
//            home.setSuccess(true);
//            LogUtil.e("HaliHaliParser","===>" + new Gson().toJson(home));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return home;
//    }
//
//    @Override
//    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoHome videoHome = new VideoHome();
//        try {
//            List<VideoTitle> titles = new ArrayList<>();
//            List<VideoBanner> banners = new ArrayList<>();
//            int count = 0;
//            for (Node n : node.list("div.swiper-wrapper > div.swiper-slide")) {
//                VideoBanner banner = new VideoBanner();
//                banner.setCover(n.attr("a", "data-background"));
//                banner.setId( n.attr("a", "href", "/", 2));
//                banner.setTitle(n.text("a > div > div.sub_title"));
//                banner.setUrl(baseUrl + n.attr("a", "href"));
//                banner.setServiceClass(HaliHaliService.class.getName());
//                banners.add(banner);
//            }
//            if (banners.size() > 0) {
//                videoHome.setHomeBanner(banners);
//            }
//            LogUtil.e("home","section -> " + node.list("section.mod.margin-t-15").size());
//            for (Node n : node.list("section.mod.margin-t-15")) {
//                String topTitle = n.text("span.mod-head-name");
//                if (TextUtils.isEmpty(topTitle) || "热点推送" .equals(topTitle)|| "小哈推荐" .equals(topTitle)
//                || "新番时间表".equals(topTitle)) continue;
//                String topUrl = baseUrl + n.attr("iv.mod-head.clearfix > a", "href");
//                String topId = n.attr("a.change", "href", "/", 1);
//                List<Video> videos = new ArrayList<>();
//                VideoTitle videoTitle = new VideoTitle();
//                videoTitle.setTitle(topTitle);
//                videoTitle.setDrawable(SEASON[count++ % SEASON.length]);
//                videoTitle.setId(topId);
//                if(!topTitle.contains("最新") && !topTitle.contains("筛选列表")){
//                    videoTitle.setMore(true);
//                }
//                videoTitle.setPageStart(2);
//                videoTitle.setUrl(topUrl);
//                videoTitle.setList(videos);
//                videoTitle.setServiceClass(HaliHaliService.class.getName());
//                for (Node sub : n.list("div > div > ul > li")) {
//                    Video video = new Video();
//                    video.setHasDetails(true);
//                    video.setServiceClass(HaliHaliService.class.getName());
//                    video.setCover(sub.attr("a > div._item-pic._item-lazy", "data-echo"));
//                    video.setTitle(sub.text("a > div.video-con"));
//                    video.setClazz(sub.text("a > div > div.video-duration"));
//                    video.setId(sub.attr("a", "href", "/", 2));
//                    video.setUrl(baseUrl + sub.attr("a", "href"));
//                    video.setAuthor(sub.text("a > div > div.video-duration"));
//                    videos.add(video);
//                }
//                if (videos.size() > 0) {
//                    titles.add(videoTitle);
//                }
//            }
//            LogUtil.e("home","titles -> " + new Gson().toJson(titles));
//            if (titles.size() == 0) {
//                List<IVideo> videos = new ArrayList<>();
//                for (Node sub : node.list("ul > li.video-item")) {
//                    Video video = new Video();
//                    video.setHasDetails(true);
//                    video.setServiceClass(HaliHaliService.class.getName());
//                    video.setCover(sub.attr("a > div._item-pic._item-lazy", "data-echo"));
//                    video.setTitle(sub.text("a > div.video-con"));
//                    video.setClazz(sub.text("a > div > div.video-duration"));
//                    video.setId(sub.attr("a", "href", "/", 2));
//                    video.setUrl(baseUrl + sub.attr("a", "href"));
//                    video.setAuthor(sub.text("a > div > div.video-duration"));
//                    videos.add(video);
//                }
//                videoHome.setSuccess(true);
//                videoHome.setList(videos);
//            } else {
//                videoHome.setSuccess(true);
//                videoHome.setHomeResult(titles);
//            }
//        } catch (Exception e) {
//            videoHome.setSuccess(false);
//            videoHome.setMessage(e.toString());
//            e.printStackTrace();
//        }
//        return videoHome;
//    }
//
//    @Override
//    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
//        Node node = new Node(html);
//        VideoDetails details = new VideoDetails();
//        try {
//            List<VideoEpisode> episodes = new ArrayList<>();
//            List<Video> videos = new ArrayList<>();
//            for (Node sub : node.list("ul > li.video-item")) {
//                Video video = new Video();
//                video.setHasDetails(true);
//                video.setServiceClass(HaliHaliService.class.getName());
//                video.setCover(sub.attr("a > div._item-pic._item-lazy", "data-echo"));
//                video.setTitle(sub.text("a > div.video-con"));
//                video.setClazz(sub.text("a > div > div.video-duration"));
//                video.setId(sub.attr("a", "href", "/", 2));
//                video.setUrl(baseUrl + sub.attr("a", "href"));
//                video.setAuthor(sub.text("a > div > div.video-duration"));
//                videos.add(video);
//            }
//            for (Node n : node.list("div.detail-video-select > ul > li")) {
//                for (Node sub : n.list("li")) {
//                    VideoEpisode episode = new VideoEpisode();
//                    episode.setServiceClass(HaliHaliService.class.getName());
//                    episode.setId(baseUrl + sub.attr("a", "href"));
//                    episode.setUrl(baseUrl + sub.attr("a", "href"));
//                    episode.setTitle(sub.text());
//                    episodes.add(episode);
//                }
//            }
//            details.setServiceClass(HaliHaliService.class.getName());
//            details.setCover(node.attr("div.detail-img > img", "src"));
//            details.setClazz(node.textAt("div.detail-media > ul.desc > li", 1));
//            details.setType(node.textAt("div.detail-media > ul.desc > li", 2));
//            details.setAuthor(node.textAt("div.detail-media > ul.desc > li", 0));
//            details.setTitle(node.text("div.detail-media > h1.media-title.ellipsis-2"));
//            details.setIntroduce(node.text("p.detail-intro-txt.ellipsis-2"));
//            details.setEpisodes(episodes);
//            details.setRecomm(videos);
//            details.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return details;
//    }
//
//    @Override
//    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
//        VideoPlayUrls playUrl = new VideoPlayUrls();
//        try {
//            Map<String, String> stringMap = new HashMap<>();
//            if (html.contains("var zanpiancms_player")) {
//                String match = JavaScriptUtil.match("zanpiancms_player = \\{[\\{\\}\\[\\]\\\"\\w\\d第集`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\\}", html, 0, 20, 0);
//                LogUtil.e("HaliHaliParser","match -> " + match);
//                if(JavaScriptUtil.isJson(match)){
//                    JSONObject object = new JSONObject(match);
//                    if( object.has("url")  &&  object.has("apiurl")){
//                        String url = object.getString("url");
//                        String apiurl = object.getString("apiurl");
//                        if(!StreamUtil.check(apiurl)){
//                            apiurl = "https://m.halihali.me/api/dplayer.php?v=";
//                        }
//                        playUrl.setSuccess(true);
//                        if (url.startsWith("http") && url.contains(".m3u")) {
//                            stringMap.put("标清", url);
//                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
//                            playUrl.setUrlType(IPlayUrls.URL_M3U8);
//                        } else if (url.startsWith("http") && (url.contains(".mp4") || url.contains(".avi") || url.contains(".rm") || url.contains("wmv"))) {
//                            stringMap.put("标清", url);
//                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//                            playUrl.setUrlType(IPlayUrls.URL_FILE);
//                        } else if (!TextUtils.isEmpty(apiurl) && apiurl.startsWith("http")) {
//                            stringMap.put("标清", apiurl + url);
//                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                            playUrl.setUrlType(IPlayUrls.URL_WEB);
//                        } else {
//                            playUrl.setSuccess(false);
//                        }
//                        playUrl.setUrls(stringMap);
//                        playUrl.setReferer(RetrofitManager.REQUEST_URL);
//                    }
//                }else{
//                    if (match.contains("http")) {
//                        match = match.substring(match.indexOf("http"));
//                        match = match.substring(0, match.indexOf("\""));
//                        if (!match.contains("halihali") && !(match.contains(".mp4") || match.contains(".m3u"))) {
//                            match = "https://halihali.duapp.com/mdparse/index.php?id=" + match;
//                        }
//                        if (match.startsWith("/")) {
//                            match = baseUrl + match;
//                        } else if (match.startsWith("//")) {
//                            match = "https" + match;
//                        }
//                        if(match.contains("=")){
//                            stringMap.put("标清", match.split("=")[1]);
//                        }else{
//                            stringMap.put("标清", match);
//                        }
//                        playUrl.setUrls(stringMap);
//                        playUrl.setReferer(RetrofitManager.REQUEST_URL);
//                        if(match.contains(".mp4") || match.contains(".m3u")){
//                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//                            playUrl.setUrlType(IPlayUrls.URL_M3U8);
//                        }else{
//                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                            playUrl.setUrlType(IPlayUrls.URL_WEB);
//                        }
//                        playUrl.setSuccess(true);
//                    }
//                }
//            }
//            if (stringMap.isEmpty()) {
//                stringMap.put("标清", RetrofitManager.REQUEST_URL);
//                playUrl.setUrls(stringMap);
//                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                playUrl.setUrlType(IPlayUrls.URL_WEB);
//                playUrl.setSuccess(true);
////            }
//            }
//            //var zanpiancms_player = {"url":"https:\/\/player.qinmoe.com\/play\/MzYwLzE1Mjg1NjEwMDgyNDAwMjM5"
//
////            String src = JavaScriptUtil.match("\"url\":\"[\\-@#=&:.\\\\/\\w\\d]+\",", html, 0, 7, 2);
////            String name = JavaScriptUtil.match("\"name\":\"[\\w\\d]+\",", html, 0, 8, 2);
////            if (TextUtils.isEmpty(src) || TextUtils.isEmpty(name)){
////                stringMap.put("标清", RetrofitManager.REQUEST_URL);
////                playUrl.setUrls(stringMap);
////                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB_V);
////                playUrl.setUrlType(IPlayUrls.URL_WEB);
////                playUrl.setSuccess(true);
////                return playUrl;
////            }
////            if ("xnflv".equals(name)) {
////                if (src.contains("&") && !src.startsWith("http")) {
////                    src = "http://xn.Video.tv/?type=" + src.replace("\\/", "/");
////                } else {
////                    src = "http://xn.Video.tv/?url=" + src.replace("\\/", "/");
////                }
////            } else {
////                if (!src.contains("&") && !src.startsWith("http")) {
////                    src = "http://m.Video.tv/weigao/api/opentv_mb.php?v=" + src + "&E-mail=88888";
////                } else if (src.contains("&") && !src.startsWith("http")) {
////                    src = "https://player.guolewan.com/mdparse/index.php?type=" + src.replace("\\/", "/");
////                } else {
////                    src = "https://player.guolewan.com/mdparse/index.php?id=" + src.replace("\\/", "/");
////                }
////            }
////            LogUtil.e("src == > ", src);
////            Map<String, String> map = new HashMap<>();
////            map.put("Referer", baseUrl);
////            map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
////            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
////            map.put("Cookie", "vParse_YKCna=U7a/EW4SsSsCAdzKmCvvEJEf");
////            String src2s = StreamUtil.url2String(src, map);
////            if (!TextUtils.isEmpty(src2s)) {
////                String playerUrl = "https://player.guolewan.com" + JavaScriptUtil.match("getScript\\('[\\\\\\-%:/.?&=\\w\\d]+'\\)", src2s, 0, 11, 2);
////                map.put("Referer", src);
////                String playerUrl2s = StreamUtil.url2String(playerUrl, map);
////                if (!TextUtils.isEmpty(playerUrl2s)) {
////                    String json = JavaScriptUtil.match("\\(\\{[-\" :%&,'/=.?\\w\\d\\[\\]\\(\\)\\{\\}]+\\}\\)", playerUrl2s, 0, 1, 1);
////                    String purl = JavaScriptUtil.match("var purl = '[+?=&:.\\\\/\\w\\d]+';", playerUrl2s, 0, 12, 2);
////                    if (!TextUtils.isEmpty(purl)) {
////                        stringMap.put("标清", purl);
////                        playUrl.setUrls(stringMap);
////                        playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
////                        playUrl.setUrlType(IPlayUrls.URL_M3U8);
////                        playUrl.setSuccess(true);
////                    } else if (!TextUtils.isEmpty(json)) {
////                        getJsonUrl(playUrl, json);
////                    }
////                }
////            }
////            if (!playUrl.isSuccess() && !TextUtils.isEmpty(src)) {
////                stringMap.put("标清", src);
////                playUrl.setUrls(stringMap);
////                playUrl.setUrlType(IPlayUrls.URL_WEB);
////                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
////                playUrl.setSuccess(true);
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return playUrl;
//    }
//
//    private void getJsonUrl(VideoPlayUrls playUrl, String json) {
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            if (jsonObject.has("success") && jsonObject.getBoolean("success") && jsonObject.has("urls")) {
//                JSONArray urls = jsonObject.getJSONArray("urls");
//                if (urls != null && urls.length() > 0) {
//                    JSONObject jsonObj = urls.getJSONObject(0);
//                    if (jsonObj.has("u") && jsonObject.has("type")) {
//                        String type = jsonObject.getString("type");
//                        String u = jsonObj.getString("u");
//                        Map<String, String> stringMap = new HashMap<>();
//                        stringMap.put(type, u);
//                        playUrl.setUrls(stringMap);
//                        playUrl.setPlayType("mp4".equals(type) ? IVideoEpisode.PLAY_TYPE_ZZPLAYER : IVideoEpisode.PLAY_TYPE_VIDEO);
//                        playUrl.setUrlType("mp4".equals(type) ? IPlayUrls.URL_FILE : IPlayUrls.URL_M3U8);
//                        playUrl.setSuccess(true);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
//        return (IBangumiMoreRoot) home(retrofit, baseUrl, html);
//    }

}
