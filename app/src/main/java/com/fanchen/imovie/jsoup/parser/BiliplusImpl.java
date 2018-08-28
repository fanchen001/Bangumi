package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.VideoDetails;
import com.fanchen.imovie.entity.VideoEpisode;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.retrofit.service.BiliplusService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

/**
 *
 * Created by fanchen on 2017/10/12.
 */
public class BiliplusImpl implements IVideoParser {
    private static final String VIDEOURL = "https://www.Video.com/video/av%s";
    private static final String IFRAMEURL = "https://www.Video.com/api/h5play.php?iframe&name=&cid=%s&type=%s&vid=%s&bangumi=0";

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit,String baseUrl,String html) {
        throw new RuntimeException("this method not impl");
    }

    @Override
    public IHomeRoot home(Retrofit retrofit,String baseUrl,String html) {
        throw new RuntimeException("this method not impl");
    }

    @Override
    public IVideoDetails details(Retrofit retrofit,String baseUrl,String html) {
        VideoDetails details = new VideoDetails();
        try{
            JSONObject jsonObject = new JSONObject(html);
            details.setServiceClass(BiliplusService.class.getName());
            details.setId(jsonObject.has("id") ? jsonObject.getString("id") : "");
            details.setTitle(jsonObject.has("title") ? jsonObject.getString("title") : "");
            details.setCover(jsonObject.has("pic") ? jsonObject.getString("pic") : "");
            details.setIntroduce(jsonObject.has("description") ? jsonObject.getString("description") : "");
            details.setLast(jsonObject.has("lastupdate") ? jsonObject.getString("lastupdate") : "");
            details.setDanmaku(jsonObject.has("typename") ? jsonObject.getString("typename") : "");
            details.setExtras(jsonObject.has("author") ? jsonObject.getString("author") : "");
            details.setUrl(String.format(VIDEOURL,details.getId()));//list
            if(jsonObject.has("list")){
                List<VideoEpisode> episodes = new ArrayList<>();
                JSONArray list = jsonObject.getJSONArray("list");
                for (int i = 0; i < list.length() ; i ++){
                    JSONObject object = list.getJSONObject(i);
                    VideoEpisode episode = new VideoEpisode();
                    episode.setPlayType(VideoEpisode.PLAY_TYPE_NOT);
                    episode.setServiceClass(BiliplusService.class.getName());
                    episode.setId(object.has("cid") ? object.getString("cid") : "");
                    episode.setTitle(object.has("part") ? object.getString("part") :"第" + (i + 1) + "段");
                    episode.setExtend(object.has("vid") ? object.getString("vid") : "");
                    if(object.has("type")){
                        episode.setUrl(String.format(IFRAMEURL, episode.getId(), episode.getExtend(),object.getString("type")));
                    }
                    episodes.add(episode);
                }
                details.setEpisodes(episodes);
            }
            details.setSuccess(true);
        }catch (Exception e){
            details.setSuccess(false);
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit,String baseUrl,String html) {
        VideoPlayUrls playUrl = new VideoPlayUrls();
        try {
            JSONObject object = new JSONObject(html);
            Map<String,String> map = new HashMap<>();
            if(object.has("cid")){
                playUrl.setCid(object.getString("cid"));
            }
            if(object.has("data")){
                JSONArray data = object.getJSONArray("data");
                for (int i = 0; i < data.length() ; i ++){
                    JSONObject jsonObject = data.getJSONObject(i);
                    String name = "";
                    if(jsonObject.has("name"))
                        name = jsonObject.getString("name");
                    if(jsonObject.has("url")){
                        map.put(name,jsonObject.getString("url"));
                    }
                    if(jsonObject.has("parts")){
                        JSONArray partsJSONArray = jsonObject.getJSONArray("parts");
                        for (int j = 0; j < partsJSONArray.length() ; j ++){
                            JSONObject partsJSONObject = partsJSONArray.getJSONObject(j);
                            if(partsJSONObject.has("url")){
                                map.put(name,partsJSONObject.getString("url"));
                            }
                        }
                    }
                }
            }
            if(!map.isEmpty()){
                playUrl.setUrls(map);
                playUrl.setSuccess(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            playUrl.setSuccess(false);
        }
        return playUrl;
    }

}
