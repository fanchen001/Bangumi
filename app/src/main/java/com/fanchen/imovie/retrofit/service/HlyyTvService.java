package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IPlayUrls;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * KuaikanTvService
 * Created by fanchen on 2018/8/6.
 */
@RetrofitType(RetrofitSource.HLYY_API)
public interface HlyyTvService {
    /**
     *
     * @param path
     * @return
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.KUAIKAN_TV)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_LIVE)
    Call<List<IBaseVideo>> liveList(@Path("path") String path);

    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.KUAIKAN_TV)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_LIVE)
    Call<IPlayUrls> liveUrl(@Url String url);
}
