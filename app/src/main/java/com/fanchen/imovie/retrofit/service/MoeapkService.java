package com.fanchen.imovie.retrofit.service;


import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.apk.ApkData;
import com.fanchen.imovie.entity.apk.ApkDetails;
import com.fanchen.imovie.entity.apk.ApkEvaluat;
import com.fanchen.imovie.entity.apk.ApkItem;
import com.fanchen.imovie.entity.apk.ApkRoot;
import com.fanchen.imovie.entity.apk.ApkVideo;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by fanchen on 2017/7/16.
 */
@RetrofitType(RetrofitSource.MOEAPK_API)
public interface MoeapkService {

    @POST("app/getGameList?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.MOEAPK_API)
    Call<ApkRoot<ApkData<ApkItem>>> gameList(@Body RequestBody body);

    @POST("app/getAppList?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.MOEAPK_API)
    Call<ApkRoot<ApkData<ApkItem>>> appList(@Body RequestBody body);

    @POST("app/searchAll?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.MOEAPK_API)
    Call<ApkRoot<ApkData<ApkItem>>> search(@Body RequestBody body);

    @POST("app/getApp?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.MOEAPK_API)
    Call<ApkRoot<ApkDetails>> details(@Body RequestBody body);

    @POST("app/getAppVideoUrl?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.MOEAPK_API)
    Call<ApkRoot<ApkVideo>> videoUrl(@Body RequestBody body);

    @POST("article/getArticleList?api_v=5&code_v=157")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.MOEAPK_API)
    Call<ApkRoot<ApkData<ApkEvaluat>>> getArticleList(@Body RequestBody body);
}
