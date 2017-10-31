package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.xiaokanba.XiaokanbaDetails;
import com.fanchen.imovie.entity.xiaokanba.XiaokanbaEpisode;
import com.fanchen.imovie.entity.xiaokanba.XiaokanbaPlayUrl;
import com.fanchen.imovie.entity.xiaokanba.XiaokanbaHome;
import com.fanchen.imovie.entity.xiaokanba.XiaokanbaTitle;
import com.fanchen.imovie.entity.xiaokanba.XiaokanbaVideo;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;
import com.fanchen.imovie.util.SystemUtil;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanchen on 2017/10/16.
 */
public class XiaokanbaImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot search(String html) {
        Node node = new Node(html);
        XiaokanbaHome root = new XiaokanbaHome();
        try {
            List<XiaokanbaVideo> videos = new ArrayList<>();
            for (Node n : node.list("div.item > ul.clearfix > li")) {
                XiaokanbaVideo video = new XiaokanbaVideo();
                video.setCover(n.attr("a", "data-original"));
                video.setId(n.attr("a", "href", "/", 1));
                video.setTitle(n.attr("a", "title"));
                video.setUrl("http://xiaokanba.com" + n.attr("a", "href").replace(".", ""));
                video.setScore(n.text("a > span.score"));
                videos.add(video);
            }
            root.setSuccess(true);
            root.setResult(videos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IHomeRoot home(String html) {
        Node node = new Node(html);
        XiaokanbaHome root = new XiaokanbaHome();
        try {
            List<Node> list = node.list("div.hy-video-head");
            if (list != null && list.size() > 0) {
                int count = 0;
                List<XiaokanbaTitle> titles = new ArrayList<>();
                for (Node n : list) {
                    XiaokanbaTitle title = new XiaokanbaTitle();
                    titles.add(title);
                    title.setUrl("http://xiaokanba.com" + n.attr("li > a", "href").replace(".", ""));
                    title.setTitle(n.text("h3"));
                    title.setId(n.attr("li > a", "href", "/", 1));
                    title.setDrawable(SEASON[count++ % SEASON.length]);
                    List<XiaokanbaVideo> videos = new ArrayList<>();
                    title.setResult(videos);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div.col-md-2.col-sm-3.col-xs-4")) {
                        XiaokanbaVideo video = new XiaokanbaVideo();
                        video.setCover(sub.attr("a", "data-original"));
                        video.setId(sub.attr("a", "href", "/", 1));
                        video.setTitle(sub.attr("a", "title"));
                        video.setUrl("http://xiaokanba.com" + sub.attr("a", "href").replace(".", ""));
                        video.setScore(sub.text("a > span.score"));
                        videos.add(video);
                    }
                }
                root.setSuccess(true);
                root.setList(titles);
            } else {
                List<XiaokanbaVideo> videos = new ArrayList<>();
                for (Node n : node.list("div.item > ul.clearfix > li")) {
                    XiaokanbaVideo video = new XiaokanbaVideo();
                    video.setCover(n.attr("a", "data-original"));
                    video.setId(n.attr("a", "href", "/", 1));
                    video.setTitle(n.attr("a", "title"));
                    video.setUrl("http://xiaokanba.com" + n.attr("a", "href").replace(".", ""));
                    video.setScore(n.text("a > span.score"));
                    videos.add(video);
                }
                root.setSuccess(true);
                root.setResult(videos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IVideoDetails details(String html) {
        Node node = new Node(html);
        XiaokanbaDetails details = new XiaokanbaDetails();
        try {
            List<XiaokanbaVideo> reco = new ArrayList<>();
            List<XiaokanbaEpisode> episodes = new ArrayList<>();
            details.setTitle(node.text("div.head > h3"));
            details.setIntroduce(node.text("div.plot"));
            details.setArea(node.textAt("span.text-muted", 3));
            details.setTypes(node.textAt("span.text-muted", 4));
            details.setAuthor(node.textAt("span.text-muted", 1));
            for (Node n : node.list("div.item > div.col-md-2.col-sm-3.col-xs-4 ")) {
                XiaokanbaVideo video = new XiaokanbaVideo();
                video.setCover(n.attr("a", "data-original"));
                video.setId(n.attr("a", "href", "/", 1));
                video.setTitle(n.attr("a", "title"));
                video.setUrl("http://xiaokanba.com" + n.attr("a", "href").replace(".", ""));
                video.setScore(n.text("a > span.score"));
                reco.add(video);
            }
            int source = 1;
            for (Node n : node.list("div[id^=playlist]")) {
                for (Node sub : n.list("ul > li > a")) {
                    XiaokanbaEpisode episode = new XiaokanbaEpisode();
                    episode.setTitle("播放源" + source + ":" + sub.text());
                    episode.setUrl("http://xiaokanba.com" + sub.attr("href").replace(".", ""));
                    episode.setId(sub.attr("href", "/", 1));
                    episodes.add(episode);
                }
                source++;
            }
            details.setEpisodes(episodes);
            details.setRecoms(reco);
            details.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(String html) {
        Node node = new Node(html);
        XiaokanbaPlayUrl playUrl = new XiaokanbaPlayUrl();
        try {
            Map<String, String> map = new HashMap<>();
            map.put("Referer", RetrofitManager.REQUEST_URL);
            map.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
            String url = "http://xiaokanba.com" + node.attr("iframe", "src").replace(".", "");
            byte[] bytes = StreamUtil.url2byte(url, map);
            String videoUrl = "";
            if (bytes != null && bytes.length > 0) {
                html = new String(bytes);
                if (html.contains("ODflv")) {
                    LogUtil.e("ODflv", "ODflv    解析");
                    int startPosition = html.indexOf("$.post(\"api.php\", {");
                    if (startPosition != -1) {
                        html = html.substring(startPosition + 18);
                        int endPosition = html.indexOf("\"},");
                        if (endPosition != -1) {
                            html = html.substring(0, endPosition + 2);
                            JSONObject jsonObject = new JSONObject(html);
                            String json = String.format("time=%s&key=%s&url=%s&type=%s", jsonObject.getString("time"), jsonObject.getString("key"), URLEncoder.encode(jsonObject.getString("url")), jsonObject.getString("type"));
                            map.put("Referer", "https://p.xiaokanba.com/odflv/index.php?url=" + URLEncoder.encode(jsonObject.getString("url")));
                            map.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                            byte[] bs = StreamUtil.doPoset("https://p.xiaokanba.com/odflv/api.php", json, map);
                            if (bs != null && bs.length > 0) {
                                videoUrl = new JSONObject(new String(bs)).getString("url");
                            }
                        }
                    }
                } else if (html.contains("DPlayer")) {
                    LogUtil.e("DPlayer", "DPlayer    解析");
                    int startPosition = html.indexOf("url: '");
                    if (startPosition != -1) {
                        html = html.substring(startPosition + 6);
                        int endPosition = html.indexOf("',");
                        if (endPosition != -1) {
                            videoUrl = html.substring(0, endPosition);
                        }
                    }
                } else if (html.contains("play_url")) {
                    LogUtil.e("play_url", "play_url    解析");
                    int startPosition = html.indexOf("play_url='");
                    if (startPosition != -1) {
                        html = html.substring(startPosition + 10);
                        int endPosition = html.indexOf("',");
                        if (endPosition != -1) {
                            videoUrl = html.substring(0, endPosition);
                        }
                    }
                } else if (html.contains("$.ajax")) {
                    LogUtil.e("ajax", "ajax    解析");
                    int startPosition = html.indexOf("url: '");
                    if (startPosition != -1) {
                        html = html.substring(startPosition + 6);
                        int endPosition = html.indexOf("',");
                        if (endPosition != -1) {
                            String mUrl = "http:" + html.substring(0, endPosition);
                            byte[] bytess = StreamUtil.url2byte(mUrl);
                            if (bytess != null && bytess.length > 0) {
                                String json = new String(bytess).replace("var tvInfoJs=", "");
                                videoUrl = new JSONObject(json).getJSONObject("data").getJSONArray("vidl").getJSONObject(0).getString("m3u");
                            }
                        }
                    }
                } else if (html.contains("vParse_Play")) {
                    LogUtil.e("vParse_Play", "vParse_Play    解析");
                    int startPosition = html.indexOf("urls: [{\"u\":\"");
                    if (startPosition != -1) {
                        html = html.substring(startPosition + 13);
                        int endPosition = html.indexOf("\",");
                        if (endPosition != -1) {
                            videoUrl = html.substring(0, endPosition);
                        }
                    }
                }
                if (!TextUtils.isEmpty(videoUrl)) {
                    long millis = System.currentTimeMillis();
                    if(videoUrl.contains("video.qq.com")){
                        videoUrl = "https:" + videoUrl + "&filename=video.mp4&callback=getvideo&_=" + millis;
                        String guid = JavaScriptUtil.match("guid=[\\w\\d]+&",videoUrl,0,5,1);
                        String sdtfrom = JavaScriptUtil.match("sdtfrom=[\\w\\d]+&",videoUrl,0,8,1);
                        videoUrl = StreamUtil.url2String(videoUrl);
                        if (!TextUtils.isEmpty(videoUrl)) {
                            JSONObject jsonObject = new JSONObject(videoUrl.substring(9, videoUrl.length() - 1));
                            String vkey =  jsonObject.getString("key");
                            String filename =  jsonObject.getString("filename");
                            videoUrl = String.format("http://36.250.4.15/vlive.qqvideo.tc.qq.com/AIenJ3VT8eg39eYtdbkbKkgK-16e2gf8Q5enMzE50BsY/%s?sdtfrom=%s&guid=%s&vkey=%s",filename,sdtfrom,guid,vkey);
                        }
                    }
                    if(videoUrl.contains("http://cache.m.iqiyi.com/jp/tmts/")){
                        videoUrl = new JSONObject(StreamUtil.url2String(videoUrl).replace("var tvInfoJs=", "")).getJSONObject("data").getJSONArray("vidl").getJSONObject(0).getString("m3u");
                    }
                    if(videoUrl.contains("https://ups.youku.com") || videoUrl.contains("http://ups.youku.com")){
                        String vid = JavaScriptUtil.match("vid=[\\w\\d]+==&", videoUrl, 0, 4, 3);
                        String ccode = JavaScriptUtil.match("ccode=[\\w\\d]+&", videoUrl, 0, 6,1);
                        videoUrl = "https://ups.youku.com/ups/get.json?callback=json" + millis + "&vid=" + vid +"&ccode="+ccode+"&client_ip="+ SystemUtil.getHostIP()+"&utid=U7a%2FEW4SsSsCAdzKmCvvEJEf&client_ts=" + millis;
                        videoUrl = StreamUtil.url2String(videoUrl);
                        if(!TextUtils.isEmpty(videoUrl)){
                            videoUrl = new JSONObject(videoUrl.substring(18,videoUrl.length()-1)).getJSONObject("data").getJSONArray("stream").getJSONObject(0).getString("m3u8_url");
                        }
                    }
                    Map<String, String> playMap = new HashMap<>();
                    playMap.put("标清", videoUrl);
                    playUrl.setSuccess(true);
                    playUrl.setUrls(playMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

    @Override
    public IBangumiMoreRoot more(String html) {
        return search(html);
    }

}
