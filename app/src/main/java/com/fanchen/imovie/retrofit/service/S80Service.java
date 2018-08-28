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
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by fanchen on 2017/9/23.
 */
@RetrofitType(RetrofitSource.S80_API)
public interface S80Service {

    /**
     *
     * @param path
     * @param page
     * @return
     */
    @GET("movie/{path}-{page}-0-0-0-0-0")
    @JsoupType(JsoupSource.S80)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    /**
     * @param keyword
     * @return
     */
    @POST("search")
    @FormUrlEncoded
    @JsoupType(JsoupSource.S80)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Field("keyword") String keyword);

    /**
     *
     * @param path
     * @return
     */
    @GET("movie/{id}")
    @JsoupType(JsoupSource.S80)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Path("id") String path);
}
