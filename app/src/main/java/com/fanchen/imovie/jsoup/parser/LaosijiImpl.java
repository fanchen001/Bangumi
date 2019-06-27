package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.retrofit.service.LaosijiService;

import retrofit2.Retrofit;

/**
 * LaosijiImpl
 * Created by fanchen on 2018/7/27.
 */
public class LaosijiImpl implements IVideoMoreParser {

    private SmdyImpl smdy = new SmdyImpl(LaosijiService.class.getName(),true,false);

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return smdy.search(retrofit,baseUrl,html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return smdy.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        return smdy.details(retrofit, baseUrl, html);
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        return smdy.playUrl(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return smdy.more(retrofit, baseUrl, html);
    }
}
