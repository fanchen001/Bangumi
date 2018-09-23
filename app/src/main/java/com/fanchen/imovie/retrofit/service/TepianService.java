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
 * Created by fanchen on 2017/11/8.
 */
@RetrofitType(RetrofitSource.TEPIAN_API)
public interface TepianService {
    /**
     *
     * @param path
     * @return
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.TEPIAN)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param path1
     * @param path2
     * @return
     */
    @GET("{path1}/{path2}.html")
    @JsoupType(JsoupSource.TEPIAN)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path1") String path1, @Path("path2") String path2);

    /**
     *
     * @param path1
     * @param path2
     * @param page
     * @return
     */
    @GET("{path1}/{path2}-{page}.html")
    @JsoupType(JsoupSource.TEPIAN)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path1") String path1, @Path("path2") String path2,@Path("page")Integer page);
    /**
     *
     * @param page
     * @param keyword
     * @return
     */
    @GET("search.php")
    @JsoupType(JsoupSource.TEPIAN)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Query("page") Integer page, @Query("searchword") String keyword);

    /**
     *
     * @param pid
     * @param page
     * @return
     */
    @GET("{path}/index-{page}.html")
    @JsoupType(JsoupSource.TEPIAN)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> more(@Path("path") String pid, @Path("page") Integer page);

    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.TEPIAN)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Url String url);

    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.TEPIAN)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IPlayUrls> playUrl(@Url String url);
}
