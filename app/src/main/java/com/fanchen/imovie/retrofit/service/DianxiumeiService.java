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
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by fanchen on 2017/10/13.
 */
@RetrofitType(RetrofitSource.DIANXIUMEI_API)
public interface DianxiumeiService {

    /**
     *
     * @return
     */
    @GET
    @RetrofitType(RetrofitSource.DIANXIUMEI_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.DIANXIUMEI)
    Call<IHomeRoot> home(@Url String url,@Query("p")Integer page);

    @GET("so/s.php")
    @RetrofitType(RetrofitSource.DIANXIUMEI_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.DIANXIUMEI)
    Call<IBangumiMoreRoot> search(@Query("p")Integer page,@Query("q")String word);

    @GET
    @RetrofitType(RetrofitSource.DIANXIUMEI_API)
    @MethodType(value = MethodSource.PLAYURL)
    @JsoupType(JsoupSource.DIANXIUMEI)
    Call<IPlayUrls> playUrl(@Url String url);

}
