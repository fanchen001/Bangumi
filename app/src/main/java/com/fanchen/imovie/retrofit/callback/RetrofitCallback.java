package com.fanchen.imovie.retrofit.callback;

/**
 * 网络请求接口
 * Created by fanchen on 2017/7/15.
 */
public interface RetrofitCallback<T> {
    /**
     * 请求成功
     *
     * @param enqueueKey
     * @param response
     */
    void onSuccess(int enqueueKey, T response);

}
