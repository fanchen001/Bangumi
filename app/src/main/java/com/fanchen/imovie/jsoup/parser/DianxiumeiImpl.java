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
import com.fanchen.imovie.retrofit.service.DianxiumeiService;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * DianxiumeiImpl
 * Created by fanchen on 2017/10/13.
 */
public class DianxiumeiImpl implements IVideoMoreParser {

    private CcyParser impl = new CcyParser(DianxiumeiService.class.getName());

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
        Node node = new Node(html);
        try {
            Map<String, String> map = new HashMap<>();
            String iframesrc = node.attr("iframe", "src");
            if (!TextUtils.isEmpty(iframesrc)) {
                String splitUrl = iframesrc.split("=")[1].split("~")[0];
                if(splitUrl.contains(".m3u8")){
                    map.put("m3u8", splitUrl);
                    url.setUrlType(IPlayUrls.URL_M3U8);
                    url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                }else{
                    map.put("标清", splitUrl);
                    url.setUrlType(IPlayUrls.URL_FILE);
                    url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
                }
            } else {
                map.put("mp4", RetrofitManager.REQUEST_URL);
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
//        VideoHome root = new VideoHome();
//        try {
//            List<IVideo> videos = new ArrayList<>();
//            for (Node n : node.list("ul.list-pic > li.f-fl > dl")) {
//                Video video = new Video();
//                video.setServiceClass(DianxiumeiService.class.getName());
//                video.setId(n.attr("dt > a", "href", "=", 1));
//                video.setUrl(n.attr("dt > a", "href"));
//                video.setExtras(n.text("dd.d-i"));
//                video.setLast(n.text("dt > a > span.cnl-tag"));
//                video.setDanmaku(n.text("dt > a > span.cnl-tag"));
//                video.setTitle(n.text("dd.d-t"));
//                video.setCover(n.attr("dt > a > img", "src"));
//                videos.add(video);
//            }
//            root.setList(videos);
//            root.setSuccess(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return root;
//    }
//
//    @Override
//    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
//        return (VideoHome) search(retrofit, baseUrl, html);
//    }
//
//    @Override
//    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
//        throw new RuntimeException("this method not impl");
//    }
//
//    @Override
//    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
//        VideoPlayUrls url = new VideoPlayUrls();
//        Node node = new Node(html);
//        try {
//            Map<String, String> map = new HashMap<>();
//            String src = node.html("section.s-bg-2 > video");
//            if (TextUtils.isEmpty(src)) {
//                String iframesrc = node.attr("iframe", "src");
//                String[] split = iframesrc.split("=");
//                if (iframesrc.contains("=") && split[1].contains(".m3u")) {
//                    map.put("m3u8", split[1]);
//                    url.setUrlType(IPlayUrls.URL_M3U8);
//                    url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
//                } else if (iframesrc.contains("=") && (split[1].contains(".mp4") || split[1].contains(".rm") || split[1].contains(".avi") || split[1].contains(".wmv"))) {
//                    map.put("mp4", split[1]);
//                    url.setUrlType(IPlayUrls.URL_FILE);
//                    url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//                } else if(iframesrc.contains("=")){
//                    String[] split1 = iframesrc.split("=");
//                    if(!StreamUtil.check(iframesrc)){
//                        map.put("weburl", "http://jx.a0296.cn/?url=" + split1[1]);
//                        url.setUrlType(IPlayUrls.URL_WEB);
//                        url.setM3u8Referer(true);
//                        url.setReferer("http://jx.daheiyun.com/?url=" + split1[1]);
//                        url.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
//                    }
//                }else {
//                    String s = StreamUtil.url2String(iframesrc);
//                    String match = JavaScriptUtil.match("video:\\{[\\w\\d\\s\\S]+\\}\\}", s, 0, 6, 1);
//                    String replace = match.replace("quality:", "\"quality\":").replace("name:", "\"name\":").replace("url:", "\"url\":").replace("defaultQuality:", "\"defaultQuality\":").replace("pic:", "\"pic\":").replace("type:", "\"type\":").replace("'", "\"").replace(",}", "}");
//                    JSONArray quality = new JSONObject(replace).optJSONArray("quality");
//                    for (int i = 0; i < quality.length(); i++) {
//                        JSONObject object = quality.optJSONObject(i);
//                        map.put(object.optString("name"), object.optString("url"));
//                    }
//                    url.setUrlType(IPlayUrls.URL_FILE);
//                    url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//                }
//            } else {
//                //source标签未关闭，导致jsoup不能正常解析
//                String match = JavaScriptUtil.match("<source src=\"*[\\w\\d:/_.?=&~-]+\" type=\"video/mp4\">", src, 0, 13, 19);
//                map.put("mp4", match);
//                url.setUrlType(IPlayUrls.URL_FILE);
//                url.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO);
//            }
//            url.setSuccess(true);
//            url.setUrls(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return url;
//    }

}
