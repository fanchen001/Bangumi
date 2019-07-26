package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.entity.VideoHome;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoParser;
import com.fanchen.imovie.retrofit.service.V6Service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Retrofit;

public class V6Parser implements IVideoParser {

    private static String[] URLS = {"http://124-232-150-42.6rooms.com/v%s/playlist.m3u8","http://124-232-150-43.6rooms.com/v%s/playlist.m3u8","http://124-232-150-44.6rooms.com/v%s/playlist.m3u8"};

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return null;
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        VideoHome home = new VideoHome();
        try {
            JSONArray jsonArray = null;
            JSONObject content = new JSONObject(html).getJSONObject("content");
            if (content.has("roomList")) {
                JSONObject object = content.getJSONObject("roomList");
                jsonArray = joinJSONArray(object.getJSONArray("u0"),object.getJSONArray("u1"),object.getJSONArray("u2"),object.getJSONArray("u3"));
            } else {
                if (content.has("u0")) {
                    jsonArray = content.getJSONArray("u0");
                } else if (content.has("u1")) {
                    jsonArray = content.getJSONArray("u1");
                } else if (content.has("u2")) {
                    jsonArray = content.getJSONArray("u2");
                } else if (content.has("u3")) {
                    jsonArray = content.getJSONArray("u3");
                }
            }
            if (jsonArray != null) {
                List<IVideo> videos = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Video video = new Video();
                    video.setHasDetails(false);
                    video.setSource(Video.SOURCE_PLAY);
                    video.setServiceClass(V6Service.class.getName());
                    video.setCover(object.optString("pic"));
                    video.setId(object.optString("uid"));
                    video.setUrlReferer(baseUrl);
                    video.setDanmaku(object.optString("recTagName"));
                    video.setTitle(object.optString("userMood"));
                    video.setUrl(getUrl(object.optString("uid")));
                    video.setType(object.optString("username"));
                    videos.add(video);
                }
                home.setList(videos);
                home.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return home;
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        throw new RuntimeException("this method mot impl");
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        throw  new RuntimeException("this method mot impl");
    }

    private String getUrl(String uid){
       return String.format(URLS[new Random().nextInt(3) % 3],uid);
    }

    public static JSONArray joinJSONArray(JSONArray... array) {
        if (array.length == 1) return array[0];
        if (array.length == 2) return join(array[0], array[1]);
        JSONArray newArray = array[0];
        for (int i = 1; i < array.length; i++) {
            newArray = join(newArray, array[i]);
        }
        return newArray;
    }

    private static JSONArray join(JSONArray mData, JSONArray array) {
        StringBuilder buffer = new StringBuilder();
        try {
            int len = mData.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj = (JSONObject) mData.get(i);
                if (i == len - 1) buffer.append(obj.toString());
                else buffer.append(obj.toString()).append(",");
            }
            len = array.length();
            if (len > 0) buffer.append(",");
            for (int i = 0; i < len; i++) {
                JSONObject obj = (JSONObject) array.get(i);
                if (i == len - 1) buffer.append(obj.toString());
                else buffer.append(obj.toString()).append(",");
            }
            buffer.insert(0, "[").append("]");
            return new JSONArray(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
