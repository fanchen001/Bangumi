package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.hali.HaliHaliDetails;
import com.fanchen.imovie.entity.hali.HaliHaliEpisode;
import com.fanchen.imovie.entity.hali.HaliHaliHome;
import com.fanchen.imovie.entity.hali.HaliHaliPlayUrl;
import com.fanchen.imovie.entity.hali.HaliHaliVideo;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/11/16.
 */
public class HaliHaliParser implements IVideoParser {

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        HaliHaliHome home = new HaliHaliHome();
        try {
            List<HaliHaliVideo> videos = new ArrayList<>();
            home.setResult(videos);
            for (Node n : node.list("ul#resize_list > li")) {
                String title = n.attr("a", "title");
                String cover = n.attr("a > div > img", "data-original");
                String clazz = n.textAt("div.list_info > p", 0);
                String type = n.textAt("div.list_info > p", 1);
                String author = n.textAt("div.list_info > p", 2);
                String url = baseUrl + n.attr("a", "href");
                String id = n.attr("a", "href", "/", 2);
                HaliHaliVideo video = new HaliHaliVideo();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        HaliHaliHome haliHaliHome = new HaliHaliHome();
        try {
            List<HaliHaliVideo> videos = new ArrayList<>();
            for (Node n : node.list("div > div > ul#vod_list > li")) {
                HaliHaliVideo video = new HaliHaliVideo();
                video.setCover(n.attr("a > div > img", "data-original"));
                video.setTitle(n.text("h2"));
                video.setClazz(n.text("p"));
                video.setId(n.attr("a", "href", "/", 2));
                video.setUrl(baseUrl + n.attr("a", "href"));
                video.setAuthor(n.text("a > div > label.score"));
                video.setType(n.text("a > div > label.title"));
                videos.add(video);
            }
            if (videos.size() > 0) {
                haliHaliHome.setSuccess(true);
                haliHaliHome.setResult(videos);
            }
        } catch (Exception e) {
            haliHaliHome.setSuccess(false);
            haliHaliHome.setMessage(e.toString());
            e.printStackTrace();
        }
        return haliHaliHome;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        Node node = new Node(html);
        HaliHaliDetails details = new HaliHaliDetails();
        try {
            List<HaliHaliEpisode> episodes = new ArrayList<>();
            List<HaliHaliVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul.list_tab_img > li")) {
                String title = n.text("h2");
                String cover = n.attr("a > div > img", "src");
                if (TextUtils.isEmpty(cover) || !cover.startsWith("http"))
                    continue;
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = baseUrl + n.attr("a", "href");
                String id = n.attr("a", "href", "/", 2);
                HaliHaliVideo video = new HaliHaliVideo();
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
            for (Node n : node.list("ul.plau-ul-list")) {
                for (Node sub : n.list("li")) {
                    HaliHaliEpisode episode = new HaliHaliEpisode();
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
            details.setCover(node.attr("div.vod-n-img > img.loading", "data-original"));
            details.setClazz(node.textAt("div.vod-n-l > p", 0));
            details.setType(node.textAt("div.vod-n-l > p", 1));
            details.setAuthor(node.textAt("div.vod-n-l > p", 2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            details.setRecoms(videos);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        HaliHaliPlayUrl playUrl = new HaliHaliPlayUrl();
        try {
            String src = JavaScriptUtil.match("\"url\":\"[=&:.\\\\/\\w\\d]+\",", html, 0, 7, 2);
            String name = JavaScriptUtil.match("\"name\":\"[\\w\\d]+\",", html, 0, 8, 2);
            if (TextUtils.isEmpty(src) || TextUtils.isEmpty(name)) return playUrl;
            if ("xnflv".equals(name)) {
                if (src.contains("&") && !src.startsWith("http")) {
                    src = "http://xn.halihali.tv/?type=" + src.replace("\\/", "/");
                } else {
                    src = "http://xn.halihali.tv/?url=" + src.replace("\\/", "/");
                }
            } else {
                if (!src.contains("&") && !src.startsWith("http")) {
                    src = "http://m.halihali.tv/weigao/api/opentv_mb.php?v=" + src + "&E-mail=88888";
                } else if (src.contains("&") && !src.startsWith("http")) {
                    src = "https://player.guolewan.com/api/play.php?type=" + src.replace("\\/", "/");
                } else {
                    src = "https://player.guolewan.com/api/play.php?vid=" + src.replace("\\/", "/");
                }
            }
            LogUtil.e("src == > ", src);
            Map<String, String> map = new HashMap<>();
            map.put("Referer", baseUrl);
            map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            map.put("Cookie", "vParse_YKCna=U7a/EW4SsSsCAdzKmCvvEJEf");
            String src2s = StreamUtil.url2String(src, map);
            if (!TextUtils.isEmpty(src2s)) {
                String playerUrl = "https://player.guolewan.com" + JavaScriptUtil.match("getScript\\('[\\\\\\-%:/.?&=\\w\\d]+'\\)", src2s, 0, 11, 2);
                map.put("Referer", src);
                String playerUrl2s = StreamUtil.url2String(playerUrl, map);
                if (!TextUtils.isEmpty(playerUrl2s)) {
                    String json = JavaScriptUtil.match("\\(\\{[-\" :%&,'/=.?\\w\\d\\[\\]\\(\\)\\{\\}]+\\}\\)", playerUrl2s, 0, 1, 1);
                    String purl = JavaScriptUtil.match("var purl = '[+?=&:.\\\\/\\w\\d]+';", playerUrl2s, 0, 12, 2);
                    if (!TextUtils.isEmpty(purl)) {
                        Map<String, String> stringMap = new HashMap<>();
                        stringMap.put("标清", purl);
                        playUrl.setUrls(stringMap);
                        playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                        playUrl.setUrlType(IPlayUrls.URL_M3U8);
                        playUrl.setSuccess(true);
                    } else if (!TextUtils.isEmpty(json)) {
                        getJsonUrl(playUrl, json);
                    }
                }
            }
            if (!playUrl.isSuccess() && !TextUtils.isEmpty(src)) {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("标清", src);
                playUrl.setUrls(stringMap);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

    private void getJsonUrl(HaliHaliPlayUrl playUrl, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("success") && jsonObject.getBoolean("success") && jsonObject.has("urls")) {
                JSONArray urls = jsonObject.getJSONArray("urls");
                if (urls != null && urls.length() > 0) {
                    JSONObject jsonObj = urls.getJSONObject(0);
                    if (jsonObj.has("u") && jsonObject.has("type")) {
                        String type = jsonObject.getString("type");
                        String u = jsonObj.getString("u");
                        Map<String, String> stringMap = new HashMap<>();
                        stringMap.put(type, u);
                        playUrl.setUrls(stringMap);
                        playUrl.setPlayType("mp4".equals(type) ? IVideoEpisode.PLAY_TYPE_ZZPLAYER : IVideoEpisode.PLAY_TYPE_VIDEO);
                        playUrl.setUrlType("mp4".equals(type) ? IPlayUrls.URL_FILE : IPlayUrls.URL_M3U8);
                        playUrl.setSuccess(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
