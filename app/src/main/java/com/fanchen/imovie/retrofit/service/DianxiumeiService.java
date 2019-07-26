package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by fanchen on 2017/10/13.
 */
@RetrofitType(value = RetrofitSource.DIANXIUMEI_API, isJsoupResponse = JsoupSource.TYPE_VIDEO)
public interface DianxiumeiService {

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.DIANXIUMEI)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.HOME)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param path
     * @param page
     * @return
     */
    @GET("{path}/index-{page}.html")
    @JsoupType(JsoupSource.DIANXIUMEI)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    /**
     *
     * @param page
     * @param word
     * @return
     */
    @GET("search/{q}-{page}.html")
    @JsoupType(JsoupSource.DIANXIUMEI)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.SEARCH)
    Call<IBangumiMoreRoot> search(@Path("page") Integer page, @Path("q") String word);

    /**
     *
     * @param path
     * @param page
     * @return
     */
    @GET("{path}/index-{page}.html")
    @JsoupType(JsoupSource.DIANXIUMEI)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.MORE)
    Call<IBangumiMoreRoot> more(@Path("path") String path, @Path("page") Integer page);

    /**
     * @param path
     * @return
     */
    @GET("{path}")
    @JsoupType(JsoupSource.DIANXIUMEI)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Path("path") String path);

    /**
     *
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.DIANXIUMEI)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.PLAYURL)
    Call<IPlayUrls> playUrl(@Url String url);

}
