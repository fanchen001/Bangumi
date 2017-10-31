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
 * Created by fanchen on 2017/9/24.
 */
@RetrofitType(value = RetrofitSource.BUMIMI_API)
public interface BumimiService {
    /**
     *
     * @return
     */
    @GET("{path}")
    @RetrofitType(RetrofitSource.BUMIMI_API)
    @MethodType(value = MethodSource.HOME)
    @JsoupType(JsoupSource.BUMIMI)
    Call<IHomeRoot> home(@Path("path")String path);

    /**
     *
     * @return
     */
    @GET("{pid}/index-{page}.html")
    @RetrofitType(RetrofitSource.BUMIMI_API)
    @MethodType(value = MethodSource.MORE)
    @JsoupType(JsoupSource.BUMIMI)
    Call<IBangumiMoreRoot> more(@Path("pid")String pid,@Path("page") Integer page);

    @GET("search/{word}-{page}.html")
    @RetrofitType(RetrofitSource.BUMIMI_API)
    @MethodType(value = MethodSource.SEARCH)
    @JsoupType(JsoupSource.BUMIMI)
    @Headers({"Connection: keep-alive",
            "User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
            "Upgrade-Insecure-Requests: 1",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
            "Accept-Encoding: deflate",
            "Accept-Language: zh-CN,zh;q=0.8",
            "Cookie: __cfduid=d81cc73a77d49a80deb38757a8b52a2a61499928221; UM_distinctid=15d3b14ec464d9-0125d6f54bd985-63151074-144000-15d3b14ec4749f; webGlHysiV2=3310338605; a4319_times=1; CNZZDATA1261825573=211215625-1493350587-null%7C1493350587; Hm_lvt_b23539ba1c561d1a4d571b44d30ce013=1500353504; Hm_lvt_b172c24deb128a9289271b441dae4605=1500353532; Hm_lvt_bcf56c077779b9580c25470691661f73=1500370143; CNZZDATA1260882964=2036780354-1499935927-http%253A%252F%252Fwww.bumimi.com%252F%7C1500367982; CNZZDATA1260882966=1963358055-1499932813-http%253A%252F%252Fwww.bumimi.com%252F%7C1500370284; Hm_lvt_996309ef81048737db497b7f6f7277a5=1502459785; Hm_lvt_583c9db5fe325dc4ea7b201c8253907d=1502459795; CNZZDATA1261792595=98601123-1502457231-%7C1502457231; Hm_lvt_c549a16e29fd9a94802b1243c98494d9=1505884513,1506214945,1506214951,1506214956; Hm_lpvt_c549a16e29fd9a94802b1243c98494d9=1506214956; Hm_lvt_eb1cc0663ec2a6b45db53328696b5592=1505884582,1506214969; Hm_lpvt_eb1cc0663ec2a6b45db53328696b5592=1506297377"
    })
    Call<IBangumiMoreRoot> search(@Path("page")Integer page,@Path("word")String word);

    @GET("video/{path}")
    @RetrofitType(RetrofitSource.BUMIMI_API)
    @MethodType(value = MethodSource.DETAILS)
    @JsoupType(JsoupSource.BUMIMI)
    Call<IVideoDetails> details(@Path("path") String path);

    @GET("play/{path}")
    @RetrofitType(RetrofitSource.BUMIMI_API)
    @MethodType(value = MethodSource.PLAYURL)
    @JsoupType(JsoupSource.BUMIMI)
    Call<IPlayUrls> playUrl(@Path("path") String path);
}
