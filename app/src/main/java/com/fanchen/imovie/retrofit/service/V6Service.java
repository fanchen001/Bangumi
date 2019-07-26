package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

@RetrofitType(RetrofitSource.V6_API)
public interface V6Service {
    /**
     *
     * @param type
     * @return
     */
    @GET("coop/mobile/index.php?padapi=coop-mobile-getlivelistnew.php")
    @JsoupType(JsoupSource.V6)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.HOME)
    Call<IHomeRoot> home(@Query("type") String type);

    /**
     *
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.V6)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.PLAYURL)
    Call<IPlayUrls> playUrl(@Url String url);
}
