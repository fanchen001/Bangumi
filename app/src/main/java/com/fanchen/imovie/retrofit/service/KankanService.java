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
 * Created by fanchen on 2017/11/8.
 */
@RetrofitType(RetrofitSource.KANKANWU_API)
public interface KankanService {
    /**
     *
     * @param path
     * @return
     *
     *
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.KANKAN)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({"Connection: keep-alive" ,"User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",    "Upgrade-Insecure-Requests: 1","Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8","Accept-Encoding: gzip, deflate","Accept-Language: zh-CN,zh;q=0.9","Cookie: _ga=GA1.2.1472396023.1513846908; __cm_warden_uid=b6394612e0ac1c95a9176d3b9db3a1b5cookie; __cm_warden_upi=MTE4LjI0OS4yMDUuMQ%3D%3D; Hm_lvt_0409e9298c779b99a630b055ffe9d391=1513846908,1513907031,1514440558,1514865087; Hm_lpvt_0409e9298c779b99a630b055ffe9d391=1514865087"})
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param path
     * @param page
     * @return
     */
    @GET("{path}/index-{page}.html")
    @JsoupType(JsoupSource.KANKAN)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({"Connection: keep-alive" ,"User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",    "Upgrade-Insecure-Requests: 1","Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8","Accept-Encoding: gzip, deflate","Accept-Language: zh-CN,zh;q=0.9","Cookie: _ga=GA1.2.1472396023.1513846908; __cm_warden_uid=b6394612e0ac1c95a9176d3b9db3a1b5cookie; __cm_warden_upi=MTE4LjI0OS4yMDUuMQ%3D%3D; Hm_lvt_0409e9298c779b99a630b055ffe9d391=1513846908,1513907031,1514440558,1514865087; Hm_lpvt_0409e9298c779b99a630b055ffe9d391=1514865087"})
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    /**
     *
     * @param page
     * @param keyword
     * @return
     */
    @GET("vod-search-wd-{keyword}-p-{page}.html")
    @JsoupType(JsoupSource.KANKAN)
    @MethodType(value = MethodSource.SEARCH)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({"Connection: keep-alive" ,"User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",    "Upgrade-Insecure-Requests: 1","Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8","Accept-Encoding: gzip, deflate","Accept-Language: zh-CN,zh;q=0.9","Cookie: _ga=GA1.2.1472396023.1513846908; __cm_warden_uid=b6394612e0ac1c95a9176d3b9db3a1b5cookie; __cm_warden_upi=MTE4LjI0OS4yMDUuMQ%3D%3D; Hm_lvt_0409e9298c779b99a630b055ffe9d391=1513846908,1513907031,1514440558,1514865087; Hm_lpvt_0409e9298c779b99a630b055ffe9d391=1514865087"})
    Call<IBangumiMoreRoot> search(@Path("page") Integer page, @Path("keyword") String keyword);

    /**
     *
     * @param pid
     * @param page
     * @return
     */
    @GET("{path}/index-{page}.html")
    @JsoupType(JsoupSource.KANKAN)
    @MethodType(value = MethodSource.MORE)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({"Connection: keep-alive" ,"User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",    "Upgrade-Insecure-Requests: 1","Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8","Accept-Encoding: gzip, deflate","Accept-Language: zh-CN,zh;q=0.9","Cookie: _ga=GA1.2.1472396023.1513846908; __cm_warden_uid=b6394612e0ac1c95a9176d3b9db3a1b5cookie; __cm_warden_upi=MTE4LjI0OS4yMDUuMQ%3D%3D; Hm_lvt_0409e9298c779b99a630b055ffe9d391=1513846908,1513907031,1514440558,1514865087; Hm_lpvt_0409e9298c779b99a630b055ffe9d391=1514865087"})
    Call<IBangumiMoreRoot> more(@Path("path") String pid, @Path("page") Integer page);

    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.KANKAN)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({"Connection: keep-alive" ,"User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",    "Upgrade-Insecure-Requests: 1","Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8","Accept-Encoding: gzip, deflate","Accept-Language: zh-CN,zh;q=0.9","Cookie: _ga=GA1.2.1472396023.1513846908; __cm_warden_uid=b6394612e0ac1c95a9176d3b9db3a1b5cookie; __cm_warden_upi=MTE4LjI0OS4yMDUuMQ%3D%3D; Hm_lvt_0409e9298c779b99a630b055ffe9d391=1513846908,1513907031,1514440558,1514865087; Hm_lpvt_0409e9298c779b99a630b055ffe9d391=1514865087"})
    Call<IVideoDetails> details(@Url String url);



    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.KANKAN)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    @Headers({"Connection: keep-alive" ,"User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",    "Upgrade-Insecure-Requests: 1","Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8","Accept-Encoding: gzip, deflate","Accept-Language: zh-CN,zh;q=0.9","Cookie: _ga=GA1.2.1472396023.1513846908; __cm_warden_uid=b6394612e0ac1c95a9176d3b9db3a1b5cookie; __cm_warden_upi=MTE4LjI0OS4yMDUuMQ%3D%3D; Hm_lvt_0409e9298c779b99a630b055ffe9d391=1513846908,1513907031,1514440558,1514865087; Hm_lpvt_0409e9298c779b99a630b055ffe9d391=1514865087"})
    Call<IPlayUrls> playUrl(@Url String url);
}
