package com.fanchen.imovie.util;

import com.fanchen.imovie.entity.face.IVideo;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by fanchen on 2018/9/15.
 */
public class VideoJsonUtil {

    public static IVideo json2Video(String info) {
        try {
            JSONObject jsonObject = new JSONObject(info);
            if (!jsonObject.has("thisClass")) return null;
            Class<?> forName = Class.forName(jsonObject.getString("thisClass"));
            return (IVideo) new Gson().fromJson(info, forName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
