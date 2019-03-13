package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.JrenService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * JrenImpl
 * Created by fanchen on 2017/9/24.
 */
public class JrenImpl implements IVideoParser {

    private XiaokanbaImpl xiaokanba = new XiaokanbaImpl(JrenService.class.getName());

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return xiaokanba.search(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return xiaokanba.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        VideoDetails details = (VideoDetails) xiaokanba.details(retrofit, baseUrl, html);
        List<? extends IVideoEpisode> episodes = details.getEpisodes();
        for (IVideoEpisode e : episodes) {
            VideoEpisode ve = (VideoEpisode) e;
            String id = ve.getId();
            String[] split = id.split("\\?");
            ve.setId(split[0]);
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        Node node = new Node(html);
        String src = baseUrl + node.attr("div.info.clearfix > script", "src");
        String s = StreamUtil.url2String(src);
        if (!TextUtils.isEmpty(s)) {
            String match = JavaScriptUtil.match("unescape\\([\"%\\w\\d\\W$]+\\);", s, 0);
            String[] splitUrl = RetrofitManager.REQUEST_URL.split("\\?")[1].split("-");
            String[] split = JavaScriptUtil.evalDecrypt(match).split("\\$\\$\\$");
            if (split.length > (Integer.valueOf(splitUrl[1]) - 1)) {
                String[] ids = split[Integer.valueOf(splitUrl[1]) - 1].split("#");
                for (int k = 0; k < ids.length; k++) {
                    if (k == (Integer.valueOf(splitUrl[2]) - 1)) {
                        String[] strings = ids[k].split("\\$");
                        Map<String, String> map = new HashMap<>();
                        if (strings[1].startsWith("ftp:") || strings[1].startsWith("xg:")) {
                            map.put(strings[0], strings[1]);
                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_XIGUA);
                            playUrl.setUrlType(IPlayUrls.URL_XIGUA);
                        } else if (strings[1].contains(".m3u8")) {
                            map.put(strings[0], strings[1]);
                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                            playUrl.setUrlType(IPlayUrls.URL_M3U8);
                        } else {
                            map.put(strings[0], strings[1]);
                            playUrl.setUrlType(IPlayUrls.URL_WEB);
                            playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                        }
                        playUrl.setUrls(map);
                        playUrl.setSuccess(true);
                    }
                }
            }
        }
        return playUrl;
    }

//    @Override
//    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html) {
//        VideoHome more = new VideoHome();
//        try {
//            List<Video> videos = new ArrayList<>();
//            JSONObject jsonObject = new JSONObject(html);
//            if (jsonObject.has("data")) {
//                JSONObject jsonData = jsonObject.getJSONObject("data");
//                if (jsonData.has("posts")) {
//                    JSONArray posts = jsonData.getJSONArray("posts");
//                    for (int i = 0; i < posts.length(); i++) {
//                        JSONObject object = posts.getJSONObject(i);
//                        String durl = object.has("url") ? object.getString("url") : "";
//                        String title = object.has("title") ? object.getString("title") : "";
//                        String cover = "";
//                        if(object.has("thumbnail")){
//                            JSONObject author = object.getJSONObject("thumbnail");
//                            if(author.has("url")){
//                                cover = author.getString("url");
//                            }
//                        }
//                        String info = "";
//                        if(object.has("date")){
//                            JSONObject author = object.getJSONObject("date");
//                            if(author.has("human")){
//                                info = author.getString("human");
//                            }
//                        }
//                        String up = "";
//                        if(object.has("author")){
//                            JSONObject author = object.getJSONObject("author");
//                            if(author.has("name")){
//                                up = author.getString("name");
//                            }
//                        }
//                        String aid = object.has("id") ? object.getString("id") : "";
//                        Video item = new Video();
//                        item.setHasDetails(true);
//                        item.setServiceClass(JrenService.class.getName());
//                        item.setTitle(title);
//                        item.setId(aid);
//                        item.setUrl(durl);
//                        item.setCover(cover);
//                        item.setExtras(info);
//                        item.setDanmaku(up);
//                        videos.add(item);
//                    }
//                }
//            }
//            more.setList(videos);
//            more.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//            more.setSuccess(false);
//        }
//        return more;
//    }
//
//    @Override
//    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
//        Node node = new Node(html);
//        VideoHome root = new VideoHome();
//        try {
//            List<Video> videos = new ArrayList<>();
//            for (Node n : node.list("article > div.inn-archive__item__container.inn-card_post-thumbnail__item__container")) {
//                String cover = n.attr("a > img", "data-src");
//                String durl = n.attr("a", "href");
//                String title = n.text("h3");
//                String info = n.text("div > div > time");
//                String up = n.text("div > div.inn-archive__item__meta.inn-card_post-thumbnail__item__meta");
//                String aid = n.attr("a", "href", "/", 4);
//                Video item = new Video();
//                item.setHasDetails(true);
//                item.setServiceClass(JrenService.class.getName());
//                item.setTitle(title);
//                item.setId(aid);
//                item.setUrl(durl);
//                item.setCover(cover);
//                item.setExtras(info);
//                item.setDanmaku(up);
//                videos.add(item);
//            }
//            root.setList(videos);
//            root.setSuccess(true);
//        } catch (Exception e) {
//            root.setSuccess(false);
//            e.printStackTrace();
//        }
//        return root;
//    }
//
//    @Override
//    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
//        String startUrl = "https://Video100.moe/?p=";
//        int start = html.indexOf(startUrl);
//        VideoDetails details = new VideoDetails();
//        details.setServiceClass(JrenService.class.getName());
//        try{
//            if(start != -1){
//                String url = html.substring(start,start + startUrl.length() + 5);
//                Map<String, String> header = new HashMap<>();
//                header.put("Cookie", "wordpress_test_cookie=WP+Cookie+check; Hm_lvt_5672553e8af14393d309ed5014151ae0=1505464427; Hm_lpvt_5672553e8af14393d309ed5014151ae0=1505464491; wordpress_logged_in_655c5bda33877be6d5d9650e5342f445=Sa79XtcUv80jGlA1%7C1506674155%7Cuj0MtWsNIycr31R1CDaTQtBH1KUMWXR0yBPllKAhGH2%7C6cecc172f5acff3986e583b4a90c98cc89d22de59f01233cecf83107b1cbb250");
//                byte[] bs = StreamUtil.url2byte(url, header);
//                if(bs != null && bs.length > 0){
//                    html = new String(bs);
//                }
//                details = parserRoot(details,html);
//            }else{
//                details = parserRoot(details,html);
//            }
//            details.setSuccess(true);
//        }catch (Exception e){
//            details.setSuccess(false);
//            e.printStackTrace();
//        }
//        return details;
//    }
//
//    @Override
//    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
//        return null;
//    }
//
//    private VideoDetails parserRoot(VideoDetails details,String html){
//        Node node = new Node(html);
//        List<VideoEpisode> chapters = new ArrayList<>();
//        List<Video> items = new ArrayList<>();
//        details.setEpisodes(chapters);
//        details.setRecomm(items);
//        details.setIntroduce(node.text("table > tbody"));
//        for (Node n : node.list("article > div.inn-related-posts__item__container.inn-card_variable-width__item__container")) {
//            String cover = n.attr("a > img", "data-src");
//            String durl = n.attr("a", "href");
//            String title = n.text("h3");
//            String info = n.text("div > div > time");
//            String up = n.text("div > div.inn-archive__item__meta.inn-card_post-thumbnail__item__meta");
//            String aid = n.attr("a", "href", "/", 4);
//            Video item = new Video();
//            item.setHasDetails(true);
//            item.setServiceClass(JrenService.class.getName());
//            item.setTitle(title);
//            item.setDanmaku(up);
//            item.setId(aid);
//            item.setExtras(info);
//            item.setUrl(durl);
//            item.setCover(cover);
//            items.add(item);
//        }
//        List<String> addVideo = addVideo(html);
//        if(addVideo != null){
//            for (int i = 0; i < addVideo.size(); i++) {
//                VideoEpisode chapter = new VideoEpisode();
//                chapter.setPlayType(VideoEpisode.PLAY_TYPE_VIDEO);
//                chapter.setServiceClass(JrenService.class.getName());
//                chapter.setTitle("第" + (i + 1) + "集");
//                chapter.setUrl(addVideo.get(i));
//                chapters.add(chapter);
//            }
//        }
//        return details;
//    }
//
//    public static  List<String> addVideo(String src){
//        Pattern pattern = Pattern.compile("addVideo\\([\\d\\w;,\"&=]+\\);");
//        Matcher matcher = pattern.matcher(src);
//        List<String> urls = new ArrayList<>();
//        while (matcher.find()) {
//            String group = matcher.group();
//            urls.add(String.format("http://api.moehuan.com/189/mobile.php?%s",group.substring(18, group.length() - 8)));
//        }
//        return urls;
//    }
}
