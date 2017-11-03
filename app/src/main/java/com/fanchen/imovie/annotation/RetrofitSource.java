package com.fanchen.imovie.annotation;

/**
 * Retrofit源类型
 * Created by fanchen on 2017/7/15.
 */
public enum RetrofitSource {

    /**
     * 搜索智能词语联想
     * 使用的是百度的一个API
     */
    BAIDU_API,

    /**
     * 搜索热词
     * 使用小马搜索的一个API
     */
    XIAOMA_API,

    /**
     * 应用游戏
     * 使用萌萌安卓的api
     */
    MOEAPK_API,

    /**
     * 电视直播，云播搜索等
     * 使用电影天堂的API
     */
    DYTT_API,

    /**
     * 具体数据需要使用JsoupResponseConverter
     * 来进行相对应的解析
     */
    TUCAO_API,

    /**
     *
     */
    S80_API,

    /**
     *
     */
    BUMIMI_API,

    /**
     *
     */
    JREN_API,

    /**
     *
     */
    ACG12_API,

    /**
     *
     */
    DM5_API,

    /**
     *
     */
    BILIPLUS_API,

    /**
     *
     */
    DIANXIUMEI_API,

    /**
     *
     */
    XIAOKANBA_API,

    /**
     *
     */
    XIAOBO_API,

    /**
     *
     */
    KMAO_API,

    /**
     *
     */
    A4DY_API
}
