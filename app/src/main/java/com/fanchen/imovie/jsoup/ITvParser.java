package com.fanchen.imovie.jsoup;

import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IPlayUrls;

import java.util.List;

import retrofit2.Retrofit;

/**
 * ITvParser
 * Created by fanchen on 2018/8/6.
 */
public interface ITvParser {
    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    List<IBaseVideo> liveList(Retrofit retrofit,String baseUrl,String html);

    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    IPlayUrls liveUrl(Retrofit retrofit,String baseUrl,String html);
}
