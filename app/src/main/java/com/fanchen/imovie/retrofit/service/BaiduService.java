package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.entity.baidu.SearchHitRoot;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Created by fanchen on 2017/9/17.
 */
@RetrofitType(RetrofitSource.BAIDU_API)
public interface BaiduService {

    /**
     *
     * @param wd
     * @return
     */
    @GET("5a1Fazu8AA54nxGko9WTAnF6hhy/su?json=1")
    @RetrofitType(isBaiduResponse = true)
    Call<SearchHitRoot> searchHit(@Query("wd") String wd);

}
