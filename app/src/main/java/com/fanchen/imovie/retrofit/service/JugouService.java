package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 聚狗影院
 * Created by fanchen on 2018/6/13.
 */
@RetrofitType(RetrofitSource.JUGOU_API)
public interface JugouService {
    /**
     *
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Url String url);

    /**
     *
     * @param url
     * @param page
     * @return
     */
    @GET
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Url String url, @Query("page") Integer page);

    /**
     *
     * @param page
     * @param keyword
     * @return
     */
    @GET("seacher.php")
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Query("page") Integer page, @Query("wd") String keyword);

    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Url String url);

    /**
     *
     * @param url
     * @param page
     * @return
     */
    @GET
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> more(@Url String url, @Query("page") Integer page);
}
