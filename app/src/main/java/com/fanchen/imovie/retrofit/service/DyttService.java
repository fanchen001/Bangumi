package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.dytt.DyttLive;
import com.fanchen.imovie.entity.dytt.DyttLiveBody;
import com.fanchen.imovie.entity.dytt.DyttRoot;
import com.fanchen.imovie.entity.dytt.DyttShortVideo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * DyttService
 * Created by fanchen on 2017/9/20.
 */
@RetrofitType(RetrofitSource.DYTT_API)
public interface DyttService {

    /**
     * 短视频
     * @param tid
     * @param page
     * @return
     */
    @GET("newmovie/api/hotshortvideo")
    @RetrofitType(isJsonResponse = true)
    @Headers({"platVersion:5.1.1", "userId:WWNl6KssHOIDAFGqNssEweUo", "platform:android", "xigua:true", "thunder:true", "package:com.ghost.movieheaven", "appVersion:5.6.0"})
    Call<DyttRoot<List<DyttShortVideo>>> shortVideo(@Query("tid") String tid, @Query("page") Integer page);

    /**
     *
     * @param groupId
     * @param vc
     * @param _t
     * @return
     */
    @GET("newmovie/api/tvlive_channels")
    @RetrofitType(isJsonResponse = true)
    @Headers({"platVersion:5.1.1", "userId:WWNl6KssHOIDAFGqNssEweUo", "platform:android", "xigua:true", "thunder:true", "package:com.ghost.movieheaven", "appVersion:5.6.0"})
    Call<DyttRoot<List<DyttLive>>> liveVideo(@Query("groupId") String groupId, @Query("vc") String vc,@Query("_t") Long _t);

    /**
     *
     * @param channelId
     * @param vc
     * @param _t
     * @return
     */
    @GET("newmovie/api/tvlive_channel_info")
    @RetrofitType(isJsonResponse = true)
    @Headers({"platVersion:5.1.1", "userId:WWNl6KssHOIDAFGqNssEweUo", "platform:android", "xigua:true", "thunder:true", "package:com.ghost.movieheaven", "appVersion:5.6.0"})
    Call<DyttRoot<DyttLiveBody>> livesInfo(@Query("channelId") String channelId, @Query("vc") String vc,@Query("_t") Long _t);
}
