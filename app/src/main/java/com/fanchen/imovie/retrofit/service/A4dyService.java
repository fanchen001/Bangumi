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
 * Created by fanchen on 2017/10/28.
 */
@RetrofitType(RetrofitSource.A4DY_API)
public interface A4dyService {

    /**
     * @return
     */
    @GET("{path}")
    @RetrofitType(RetrofitSource.A4DY_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.A4DY)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     * @return
     */
    @GET("vod-type-id-{path}-pg-{page}.html")
    @RetrofitType(RetrofitSource.A4DY_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.A4DY)
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    /**
     * @param keyword
     * @return
     */
    @GET("vod-search-pg-{page}-wd-{keyword}.html")
    @RetrofitType(RetrofitSource.A4DY_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.A4DY)
    Call<IBangumiMoreRoot> search(@Path("page") Integer page, @Path("keyword") String keyword);


    /**
     * @return
     */
    @GET("vod-type-id-{path}-pg-{page}.html")
    @RetrofitType(RetrofitSource.A4DY_API)
    @MethodType(value = MethodSource.MORE)
    @JsoupType(JsoupSource.A4DY)
    Call<IBangumiMoreRoot> more(@Path("path") String pid, @Path("page") Integer page);


    /**
     * @param path
     * @return
     */
    @GET("vod-detail-id-{path}")
    @RetrofitType(RetrofitSource.A4DY_API)
    @MethodType(value = MethodSource.DETAILS)
    @JsoupType(JsoupSource.A4DY)
    Call<IVideoDetails> details(@Path("path") String path);

    /**
     * @param path
     * @return
     */
    @GET("{path}")
    @RetrofitType(RetrofitSource.A4DY_API)
    @MethodType(value = MethodSource.PLAYURL)
    @JsoupType(JsoupSource.A4DY)
    Call<IPlayUrls> playUrl(@Path("path") String path);
}
