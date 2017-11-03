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
import com.fanchen.imovie.entity.face.IVideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by fanchen on 2017/10/28.
 */
@RetrofitType(RetrofitSource.KMAO_API)
public interface KmaoService {

    /**
     *
     * @return
     */
    @GET("{path}/")
    @RetrofitType(RetrofitSource.KMAO_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.KMAO)
    Call<IHomeRoot> home(@Path("path")String path);

    /**
     *
     * @return
     */
    @GET("{path}/index-{page}.html")
    @RetrofitType(RetrofitSource.KMAO_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.KMAO)
    Call<IHomeRoot> home(@Path("path")String path,@Path("page")Integer page);

    /**
     *
     * @param keyword
     * @return
     */
    @GET("vod-search-wd-{keyword}-p-{page}.html")
    @RetrofitType(RetrofitSource.KMAO_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.KMAO)
    Call<IBangumiMoreRoot> search(@Path("page")Integer page,@Path("keyword")String keyword);

    /**
     *
     * @return
     */
    @GET("{path}/index-{page}.html")
    @RetrofitType(RetrofitSource.KMAO_API)
    @MethodType(value = MethodSource.MORE)
    @JsoupType(JsoupSource.KMAO)
    Call<IBangumiMoreRoot> more(@Path("path")String pid,@Path("page") Integer page);

    /**
     *
     * @param url
     * @return
     */
    @GET
    @RetrofitType(RetrofitSource.KMAO_API)
    @MethodType(value = MethodSource.DETAILS)
    @JsoupType(JsoupSource.KMAO)
    Call<IVideoDetails> details(@Url String url);

    /**
     *
     * @param url
     * @return
     */
    @GET
    @RetrofitType(RetrofitSource.KMAO_API)
    @MethodType(value = MethodSource.PLAYURL)
    @JsoupType(JsoupSource.KMAO)
    Call<IPlayUrls> playUrl(@Url String url);
}
