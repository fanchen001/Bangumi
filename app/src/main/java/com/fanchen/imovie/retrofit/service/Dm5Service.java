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

/**
 * Created by fanchen on 2017/10/2.
 */
@RetrofitType(RetrofitSource.DM5_API)
public interface Dm5Service {

    /**
     * @param path
     * @return
     */
    @GET("{path}")
    @JsoupType(JsoupSource.DM5)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     * @param path
     * @param page
     * @return
     */
    @GET("video/{path}/page/{page}")
    @JsoupType(JsoupSource.DM5)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    /**
     * @param page
     * @param word
     * @return
     */
    @GET("page/{page}")
    @JsoupType(JsoupSource.DM5)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Path("page") Integer page, @Query("s") String word);

    /**
     * @param path
     * @return
     */
    @GET("bangumi/{path}")
    @JsoupType(JsoupSource.DM5)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Path("path") String path);

    /**
     * @param path
     * @param link
     * @return
     */
    @GET("bangumi/{path}")
    @JsoupType(JsoupSource.DM5)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IPlayUrls> playUrl(@Path("path") String path, @Query("link") String link);

    /**
     * @param pid
     * @param page
     * @return
     */
    @GET("video/bgm/{pid}/page/{page}")
    @JsoupType(JsoupSource.DM5)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> more(@Path("pid") String pid, @Path("page") Integer page);
}
