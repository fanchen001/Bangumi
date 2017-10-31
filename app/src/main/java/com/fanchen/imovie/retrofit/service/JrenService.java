package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by fanchen on 2017/9/24.
 */
@RetrofitType(RetrofitSource.JREN_API)
public interface JrenService {
    /**
     * @return
     */
    @GET("archives/category/dm/{path}/page/{page}")
    @RetrofitType(RetrofitSource.JREN_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.JREN)
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    @POST("wp-admin/admin-ajax.php?_nonce=5dff0382bf&action=fb3c8529e9820fef2769456c19d04292")
    @RetrofitType(RetrofitSource.JREN_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.JREN)
    @Headers({"Connection: keep-alive",
            "Accept: */*",
            "User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Accept-Encoding: deflate, br",
            "Accept-Language: zh-CN,zh;q=0.8",
            "Cookie: vpid[16562]=1; wordpress_test_cookie=WP+Cookie+check; wordpress_logged_in_655c5bda33877be6d5d9650e5342f445=Sa79XtcUv80jGlA1%7C1508319436%7CcyBpgafx6txNN5gQD2teJzetvCoQSiLeavdXuPvgcez%7C4d0a5b375846d1d7d91bfe319b49fac54a389c81f38c1787157d48d540671e13; PHPSESSID=kf2cfgsj9dd6rrtkdou0i7lfmt; Hm_lvt_5672553e8af14393d309ed5014151ae0=1507109704; Hm_lpvt_5672553e8af14393d309ed5014151ae0=1507109774"})
    Call<IBangumiMoreRoot> search(@Header("Referer") String Referer,@Query("query") String query);

    @GET("archives/{id}")
    @RetrofitType(RetrofitSource.JREN_API)
    @MethodType(value = MethodSource.DETAILS)
    @JsoupType(JsoupSource.JREN)
    @Headers({"Connection: keep-alive",
            "Accept: */*",
            "User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Accept-Encoding: deflate, br",
            "Accept-Language: zh-CN,zh;q=0.8",
            "Cookie: vpid[16562]=1; wordpress_test_cookie=WP+Cookie+check; wordpress_logged_in_655c5bda33877be6d5d9650e5342f445=Sa79XtcUv80jGlA1%7C1508319436%7CcyBpgafx6txNN5gQD2teJzetvCoQSiLeavdXuPvgcez%7C4d0a5b375846d1d7d91bfe319b49fac54a389c81f38c1787157d48d540671e13; PHPSESSID=kf2cfgsj9dd6rrtkdou0i7lfmt; Hm_lvt_5672553e8af14393d309ed5014151ae0=1507109704; Hm_lpvt_5672553e8af14393d309ed5014151ae0=1507109774"})
    Call<IVideoDetails> details(@Path("id") String path);

}
