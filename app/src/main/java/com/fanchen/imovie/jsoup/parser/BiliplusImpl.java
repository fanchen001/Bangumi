package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.biliplus.BiliplusDetails;
import com.fanchen.imovie.entity.biliplus.BiliplusEpisode;
import com.fanchen.imovie.entity.biliplus.BiliplusPlayUrl;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by fanchen on 2017/10/12.
 */
public class BiliplusImpl implements IVideoParser {

    @Override
    public IBangumiMoreRoot search(String html) {
        throw new RuntimeException("this method not impl");
    }

    @Override
    public IHomeRoot home(String html) {
        throw new RuntimeException("this method not impl");
    }

    @Override
    public IVideoDetails details(String html) {
        BiliplusDetails details = new BiliplusDetails();
        try{
            JSONObject jsonObject = new JSONObject(html);
            details.setId(jsonObject.has("id") ? jsonObject.getString("id") : "");
            details.setTitle(jsonObject.has("title") ? jsonObject.getString("title") : "");
            details.setCover(jsonObject.has("pic") ? jsonObject.getString("pic") : "");
            details.setIntroduce(jsonObject.has("description") ? jsonObject.getString("description") : "");
            details.setLast(jsonObject.has("lastupdate") ? jsonObject.getString("lastupdate") : "");
            details.setDanmaku(jsonObject.has("typename") ? jsonObject.getString("typename") : "");
            details.setExtras(jsonObject.has("author") ? jsonObject.getString("author") : "");
            details.setUrl(String.format("https://www.biliplus.com/video/av%s",details.getId()));//list
            if(jsonObject.has("list")){
                List<BiliplusEpisode> episodes = new ArrayList<>();
                JSONArray list = jsonObject.getJSONArray("list");
                for (int i = 0; i < list.length() ; i ++){
                    JSONObject object = list.getJSONObject(i);
                    BiliplusEpisode episode = new BiliplusEpisode();
                    episode.setId(object.has("cid") ? object.getString("cid") : "");
                    episode.setTitle(object.has("part") ? object.getString("part") :"第" + (i + 1) + "段");
                    episode.setExtend(object.has("vid") ? object.getString("vid") : "");
                    if(object.has("type")){
                        episode.setUrl(String.format("https://www.biliplus.com/api/h5play.php?iframe&name=&cid=%s&type=%s&vid=%s&bangumi=0", episode.getId(), episode.getExtend(),object.getString("type")));
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
    public IPlayUrls playUrl(String html) {
        BiliplusPlayUrl playUrl = new BiliplusPlayUrl();
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
