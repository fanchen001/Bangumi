package com.fanchen.imovie.jsoup.parser;

import android.text.TextUtils;

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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

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
        Map<String, String> mapUrl = new HashMap<>();
        playUrl.setReferer(RetrofitManager.REQUEST_URL);
        playUrl.setUrls(mapUrl);
        try {
            String match = JavaScriptUtil.match("iframe src=[\\S\\w\\d]+\"", html, 0, 13, 2).replace("\\", "");
            if(TextUtils.isEmpty(match)){
                String match1 = JavaScriptUtil.match("eval\\([\\u4e00-\\u9fa5\\(\\)\\{\\}\\[\\]\\\"\\w\\d`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\\{\\}\\)\\)", html, 0,5,1);
                String jsCode = "temp = " + match1 + ";";
                String s = JavaScriptUtil.evalDecrypt(jsCode);
                String match2 = JavaScriptUtil.match("\\[\\{[\\u4e00-\\u9fa5\\{\\}\\[\\]\\\"\\w\\d`~!@#$%^&*_\\-+=<>?:|,.\\\\ \\/;']+\\}\\]", s, 0);
                LogUtil.e("ICanTvParser","json -> " + match2);
                if(JavaScriptUtil.isJson(match2)){
                    JSONArray jsonArray = new JSONArray(match2);
                    for (int i = 0 ; i < jsonArray.length() ; i ++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        String pCode = jsonObject.getString("pCode");
                        String node = new Node(pCode).attr("video","src");
                        if(!TextUtils.isEmpty(node)){
                            mapUrl.put(name, node);
                            playUrl.setSuccess(true);
                            if(node.contains(".m3u")){
                                playUrl.setUrlType(IPlayUrls.URL_M3U8);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_VIDEO_M3U8);
                            }else{
                                playUrl.setUrlType(IPlayUrls.URL_WEB);
                                playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                            }
                        }
                    }
                }
            }else{
                Map<String, String> header = new HashMap<>();
                header.put("Referer", RetrofitManager.REQUEST_URL);
                header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
                header.put("Accept-Encoding", "gzip, deflate");
                header.put("Accept-Encoding", "zh-CN,zh;q=0.9");
                header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                String s = StreamUtil.url2String(baseUrl + match, header);
                LogUtil.e("liveUrl", "s -> " + s);
                String attr = new Node(s).attr("iframe", "src");
                if (!attr.isEmpty()) {
                    mapUrl.put("标清", attr);
                    playUrl.setUrlType(IPlayUrls.URL_WEB);
                    playUrl.setPlayType(IVideoEpisode.PLAY_TYPE_WEB);
                    playUrl.setSuccess(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e("ICanTvParser","playUrl->" + new Gson().toJson(playUrl));
        return playUrl;
    }

}
