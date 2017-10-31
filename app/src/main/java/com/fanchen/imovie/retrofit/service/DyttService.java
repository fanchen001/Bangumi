package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.dytt.DyttLiveBody;
import com.fanchen.imovie.entity.dytt.DyttRoot;
import com.fanchen.imovie.entity.dytt.DyttShortVideo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by fanchen on 2017/9/20.
 */
@RetrofitType(RetrofitSource.DYTT_API)
public interface DyttService {

    @GET("newmovie/api/tvlives")
    @Headers({"platVersion:5.1.1", "userId:WWNl6KssHOIDAFGqNssEweUo","platform:android", "xigua:true", "thunder:true", "package:com.ghost.movieheaven", "appVersion:5.6.0"})
    @RetrofitType(RetrofitSource.DYTT_API)
    /********电视直播********/
    Call<DyttRoot<List<DyttLiveBody>>> live();

    @GET("newmovie/api/hotshortvideo")
    @Headers({"platVersion:5.1.1", "userId:WWNl6KssHOIDAFGqNssEweUo","platform:android", "xigua:true", "thunder:true", "package:com.ghost.movieheaven", "appVersion:5.6.0"})
    @RetrofitType(RetrofitSource.DYTT_API)
    /******短视频*******/
    Call<DyttRoot<List<DyttShortVideo>>> shortVideo(@Query("tid") String tid,@Query("page") Integer page);
}
