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
     * @return
     */
    @GET("videos_type_{type}_sort_time_gener_全部_area_全部_year_全部_p_{page}.html")
    @RetrofitType(RetrofitSource.XIAOKANBA_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.XIAOKANBA)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IHomeRoot> home(@Path("type")String type,@Path("page") Integer page);

    /**
     *
     * @return
     */
    @GET("{path}")
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    @RetrofitType(RetrofitSource.XIAOKANBA_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.XIAOKANBA)
    Call<IHomeRoot> home(@Path("path")String path);

    @GET("search_key_{key}_sort_time_p_{p}.html")
    @RetrofitType(RetrofitSource.XIAOKANBA_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.XIAOKANBA)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IBangumiMoreRoot> search(@Path("p")Integer page,@Path("key")String word);

    @GET("{path}")
    @RetrofitType(RetrofitSource.XIAOKANBA_API)
    @MethodType(value = MethodSource.DETAILS)
    @JsoupType(JsoupSource.XIAOKANBA)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IVideoDetails> details(@Path("path") String path);

    @GET("{path}")
    @RetrofitType(RetrofitSource.XIAOKANBA_API)
    @MethodType(value = MethodSource.PLAYURL)
    @JsoupType(JsoupSource.XIAOKANBA)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IPlayUrls> playUrl(@Path("path") String path);

    /**
     *
     * @return
     */
    @GET("videos_type_{pid}_sort_time_gener_全部_area_全部_year_全部_p_{page}.html")
    @RetrofitType(RetrofitSource.XIAOKANBA_API)
    @MethodType(value = MethodSource.MORE)
    @JsoupType(JsoupSource.XIAOKANBA)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IBangumiMoreRoot> more(@Path("pid")String pid,@Path("page") Integer page);

}
