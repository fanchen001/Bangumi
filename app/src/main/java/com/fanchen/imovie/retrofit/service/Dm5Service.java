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
     *
     * @return
     */
    @GET("{path}")
    @RetrofitType(RetrofitSource.DM5_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.DM5)
    Call<IHomeRoot> home(@Path("path")String path);

    @GET("video/{path}/page/{page}")
    @RetrofitType(RetrofitSource.BUMIMI_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.DM5)
    Call<IHomeRoot> home(@Path("path")String path,@Path("page")Integer page);

    @GET("page/{page}")
    @RetrofitType(RetrofitSource.DM5_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.DM5)
    Call<IBangumiMoreRoot> search(@Path("page")Integer page,@Query("s")String word);

    @GET("bangumi/{path}")
    @RetrofitType(RetrofitSource.DM5_API)
    @MethodType(value = MethodSource.DETAILS)
    @JsoupType(JsoupSource.DM5)
    Call<IVideoDetails> details(@Path("path") String path);

    @GET("bangumi/{path}")
    @RetrofitType(RetrofitSource.DM5_API)
    @MethodType(value = MethodSource.PLAYURL)
    @JsoupType(JsoupSource.DM5)
    Call<IPlayUrls> playUrl(@Path("path") String path,@Query("link") String link);

    /**
     *
     * @return
     */
    @GET("video/bgm/{pid}/page/{page}")
    @RetrofitType(RetrofitSource.DM5_API)
    @MethodType(value = MethodSource.MORE)
    @JsoupType(JsoupSource.DM5)
    Call<IBangumiMoreRoot> more(@Path("pid")String pid,@Path("page") Integer page);
}
