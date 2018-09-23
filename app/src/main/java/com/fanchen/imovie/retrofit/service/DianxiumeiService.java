package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by fanchen on 2017/10/13.
 */
@RetrofitType(value = RetrofitSource.DIANXIUMEI_API, isJsoupResponse = JsoupSource.TYPE_VIDEO)
public interface DianxiumeiService {

    /**
     *
     * @param url
     * @param page
     * @return
     */
    @GET
    @JsoupType(JsoupSource.DIANXIUMEI)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.HOME)
    Call<IHomeRoot> home(@Url String url, @Query("p") Integer page);

    /**
     *
     * @param page
     * @param word
     * @return
     */
    @GET("so/s.php")
    @JsoupType(JsoupSource.DIANXIUMEI)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.SEARCH)
    Call<IBangumiMoreRoot> search(@Query("p") Integer page, @Query("q") String word);

    /**
     *
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.DIANXIUMEI)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.PLAYURL)
    @Headers({"User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1"})
    Call<IPlayUrls> playUrl(@Url String url);

}
