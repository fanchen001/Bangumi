package com.fanchen.imovie.retrofit.callback;

/**
 * 网络请求接口
 *
 * 包含开始请求，和请求结束回调
 * Created by fanchen on 2017/7/15.
 */
public interface RefreshCallback<T> extends RetrofitCallback<T>{
    /**
     * 网络请求开始
     *
     * @param enqueueKey
     */
    void onStart(int enqueueKey);

    /**
     * 请求结束
     *
     * @param enqueueKey
     */
    void onFinish(int enqueueKey);

    /**
     * 请求出错
     *
     * @param enqueueKey
     * @param throwable
     */
    void onFailure(int enqueueKey, String throwable);
}
