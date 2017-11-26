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
 * Created by fanchen on 2017/10/28.
 */
@RetrofitType(RetrofitSource.K8DY_API)
public interface K8dyService {

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.K8DY)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = true)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param path1
     * @param path2
     * @return
     */
    @GET("{path1}/{path2}")
    @JsoupType(JsoupSource.K8DY)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = true)
    Call<IHomeRoot> home(@Path("path1") String path1, @Path("path2") String path2);

    /**
     *
     * @param path
     * @param page
     * @return
     */
    @GET("list/{path}_{page}.html")
    @JsoupType(JsoupSource.K8DY)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = true)
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    /**
     *
     * @param page
     * @param keyword
     * @return
     */
    @GET("search.php")
    @JsoupType(JsoupSource.K8DY)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = true)
    Call<IBangumiMoreRoot> search(@Query("page") Integer page, @Query("searchword") String keyword);

    /**
     *
     * @param pid
     * @param page
     * @return
     */
    @GET("list/{path}_{page}.html")
    @JsoupType(JsoupSource.K8DY)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = true)
    Call<IBangumiMoreRoot> more(@Path("path") String pid, @Path("page") Integer page);

    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.K8DY)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = true)
    Call<IVideoDetails> details(@Url String url);

    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.K8DY)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = true)
    Call<IPlayUrls> playUrl(@Url String url);
}
