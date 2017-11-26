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

    /**
     *
     * @param body
     * @return
     */
    @POST("app/getGameList?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<ApkRoot<ApkData<ApkItem>>> gameList(@Body RequestBody body);

    /**
     *
     * @param body
     * @return
     */
    @POST("app/getAppList?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<ApkRoot<ApkData<ApkItem>>> appList(@Body RequestBody body);

    /**
     *
     * @param body
     * @return
     */
    @POST("app/searchAll?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<ApkRoot<ApkData<ApkItem>>> search(@Body RequestBody body);

    /**
     *
     * @param body
     * @return
     */
    @POST("app/getApp?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<ApkRoot<ApkDetails>> details(@Body RequestBody body);

    /**
     *
     * @param body
     * @return
     */
    @POST("app/getAppVideoUrl?api_v=5&code_v=172")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<ApkRoot<ApkVideo>> videoUrl(@Body RequestBody body);

    /**
     *
     * @param body
     * @return
     */
    @POST("article/getArticleList?api_v=5&code_v=157")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<ApkRoot<ApkData<ApkEvaluat>>> getArticleList(@Body RequestBody body);
}
