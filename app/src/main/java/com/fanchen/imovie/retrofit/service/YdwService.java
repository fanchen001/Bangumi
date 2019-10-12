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
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

@RetrofitType(RetrofitSource.YDW_API)
public interface YdwService {

    /**
     *
     * @param path
     * @return
     */
    @GET("{path}/")
    @JsoupType(JsoupSource.YDW)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path);

    /**
     *
     * @param path
     * @param page
     * @return
     */
    @GET("list/{path}-0-0-0-0-0-{page}.html")
    @JsoupType(JsoupSource.YDW)
    @MethodType(value = MethodSource.HOME)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IHomeRoot> home(@Path("path") String path, @Path("page") Integer page);

    /**
     *
     * @param page
     * @param keyword
     * @return
     */
    @POST("e/search/index.php")
    @JsoupType(JsoupSource.YDW)
    @MethodType(value = MethodSource.SEARCH)
    @FormUrlEncoded
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IBangumiMoreRoot> search(@Field("tempid") Integer page, @Field("keyboard") String keyword);

    /**
     * @param id
     * @return
     */
    @GET("html/{id}.html")
    @JsoupType(JsoupSource.YDW)
    @MethodType(value = MethodSource.DETAILS)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IVideoDetails> details(@Path("id") String id);

    /**
     * @param url
     * @return
     */
    @GET
    @JsoupType(JsoupSource.YDW)
    @MethodType(value = MethodSource.PLAYURL)
    @RetrofitType(isJsoupResponse = JsoupSource.TYPE_VIDEO)
    Call<IPlayUrls> playUrl(@Url String url);

}
