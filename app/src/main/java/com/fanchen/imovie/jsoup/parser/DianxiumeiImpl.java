package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

import com.fanchen.imovie.entity.dianxiumei.DianxiumeiPlayUrl;
import com.fanchen.imovie.entity.dianxiumei.DianxiumeiHome;
import com.fanchen.imovie.entity.dianxiumei.DianxiumeiVideo;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.util.JavaScriptUtil;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanchen on 2017/10/13.
 */
public class DianxiumeiImpl implements IVideoParser {

    @Override
    public IBangumiMoreRoot search(String html) {
        Node node = new Node(html);
        DianxiumeiHome root = new DianxiumeiHome();
        try {
            List<DianxiumeiVideo> videos = new ArrayList<>();
            for (Node n : node.list("ul.list-pic > li.f-fl > dl")){
                DianxiumeiVideo video = new DianxiumeiVideo();
                video.setId(n.attr("dt > a", "href","=",1));
                video.setUrl(n.attr("dt > a", "href"));
                video.setExtras(n.text("dd.d-i"));
                video.setInfo(n.text("dt > a > span.cnl-tag"));
                video.setTopInfo(n.text("dt > a > span.cnl-tag"));
                video.setTitle(n.text("dd.d-t"));
                video.setCover(n.attr("dt > a > img","src"));
                videos.add(video);
            }
            root.setResult(videos);
            root.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public IHomeRoot home(String html) {
        return (DianxiumeiHome)search(html);
    }

    @Override
    public IVideoDetails details(String html) {
        throw new RuntimeException("this method not impl");
    }

    @Override
    public IPlayUrls playUrl(String html) {
        DianxiumeiPlayUrl url = new DianxiumeiPlayUrl();
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
