package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.service.DianxiumeiService;
import com.fanchen.imovie.util.JavaScriptUtil;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/10/13.
 */
public class DianxiumeiImpl implements IVideoParser {

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html) {
        Node node = new Node(html);
        VideoHome root = new VideoHome();
        try {
            List<Video> videos = new ArrayList<>();
            for (Node n : node.list("ul.list-pic > li.f-fl > dl")){
                Video video = new Video();
                video.setServiceClass(DianxiumeiService.class.getName());
                video.setId(n.attr("dt > a", "href","=",1));
                video.setUrl(n.attr("dt > a", "href"));
                video.setExtras(n.text("dd.d-i"));
                video.setLast(n.text("dt > a > span.cnl-tag"));
                video.setDanmaku(n.text("dt > a > span.cnl-tag"));
                video.setTitle(n.text("dd.d-t"));
                video.setCover(n.attr("dt > a > img","src"));
                videos.add(video);
            }
            root.setList(videos);
            root.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
        return (VideoHome)search(retrofit,baseUrl,html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
        throw new RuntimeException("this method not impl");
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
        VideoPlayUrls url = new VideoPlayUrls();
        Node node = new Node(html);
        try {
            Map<String,String> map = new HashMap<>();
            String src = node.html("section.s-bg-2 > video");
            if(TextUtils.isEmpty(src)){
                map.put("m3u8", URLDecoder.decode(node.attr("iframe", "src", "=", 1)));
            }else{
                //source标签未关闭，导致jsoup不能正常解析
                String match = JavaScriptUtil.match("<source src=\"*[\\w\\d:/_.?=&~-]+\" type=\"video/mp4\">", src, 0, 13, 17);
                map.put("mp4",match);
            }
            url.setSuccess(true);
            url.setUrls(map);
        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

}
