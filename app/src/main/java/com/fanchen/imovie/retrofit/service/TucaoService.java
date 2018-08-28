package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.annotation.JsoupSource;
import com.fanchen.imovie.annotation.JsoupType;
import com.fanchen.imovie.annotation.MethodSource;
import com.fanchen.imovie.annotation.MethodType;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IBangumiTimeRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by fanchen on 2017/9/19.
 */
@RetrofitType(RetrofitSource.TUCAO_API)
public interface TucaoService {

    /**
     * @param path
     * @return
     */
    @GET("{path}")
    @JsoupType(JsoupSource.TUCAO)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     * @param pid
     * @param page
     * @return
     */
    @GET("list/{pid}/index_{page}.html")
    @JsoupType(JsoupSource.TUCAO)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> more(@Path("pid") String pid, @Path("page") Integer page);

    /**
     * @param tid
     * @param date
     * @return
     */
    @GET("api_v2/rank.php?apikey=25tids8f1ew1821ed&type=json")
    @JsoupType(JsoupSource.TUCAO)
    @MethodType(value = MethodSource.RANKING)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> ranking(@Query("tid") String tid, @Query("date") String date);

    /**
     * @param q
     * @param page
     * @param tid
     * @param order
     * @return
     */
    @GET("api_v2/search.php?pagesize=10&apikey=25tids8f1ew1821ed&type=json")
    @JsoupType(JsoupSource.TUCAO)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Query("q") String q, @Query("page") Integer page, @Query("tid") String tid, @Query("order") String order);

    /**
     * @param q
     * @param page
     * @param order
     * @return
     */
    @GET("api_v2/search.php?pagesize=10&apikey=25tids8f1ew1821ed&type=json")
    @JsoupType(JsoupSource.TUCAO)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Query("q") String q, @Query("page") Integer page, @Query("order") String order);


    /**
     * @return
     */
    @GET("index.html")
    @JsoupType(JsoupSource.TUCAO)
    @MethodType(value = MethodSource.TIME_LINE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiTimeRoot> timeLine();

    /**
     * @param path
     * @return
     */
    @GET("api_v2/view.php?apikey=25tids8f1ew1821ed&type=json")
    @JsoupType(JsoupSource.TUCAO)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Query("hid") String path);

    /**
     *
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.TUCAO)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({"Cookie: TUCAO_COOKIE=8c65UgQEBFQCCVFRBwJQAAABBwdVXA5WBgxZBVNnbCV5fA"})
    Call<IPlayUrls> playUrl(@Url String url);
}
