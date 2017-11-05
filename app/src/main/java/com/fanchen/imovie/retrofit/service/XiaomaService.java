package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.entity.xiaoma.XiaomaIndex;
import com.fanchen.imovie.entity.xiaoma.XiaomaSearchResult;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;
import com.fanchen.imovie.entity.xiaoma.XiaomaWordResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by fanchen on 2017/7/15.
 */
@RetrofitType(value = RetrofitSource.XIAOMA_API)
public interface XiaomaService {

    /**
     * @param devId
     * @return
     */
    @GET("nav/sys/indexconfig?version_code=2&api_version=2")
    @RetrofitType(value = RetrofitSource.XIAOMA_API)
    Call<XiaomaIndex<XiaomaWordResult>> loadHotword(@Query("dev_id") String devId);

    /**
     * @param devId
     * @return
     */
    @GET("nav/search/list?version_code=21&api_version=2&start=0")
    @RetrofitType(value = RetrofitSource.XIAOMA_API)
    Call<XiaomaIndex<XiaomaSearchResult>> search(@Query("q") String word, @Query("dev_id") String devId);
}
