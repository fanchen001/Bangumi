package com.fanchen.imovie.view.pager;

/**
 *
 * @param <T>
 */
public interface IBanner<T> {

    int TYPE_WEB = 1;
    int TYPE_NATIVE = 2;

    String getTitle();

    String getCover();

    T getData();

    int getBannerType();

}
