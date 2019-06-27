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
 * 聚狗影院
 * Created by fanchen on 2018/6/13.
 */
@RetrofitType(RetrofitSource.JUGOU_API)
public interface JugouService {
    /**
     *
     * @param path
     * @return
     */
    @GET("{path}")
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param url
     * @param page
     * @return
     */
    @GET("type/index{path}-{page}.html")
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String url, @Path("page") Integer page);

    /**
     * @param page
     * @param keyword
     * @return
     */
    @GET("search.php")
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Query("page") Integer page, @Query("searchword") String keyword);

    /**
     * @param id
     * @return
     */
    @GET("movie/index{id}.html")
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Path("id") String id);

    /**
     *
     * @param id
     * @param page
     * @return
     */
    @GET("type/index{id}-{page}.html")
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> more(@Path("id") String id, @Path("page") Integer page);

    /**
     * @param id
     * @return
     */
    @GET("play/{id}.html")
    @JsoupType(JsoupSource.JUGOU)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IPlayUrls> playUrl(@Path("id") String id);
}
