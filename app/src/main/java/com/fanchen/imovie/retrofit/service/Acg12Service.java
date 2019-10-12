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

    /**
     *
     * @param body
     * @return
     */
    @POST("wp-admin/admin-ajax.php?action=c0bb4c20c6136e2ea6bb77713fff6675&type=getPostsByCategory")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<AcgRoot<AcgData>> getPostsByCategory(@Body RequestBody body);

    /**
     *
     * @param body
     * @return
     */
    @POST("wp-admin/admin-ajax.php?action=c0bb4c20c6136e2ea6bb77713fff6675&type=getCategories")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<AcgRoot<AcgData>> getCategories(@Body RequestBody body);

    /**
     *
     * @param body
     * @return
     */
    @POST("wp-admin/admin-ajax.php?action=c0bb4c20c6136e2ea6bb77713fff6675&type=getToken")
    @Headers({"Content-type:application/x-www-form-urlencoded","Accept-Language:zh-CN,zh;q=0.8"})
    @RetrofitType(isJsonResponse = true)
    Call<AcgRoot<AcgToken>> getToken(@Body RequestBody body);

}
