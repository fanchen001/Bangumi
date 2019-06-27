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
import retrofit2.http.Url;

/**
 * ZzyoService
 * Created by fanchen on 2018/11/15.
 */
@RetrofitType(RetrofitSource.ZZYO_API)
public interface ZzyoService {
    /**
     *
     * @param type
     * @param page
     * @return
     */
    @GET("{type}/index{page}.html")
    @JsoupType(JsoupSource.ZZYO)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.HOME)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IHomeRoot> home(@Path("type") String type, @Path("page") Integer page);

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.ZZYO)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.HOME)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param page
     * @param word
     * @return
     */
    @GET("vod-search-wd-{key}-p-{p}.html")
    @JsoupType(JsoupSource.ZZYO)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.SEARCH)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IBangumiMoreRoot> search(@Path("p") Integer page, @Path("key") String word);

    /**
     *
     * @param pid
     * @param page
     * @return
     */
    @GET("{pid}/index{page}.html")
    @JsoupType(JsoupSource.ZZYO)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.MORE)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IBangumiMoreRoot> more(@Path("pid") String pid, @Path("page") Integer page);

    /**
     *
     * @param path
     * @return
     */
    @GET
    @JsoupType(JsoupSource.ZZYO)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.DETAILS)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IVideoDetails> details(@Url String path);

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}")
    @JsoupType(JsoupSource.ZZYO)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @MethodType(value = MethodSource.PLAYURL)
    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
    Call<IPlayUrls> playUrl(@Path("path") String path);

//    /**
//     * @return
//     */
//    @GET("vod-list-id-{pid}-pg-{page}-order--by-score-class--year--letter--area--lang-.html")
//    @JsoupType(JsoupSource.ZZYO)
//    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
//    @MethodType(value = MethodSource.MORE)
//    @Headers({"User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"})
//    Call<IBangumiMoreRoot> more(@Path("pid") String pid, @Path("page") Integer page);
}
