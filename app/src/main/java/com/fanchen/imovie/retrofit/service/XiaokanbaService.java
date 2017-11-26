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
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by fanchen on 2017/10/16.
 */
@RetrofitType(RetrofitSource.XIAOKANBA_API)
public interface XiaokanbaService {

    /**
     *
     * @param type
     * @param page
     * @return
     */
    @GET("videos_type_{type}_sort_time_gener_全部_area_全部_year_全部_p_{page}.html")
    @JsoupType(JsoupSource.XIAOKANBA)
    @RetrofitType(isJsoupResponse = true)
    @MethodType(value = MethodSource.HOME)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IHomeRoot> home(@Path("type") String type, @Path("page") Integer page);

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}")
    @JsoupType(JsoupSource.XIAOKANBA)
    @RetrofitType(isJsoupResponse = true)
    @MethodType(value = MethodSource.HOME)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param page
     * @param word
     * @return
     */
    @GET("search_key_{key}_sort_time_p_{p}.html")
    @JsoupType(JsoupSource.XIAOKANBA)
    @RetrofitType(isJsoupResponse = true)
    @MethodType(value = MethodSource.SEARCH)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IBangumiMoreRoot> search(@Path("p") Integer page, @Path("key") String word);

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}")
    @JsoupType(JsoupSource.XIAOKANBA)
    @RetrofitType(isJsoupResponse = true)
    @MethodType(value = MethodSource.DETAILS)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IVideoDetails> details(@Path("path") String path);

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}")
    @JsoupType(JsoupSource.XIAOKANBA)
    @RetrofitType(isJsoupResponse = true)
    @MethodType(value = MethodSource.PLAYURL)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IPlayUrls> playUrl(@Path("path") String path);

    /**
     * @return
     */
    @GET("videos_type_{pid}_sort_time_gener_全部_area_全部_year_全部_p_{page}.html")
    @JsoupType(JsoupSource.XIAOKANBA)
    @RetrofitType(isJsoupResponse = true)
    @MethodType(value = MethodSource.MORE)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IBangumiMoreRoot> more(@Path("pid") String pid, @Path("page") Integer page);

}
