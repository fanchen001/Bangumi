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

/**
 * 泰韩剧
 * Created by fanchen on 2017/12/23.
 */
@RetrofitType(RetrofitSource.TAIHAN_API)
public interface TaihanService {
    /**
     * @param path
     * @return
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.TAIHAN)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({
            "User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1",
            "Accept-Encoding:gzip,deflate,br",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
            "Accept-Language: zh-CN,zh;q=0.9"})
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     * @param page
     * @param keyword
     * @return
     */
    @GET("vod-search-name-{wd}-p-{page}.html")
    @JsoupType(JsoupSource.TAIHAN)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({
            "User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1",
            "Accept-Encoding:gzip,deflate,br",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
            "Accept-Language: zh-CN,zh;q=0.9"})
    Call<IBangumiMoreRoot> search(@Path("page") Integer page, @Path("wd") String keyword);

    /**
     * @param pid
     * @param page
     * @return
     */
    @GET("video/type/{path}------addtime-{page}.html")
    @JsoupType(JsoupSource.TAIHAN)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({
            "User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1",
            "Accept-Encoding:gzip,deflate,br",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
            "Accept-Language: zh-CN,zh;q=0.9"})
    Call<IBangumiMoreRoot> more(@Path("path") String pid, @Path("page") Integer page);

    /**
     * @param path
     * @return
     */
    @GET("taijula/{path}")
    @JsoupType(JsoupSource.TAIHAN)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({
            "User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1",
            "Accept-Encoding:gzip,deflate,br",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
            "Accept-Language: zh-CN,zh;q=0.9"})
    Call<IVideoDetails> details(@Path("path") String path);

    /**
     * @param path
     * @return
     */
    @GET("taijula/play/{path}")
    @JsoupType(JsoupSource.TAIHAN)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({
            "User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1",
            "Accept-Encoding:gzip,deflate,br",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3",
            "Accept-Language: zh-CN,zh;q=0.9"})
    Call<IPlayUrls> playUrl(@Path("path") String path);
}
