package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.VideoBase;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.jsoup.ITvParser;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.ICanTvService;
import com.fanchen.imovie.util.JavaScriptUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.StreamUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 * ICanTvParser
 * Created by fanchen on 2018/10/12.
 */
public class ICanTvParser implements ITvParser {

    @Override
    public List<IBaseVideo> liveList(Retrofit retrofit, String baseUrl, String html) {
        List<IBaseVideo> videos = new ArrayList<>();
        List<String> videoTitles = new ArrayList<>();
        try {
            for (Node node : new Node(html).list("ul > li.channel > div > a")) {
                VideoBase videoBase = new VideoBase();
                videoBase.setId(baseUrl + node.attr("href"));
                videoBase.setUrl(baseUrl + node.attr("href"));
                videoBase.setTitle(node.text());
                videoBase.setServiceClass(ICanTvService.class.getName());
                if (!videoTitles.contains(videoBase.getTitle())) {
                    videoTitles.add(videoBase.getTitle());
                    videos.add(videoBase);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videos;
    }

    @Override
    public IPlayUrls liveUrl(Retrofit retrofit, String baseUrl, String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {
            String match = JavaScriptUtil.match("iframe src=[\\S\\w\\d]+\"", html, 0, 13, 2).replace("\\", "");
            LogUtil.e("liveUrl", "match -> " + match);
            Map<String, String> header = new HashMap<>();
            header.put("Referer", RetrofitManager.REQUEST_URL);
            header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
            header.put("Accept-Encoding", "gzip, deflate");
            header.put("Accept-Encoding", "zh-CN,zh;q=0.9");
            header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            String s = StreamUtil.url2String(baseUrl + match, header);
            LogUtil.e("liveUrl", "s -> " + s);
            String attr = new Node(s).attr("iframe", "src");
            Map<String, String> mapUrl = new HashMap<>();
            playUrl.setReferer(RetrofitManager.REQUEST_URL);
            playUrl.setUrls(mapUrl);
            LogUtil.e("liveUrl", "attr -> " + attr);
            if (!attr.isEmpty()) {
                mapUrl.put("标清", attr);
                playUrl.setUrlType(IPlayUrls.URL_WEB);
                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                playUrl.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playUrl;
    }

}
