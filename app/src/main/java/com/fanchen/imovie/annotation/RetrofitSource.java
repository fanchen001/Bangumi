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
     * 80s手机电影
     */
    S80_API,

    /**
     * 布米米
     */
    BUMIMI_API,

    /**
     * 吉人动漫
     */
    JREN_API,

    /**
     * ACG小队
     */
    ACG12_API,

    /**
     * 五弹幕
     */
    DM5_API,

    /**
     * biliplug工具
     */
    BILIPLUS_API,

    /**
     * 169秀
     */
    DIANXIUMEI_API,

    /**
     * 小看吧
     */
    XIAOKANBA_API,

    /**
     * 小播，磁力搜索
     */
    XIAOBO_API,

    /**
     * 4K电影
     */
    KMAO_API,

    /**
     * A4YY
     */
    A4DY_API,

    /**
     * 巴巴鱼
     */
    BABAYU_API,

    /**
     * 看看屋
     */
    KANKANWU_API,

    /**
     * K8电影
     * http://m.k8yy.com/
     */
    K8DY_API,

    /**
     * 520萝莉
     * http://m.520ll.com/
     */
    LL520_API,

    /**
     * 哈尼哈尼
     * http://m.halihali.tv/
     */
    HALIHALI_API,

    /**
     * 爱看番
     * http://www.ikanfan.com/
     */
    IKANFAN_API,

    /**
     * 秘密影院
     * http://m.mimiyy.com/
     */
    MMYY_API
}
