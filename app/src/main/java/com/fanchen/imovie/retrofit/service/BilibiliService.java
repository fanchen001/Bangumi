package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.bili.BilibiliIndex;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by fanchen on 2017/12/29.
 */
@RetrofitType(RetrofitSource.BILIBILI_API)
public interface BilibiliService {

    @GET("x/v2/search/hot?appkey=1d8b6e7d45233436&build=520001&limit=50&mobi_app=android&platform=android&sign=2ea767136ae9a784a594c5dc257f1af2")
    @RetrofitType(isJsonResponse = true)
    Call<BilibiliIndex> loadHotword(@Query("ts") String ts);

}
