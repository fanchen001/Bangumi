package com.fanchen.imovie.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retrofit源
 *
 * Created by fanchen on 2017/7/15.
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetrofitType {

    /**
     * api源
     * @return
     */
    RetrofitSource value() default RetrofitSource.BAIDU_API;

    /**
     *请求数据是否是json数据
     * @return
     */
    boolean isJsonRequest() default false;

    /**
     * 响应是否需要json解析
     * @return
     */
    boolean isJsonResponse() default false;

    /**
     * 响应是否需要jsoup解析
     * @return
     */
    JsoupSource isJsoupResponse() default JsoupSource.TYPE_VIDEO;

    /**
     * 响应是否需要百度解析
     * @return
     */
    boolean isBaiduResponse() default false;
}
