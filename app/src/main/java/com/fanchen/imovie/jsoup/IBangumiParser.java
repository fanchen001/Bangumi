package com.fanchen.imovie.jsoup;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IBangumiTimeRoot;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/9/18.
 */
public interface IBangumiParser extends IVideoMoreParser {

    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    IBangumiMoreRoot ranking(Retrofit retrofit,String baseUrl,String html);


    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    IBangumiTimeRoot timeLine(Retrofit retrofit,String baseUrl,String html);

}
