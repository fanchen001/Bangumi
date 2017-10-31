package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.xiaobo.XiaoboRoot;
import com.fanchen.imovie.entity.xiaobo.XiaoboVodBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by fanchen on 2017/10/15.
 */
@RetrofitType(RetrofitSource.XIAOBO_API)
public interface XiaoboService {

    @GET("Vod")
    @Headers({"platVersion:5.1.1", "userId:WWNl6KssHOIDAFGqNssEweUo","platform:android", "xigua:true", "thunder:true", "package:com.ghost.movieheaven", "appVersion:5.6.0"})
    @RetrofitType(RetrofitSource.XIAOBO_API)
    /******迅雷云播搜索*******/
    Call<XiaoboRoot> searchVod(@Query("keyword") String key,@Query("page")Integer page);
}
