package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.jren.JrenDetails;
import com.fanchen.imovie.entity.jren.JrenEpisode;
import com.fanchen.imovie.entity.jren.JrenHome;
import com.fanchen.imovie.entity.jren.JrenVideo;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.util.StreamUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fanchen on 2017/9/24.
 */
public class JrenImpl implements IVideoParser {

    @Override
    public IBangumiMoreRoot search(String html) {
        JrenHome more = new JrenHome();
        try {
            List<JrenVideo> videos = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(html);
            if (jsonObject.has("data")) {
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if (jsonData.has("posts")) {
                    JSONArray posts = jsonData.getJSONArray("posts");
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject object = posts.getJSONObject(i);
                        String durl = object.has("url") ? object.getString("url") : "";
                        String title = object.has("title") ? object.getString("title") : "";
                        String cover = "";
                        if(object.has("thumbnail")){
                            JSONObject author = object.getJSONObject("thumbnail");
                            if(author.has("url")){
                                cover = author.getString("url");
                            }
                        }
                        String info = "";
                        if(object.has("date")){
                            JSONObject author = object.getJSONObject("date");
                            if(author.has("human")){
                                info = author.getString("human");
                            }
                        }
                        String up = "";
                        if(object.has("author")){
                            JSONObject author = object.getJSONObject("author");
                            if(author.has("name")){
                                up = author.getString("name");
                            }
                        }
                        String aid = object.has("id") ? object.getString("id") : "";
                        JrenVideo item = new JrenVideo();
                        item.setTitle(title);
                        item.setId(aid);
                        item.setUrl(durl);
                        item.setCover(cover);
                        item.setExtras(info);
                        item.setUp(up);
                        videos.add(item);
                    }
                }
            }
            more.setList(videos);
            more.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            more.setSuccess(false);
        }
        return more;
    }

    @Override
    public IHomeRoot home(String html) {
        Node node = new Node(html);
        JrenHome root = new JrenHome();
        try {
            List<JrenVideo> videos = new ArrayList<>();
            for (Node n : node.list("div.row.card-container > section > div.card-bg")) {
                String cover = n.attr("a > img", "data-src");
                String durl = n.attr("a", "href");
                String title = n.attr("a", "title");
                String info = n.text("a > div.color-cat-container");
                String up = n.text("div.meta-container > a");
                String aid = n.attr("a", "href", "/", 4).replace(".html", "");
                JrenVideo item = new JrenVideo();
                item.setTitle(title);
                item.setId(aid);
                item.setUrl(durl);
                item.setCover(cover);
                item.setExtras(info);
                item.setUp(up);
                videos.add(item);
            }
            root.setList(videos);
            root.setSuccess(true);
        } catch (Exception e) {
            root.setSuccess(false);
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IVideoDetails details(String html) {
        String startUrl = "https://jren100.moe/?p=";
        int start = html.indexOf(startUrl);
        JrenDetails details = new JrenDetails();
        try{
            if(start != -1){
                String url = html.substring(start,start + startUrl.length() + 5);
                Map<String, String> header = new HashMap<>();
                header.put("Cookie", "wordpress_test_cookie=WP+Cookie+check; Hm_lvt_5672553e8af14393d309ed5014151ae0=1505464427; Hm_lpvt_5672553e8af14393d309ed5014151ae0=1505464491; wordpress_logged_in_655c5bda33877be6d5d9650e5342f445=Sa79XtcUv80jGlA1%7C1506674155%7Cuj0MtWsNIycr31R1CDaTQtBH1KUMWXR0yBPllKAhGH2%7C6cecc172f5acff3986e583b4a90c98cc89d22de59f01233cecf83107b1cbb250");
                byte[] bs = StreamUtil.url2byte(url, header);
                if(bs != null && bs.length > 0){
                    html = new String(bs);
                }
                details = parserRoot(details,html);
            }else{
                details = parserRoot(details,html);
            }
            details.setSuccess(true);
        }catch (Exception e){
            details.setSuccess(false);
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(String html) {
        return null;
    }

    private JrenDetails parserRoot(JrenDetails details,String html){
        Node node = new Node(html);
        List<JrenEpisode> chapters = new ArrayList<>();
        List<JrenVideo> items = new ArrayList<>();
        details.setEpisodes(chapters);
        details.setRecoms(items);
        details.setIntroduce(node.text("div.entry-content.content-reset > table"));
        for (Node n : node.list("div > section > div.card-bg")) {
            String cover = n.attr("a > img", "data-src");
            String durl = n.attr("a", "href");
            String title = n.attr("a", "title");
            String info = n.text("a > div.color-cat-container");
            String aid = n.attr("a", "href", "/", 4);
            String up = n.text("div.meta-container > a");
            JrenVideo item = new JrenVideo();
            item.setTitle(title);
            item.setUp(up);
            item.setId(aid);
            item.setExtras(info);
            item.setUrl(durl);
            item.setCover(cover);
            items.add(item);
        }
        List<String> addVideo = addVideo(html);
        if(addVideo != null){
            for (int i = 0; i < addVideo.size(); i++) {
                JrenEpisode chapter = new JrenEpisode();
                chapter.setTitle("第" + (i + 1) + "集");
                chapter.setUrl(addVideo.get(i));
                chapters.add(chapter);
            }
        }
        return details;
    }

    public static  List<String> addVideo(String src){
        Pattern pattern = Pattern.compile("addVideo\\([\\d\\w;,\"&=]+\\);");
        Matcher matcher = pattern.matcher(src);
        List<String> urls = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group();
            urls.add(String.format("http://api.moehuan.com/189/mobile.php?%s",group.substring(18, group.length() - 8)));
        }
        return urls;
    }
}
