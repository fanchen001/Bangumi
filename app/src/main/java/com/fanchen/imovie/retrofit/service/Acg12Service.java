package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.acg.AcgData;
import com.fanchen.imovie.entity.acg.AcgRoot;
import com.fanchen.imovie.entity.acg.AcgToken;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by fanchen on 2017/9/24.
 */
@RetrofitType(RetrofitSource.ACG12_API)
public interface Acg12Service {
    @POST("wp-admin/admin-ajax.php?action=3a83abb58190771625479890b3035831&type=getPostsByCategory")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.ACG12_API)
    Call<AcgRoot<AcgData>> getPostsByCategory(@Body RequestBody body);

    @POST("wp-admin/admin-ajax.php?action=3a83abb58190771625479890b3035831&type=getCategories")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.ACG12_API)
    Call<AcgRoot<AcgData>> getCategories(@Body RequestBody body);

    @POST("wp-admin/admin-ajax.php?action=3a83abb58190771625479890b3035831&type=getToken")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(RetrofitSource.ACG12_API)
    Call<AcgRoot<AcgToken>> getToken(@Body RequestBody body);

}
