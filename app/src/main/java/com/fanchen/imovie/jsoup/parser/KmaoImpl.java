package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.kmao.KmaoDetails;
import com.fanchen.imovie.entity.kmao.KmaoEpisode;
import com.fanchen.imovie.entity.kmao.KmaoHome;
import com.fanchen.imovie.entity.kmao.KmaoPlayUrl;
import com.fanchen.imovie.entity.kmao.KmaoTitle;
import com.fanchen.imovie.entity.kmao.KmaoVideo;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.jsoup.node.Node;
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
 * Created by fanchen on 2017/10/28.
 */
public class KmaoImpl implements IVideoMoreParser {

    @Override
    public IBangumiMoreRoot search(String html) {
        Node node = new Node(html);
        KmaoHome home = new KmaoHome();
        try {
            List<KmaoVideo> videos = new ArrayList<>();
            home.setResult(videos);
            for (Node n : node.list("ul#resize_list > li")){
                String title = n.text("a > div > label.name");
                String cover = n.attr("a > div > img", "src");
                String clazz = n.text("div.list_info > p", 0);
                String type = n.text("div.list_info > p",1);
                String author = n.text("div.list_info > p",2);
                String url = "http://m.kkkkmao.com" + n.attr("a","href");
                String id = n.attr("a","href","/",2);
                KmaoVideo video = new KmaoVideo();
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IHomeRoot home(String html) {
        Node node = new Node(html);
        KmaoHome home = new KmaoHome();
        try {
            List<Node> list = node.list("div.modo_title.top");
            if(list != null && list.size() > 0){
                int count = 0;
                List<KmaoTitle> titles = new ArrayList<>();
                home.setList(titles);
                for (Node n : list){
                    String topTitle = n.text("h2");
                    String topUrl = "" + n.attr("i > a", "href");
                    String topId = n.attr("i > a", "href","/",1);
                    List<KmaoVideo> videos = new ArrayList<>();
                    KmaoTitle kmaoTitle = new KmaoTitle();
                    kmaoTitle.setTitle(topTitle);
                    kmaoTitle.setDrawable(SEASON[count++ % SEASON.length]);
                    kmaoTitle.setId(topId);
                    kmaoTitle.setUrl(topUrl);
                    kmaoTitle.setList(videos);
                    titles.add(kmaoTitle);
                    for (Node sub : new Node(n.getElement().nextElementSibling()).list("div > ul > li")){
                        String title = sub.text("a > div > label.name");
                        String cover = sub.attr("a > div > img", "src");
                        if(TextUtils.isEmpty(cover))
                            continue;
                        String hd = sub.text("a > div > label.title");
                        String url = "http://m.kkkkmao.com" + sub.attr("a","href");
                        String id = sub.attr("a","href","/",2);
                        KmaoVideo video = new KmaoVideo();
                        video.setCover(cover);
                        video.setId(id);
                        video.setTitle(title);
                        video.setUrl(url);
                        video.setType(hd);
                        videos.add(video);
                    }
                    kmaoTitle.setMore(kmaoTitle.getList().size() != 10);
                }
            }else{
                List<KmaoVideo> videos = new ArrayList<>();
                for (Node n : node.list("div > div > ul > li")){
                    String title = n.text("h2");
                    String cover = n.attr("a > div > img", "src");
                    if(TextUtils.isEmpty(cover))
                        continue;
                    String hd = n.text("a > div > label.title");
                    String area = n.text("p");
                    String url = "http://m.kkkkmao.com" + n.attr("a","href");
                    String id = n.attr("a","href","/",2);
                    KmaoVideo video = new KmaoVideo();
                    video.setCover(cover);
                    video.setId(id);
                    video.setAuthor(area);
                    video.setTitle(title);
                    video.setUrl(url);
                    video.setType(hd);
                    videos.add(video);
                }
                home.setResult(videos);
            }
            home.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IVideoDetails details(String html) {
        Node node = new Node(html);
        KmaoDetails details = new KmaoDetails();
        try {
            List<KmaoEpisode> episodes = new ArrayList<>();
            List<KmaoVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul.list_tab_img > li")){
                String title = n.text("h2");
                String cover = n.attr("a > div > img", "src");
                if(TextUtils.isEmpty(cover))
                    continue;
                String hd = n.text("a > div > label.title");
                String score = n.text("a > div > label.score");
                String url = "http://m.kkkkmao.com" + n.attr("a","href");
                String id = n.attr("a","href","/",2);
                KmaoVideo video = new KmaoVideo();
                video.setCover(cover);
                video.setId(id);
                video.setTitle(title);
                video.setUrl(url);
                video.setAuthor(score);
                video.setType(hd);
                videos.add(video);
            }
            int count = 0;
            List<Node> list = node.list("div.play-title > span[id]");
            for (Node n : node.list("div.play-box > ul")){
                for (Node sub : n.list("li")){
                    KmaoEpisode episode = new KmaoEpisode();
                    episode.setId("http://m.kkkkmao.com" + sub.attr("a", "href"));
                    episode.setUrl("http://m.kkkkmao.com" + sub.attr("a", "href"));
                    if(list.size() > count){
                        episode.setTitle(list.get(count).text() + "_" + sub.text());
                    }else{
                        episode.setTitle(sub.text());
                    }
                    episodes.add(episode);
                }
                count ++ ;
            }
            details.setCover(node.attr("div.vod-n-img > img.loading", "src"));
            details.setClazz(node.textAt("div.vod-n-l > p", 0));
            details.setType(node.textAt("div.vod-n-l > p", 1));
            details.setAuthor(node.textAt("div.vod-n-l > p",2));
            details.setTitle(node.text("div.vod-n-l > h1"));
            details.setIntroduce(node.text("div.vod_content"));
            details.setEpisodes(episodes);
            details.setRecoms(videos);
            details.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(String html) {
        KmaoPlayUrl playUrl = new KmaoPlayUrl();
        try{
            String attr = new Node(html).attr("iframe", "src");
            Map<String, String> map = new HashMap<>();
            map.put("Referer", "http://m.kkkkmao.com/");
            map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            map.put("Upgrade-Insecure-Requests", "1");
            html = StreamUtil.url2String(attr, map);
            if (!TextUtils.isEmpty(html)) {
                String match = JavaScriptUtil.match("eval(.*?)\\n", html, 1);
                String e2 = JavaScriptUtil.getKkkkmaoE2(match);
                String urlplay1 = JavaScriptUtil.match("var urlplay1 = '*[\\d_]+';", html, 0, 16, 2);
                String tm = JavaScriptUtil.match("var tm = '*[\\d]+';",html, 0, 10, 2);
                String sign = JavaScriptUtil.match("var sign = '*[\\d\\w]+';",html, 0, 12, 2);
                String refer = JavaScriptUtil.match("var refer = '*[\\d\\w%.-]+';",html, 0, 13, 2);
                String url = String.format("https://yun.cdshiyunhui.com/parse.php?h5url=null&id=%s&tm=%s&sign=%s&script=1&userlink=%s&e2=%s&_=%s", urlplay1, tm, sign, refer, e2, String.valueOf(System.currentTimeMillis()));
                String encode = URLEncoder.encode(attr.substring(attr.lastIndexOf("=") + 1));
                map = new HashMap<>();
                map.put("Referer", attr.substring(0,attr.lastIndexOf("=")) + "=" + encode);
                map.put("X-Requested-With", "XMLHttpRequest");
                map.put("Accept-Encoding", "gzip, deflate, br");
                map.put("Accept-Language", "zh-CN,zh;q=0.8");
                map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
                map.put("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
                map.put("X-Requested-With", "XMLHttpRequest");
                html = StreamUtil.url2String(url, map);
                String pUrl = JavaScriptUtil.match("\"vid\":\"[\\d\\w=]+\",",html,0,7,2);
                String ccode = JavaScriptUtil.match("\"ccode\":\"[\\w\\d]+\"",html,0,9,1);
                if(!TextUtils.isEmpty(pUrl) && !TextUtils.isEmpty(ccode)){
                    LogUtil.e("KmaoImpl", "优酷解析");
                    long millis = System.currentTimeMillis();
                    pUrl = "https://ups.youku.com/ups/get.json?callback=json" + millis + "&vid=" + URLEncoder.encode(pUrl) +"&ccode="+ccode+"&client_ip="+ SystemUtil.getHostIP()+"&utid=U7a%2FEW4SsSsCAdzKmCvvEJEf&client_ts=" + millis;
                    pUrl = StreamUtil.url2String(pUrl);
                    if(!TextUtils.isEmpty(pUrl)){
                        pUrl = new JSONObject(pUrl.substring(18,pUrl.length()-1)).getJSONObject("data").getJSONArray("stream").getJSONObject(0).getString("m3u8_url");
                    }
                }else{
                    pUrl = JavaScriptUtil.match("\"http[\\d\\w:/.=?&-]+\"", html, 0, 1, 1);
                    LogUtil.e("KmaoImpl", "普通解析");
                }
                if(TextUtils.isEmpty(pUrl)){
                    LogUtil.e("KmaoImpl", "爱奇艺解析");
                    pUrl = JavaScriptUtil.match("'//cache[\\d\\w:/.=?&-]+'",html,0,1,1);
                    if (!TextUtils.isEmpty(pUrl)) {
                        pUrl = StreamUtil.url2String(("https:" + pUrl).replace("callback=?", "callback=tmtsCallback")).replace("try{tmtsCallback(","").replace(");}catch(e){};","");
                        pUrl = new JSONObject(pUrl).getJSONObject("data").getString("m3u");
                    }
                }
                if(TextUtils.isEmpty(pUrl)){
                    LogUtil.e("KmaoImpl", "乐视解析");
                    pUrl = JavaScriptUtil.match("\"http[\\d\\w\\+&=?.:/-]+\"",html,0,1,1);
                }
                if(!TextUtils.isEmpty(pUrl)){
                    Map<String,String> mapUrl = new HashMap<>();
                    mapUrl.put("标清",pUrl);
                    playUrl.setUrls(mapUrl);
                    playUrl.setPlayType(pUrl.startsWith("http://cn-") ? IVideoEpisode.PLAY_TYPE_ZZPLAYER : IVideoEpisode.PLAY_TYPE_VIDEO);
                    playUrl.setSuccess(true);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return playUrl;
    }

    @Override
    public IBangumiMoreRoot more(String html) {
        return (IBangumiMoreRoot)home(html);
    }

}
