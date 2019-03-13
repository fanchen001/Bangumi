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
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 战地视频
 * Created by fanchen on 2017/12/23.
 */
@RetrofitType(RetrofitSource.ZHANDI_API)
public interface ZhandiService {

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.ZHANDI)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param path
     * @param page
     * @return
     */
    @GET("{path}/index-{page}.html")
    @JsoupType(JsoupSource.ZHANDI)//index1.html
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    /**
     *
     * @param page
     * @param keyword
     * @return
     */
    @GET("search/{wd}-{page}.html")
    @JsoupType(JsoupSource.ZHANDI)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Path("page") Integer page, @Path("wd") String keyword);

    /**
     *
     * @param pid
     * @param page
     * @return
     */
    @GET("{path}/index-{page}.html")
    @JsoupType(JsoupSource.ZHANDI)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> more(@Path("path") String pid, @Path("page") Integer page);

    /**
     * @param path
     * @return
     */
    @GET("video/{path}.html")
    @JsoupType(JsoupSource.ZHANDI)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Path("path") String path);

    /**
     * @param path
     * @return
     */
    @GET
    @JsoupType(JsoupSource.ZHANDI)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IPlayUrls> playUrl(@Url String path);

//    /**
//     *
//     * @param path
//     * @return
//     */
//    @GET("{path}/")
//    @JsoupType(JsoupSource.ZHANDI)
//    @MethodType(value = MethodSource.HOME)
//    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
//    Call<IHomeRoot> home(@Path("path") String path);
//
//    /**
//     *
//     * @param path
//     * @param page
//     * @return
//     */
//    @GET("{path}/index-{page}.html")
//    @JsoupType(JsoupSource.ZHANDI)
//    @MethodType(value = MethodSource.HOME)
//    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
//    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);
//
//    /**
//     *
//     * @param page
//     * @param keyword
//     * @return
//     */
//    @GET("index.php/vod-search-wd-{wd}-p-{page}.html")
//    @JsoupType(JsoupSource.ZHANDI)
//    @MethodType(value = MethodSource.SEARCH)
//    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
//    Call<IBangumiMoreRoot> search(@Path("page") Integer page, @Path("wd") String keyword);
//
//
//    /**
//     * @param path
//     * @return
//     */
//    @GET
//    @JsoupType(JsoupSource.ZHANDI)
//    @MethodType(value = MethodSource.DETAILS)
//    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
//    Call<IVideoDetails> details(@Url String path);
//
//    /**
//     * @param path
//     * @return
//     */
//    @GET
//    @JsoupType(JsoupSource.ZHANDI)
//    @MethodType(value = MethodSource.PLAYURL)
//    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
//    Call<IPlayUrls> playUrl(@Url String path);
}
