package com.fanchen.imovie.jsoup;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;

import retrofit2.Retrofit;

/**
 * Created by fanchen on 2017/10/28.
 */
public interface IVideoMoreParser extends IVideoParser{

    /**
     *
     * @param retrofit
     * @param baseUrl
     * @param html
     * @return
     */
    IBangumiMoreRoot more(Retrofit retrofit,String baseUrl,String html);
}
