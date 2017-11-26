package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by fanchen on 2017/10/12.
 */
@RetrofitType(RetrofitSource.BILIPLUS_API)
public interface BiliplusService {

    /**
     * @param path
     * @return
     */
    @GET("api/view")
    @JsoupType(JsoupSource.BILIPLUS)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = true)
    Call<IVideoDetails> details(@Query("id") String path);

    /**
     * @param path
     * @return
     */
    @GET("/api/geturl?bangumi=0&page=1")
    @JsoupType(JsoupSource.BILIPLUS)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = true)
    Call<IPlayUrls> playUrl(@Query("av") String path);
}
