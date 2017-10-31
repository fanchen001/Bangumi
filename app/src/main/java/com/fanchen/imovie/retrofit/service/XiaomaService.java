package com.fanchen.imovie.retrofit.service;

import com.fanchen.imovie.entity.xiaoma.XiaomaWordIndex;
import com.fanchen.imovie.annotation.RetrofitSource;
import com.fanchen.imovie.annotation.RetrofitType;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by fanchen on 2017/7/15.
 */
@RetrofitType(value = RetrofitSource.XIAOMA_API)
public interface XiaomaService {

    /**
     *
     * @param devId
     * @return
     */
    @GET("nav/sys/indexconfig?version_code=2&api_version=2")
    @RetrofitType(value = RetrofitSource.XIAOMA_API)
    Call<XiaomaWordIndex> loadHotword(@Query("dev_id") String devId);

}
