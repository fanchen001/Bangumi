package com.fanchen.imovie.jsoup.parser;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.jsoup.IVideoMoreParser;
import com.fanchen.imovie.retrofit.service.ZzyoService;

import retrofit2.Retrofit;

/**
 * ZzyoImpl
 * Created by fanchen on 2018/11/15.
 */
public class ZzyoImpl implements IVideoMoreParser {

    private SmdyImpl impl = new SmdyImpl(ZzyoService.class.getName());

    @Override
    public IBangumiMoreRoot more(Retrofit retrofit, String baseUrl, String html) {
        return impl.more(retrofit, baseUrl, html);
    }

    @Override
    public IBangumiMoreRoot search(Retrofit retrofit, String baseUrl, String html) {
        return impl.search(retrofit, baseUrl, html);
    }

    @Override
    public IHomeRoot home(Retrofit retrofit, String baseUrl, String html) {
        return impl.home(retrofit, baseUrl, html);
    }

    @Override
    public IVideoDetails details(Retrofit retrofit, String baseUrl, String html) {
        return impl.details(retrofit, baseUrl, html);
    }

    @Override
    public IPlayUrls playUrl(Retrofit retrofit, String baseUrl, String html) {
        return impl.playUrl(retrofit, baseUrl, html);
    }

}
