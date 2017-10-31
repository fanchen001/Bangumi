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

/**
 * Created by fanchen on 2017/9/19.
 */
@RetrofitType(value = RetrofitSource.TUCAO_API)
public interface TucaoService {

    /**
     *
     * @return
     */
    @GET("{path}")
    @RetrofitType(RetrofitSource.TUCAO_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.TUCAO)
    Call<IHomeRoot> home(@Path("path")String path);

    /**
     *
     * @return
     */
    @GET("list/{pid}/index_{page}.html")
    @RetrofitType(RetrofitSource.TUCAO_API)
    @MethodType(value = MethodSource.MORE)
    @JsoupType(JsoupSource.TUCAO)
    Call<IBangumiMoreRoot> more(@Path("pid")String pid,@Path("page") Integer page);

    /**
     *
     * @param tid
     * @return
     */
    @GET("api_v2/rank.php?apikey=25tids8f1ew1821ed&type=json")
    @RetrofitType(RetrofitSource.TUCAO_API)
    @MethodType(value = MethodSource.RANKING)
    @JsoupType(JsoupSource.TUCAO)
    Call<IBangumiMoreRoot> ranking(@Query("tid")String tid,@Query("date")String date);

    /**
     *
     * @param q
     * @param page
     * @param order
     * @return
     */
    @GET("api_v2/search.php?pagesize=10&apikey=25tids8f1ew1821ed&type=json")
    @RetrofitType(RetrofitSource.TUCAO_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.TUCAO)
    Call<IBangumiMoreRoot> search(@Query("q")String q,@Query("page")Integer page,@Query("tid")String tid,@Query("order")String order);

    @GET("api_v2/search.php?pagesize=10&apikey=25tids8f1ew1821ed&type=json")
    @RetrofitType(RetrofitSource.TUCAO_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.TUCAO)
    Call<IBangumiMoreRoot> search(@Query("q")String q,@Query("page")Integer page,@Query("order")String order);


    @GET("index.html")
    @RetrofitType(RetrofitSource.TUCAO_API)
    @MethodType(value = MethodSource.TIME_LINE)
    @JsoupType(JsoupSource.TUCAO)
    Call<IBangumiTimeRoot> timeLine();

    @GET("api_v2/view.php?apikey=25tids8f1ew1821ed&type=json")
    @RetrofitType(RetrofitSource.TUCAO_API)
    @MethodType(value = MethodSource.DETAILS)
    @JsoupType(JsoupSource.TUCAO)
    Call<IVideoDetails> details(@Query("hid") String path);

    @GET("api_v2/playurl.php?apikey=25tids8f1ew1821ed")
    @RetrofitType(RetrofitSource.TUCAO_API)
    @MethodType(value = MethodSource.PLAYURL)
    @JsoupType(JsoupSource.TUCAO)
    @Headers({"Cookie: TUCAO_COOKIE=8c65UgQEBFQCCVFRBwJQAAABBwdVXA5WBgxZBVNnbCV5fA"})
    Call<IPlayUrls> playUrl(@Query("type") String type,@Query("vid") String vid);
}
