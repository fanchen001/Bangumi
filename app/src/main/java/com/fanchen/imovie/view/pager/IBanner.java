package com.fanchen.imovie.view.pager;

/**
 *
 * @param <T>
 */
public interface IBanner<T> {

    int TYPE_WEB = 1;
    int TYPE_NATIVE = 2;

    /**
     *
     * @return
     */
    String getTitle();

    /**
     *
     * @return
     */
    String getCover();

    /**
     *
     * @return
     */
    T getData();

    /**
     *
     * @return
     */
    int getBannerType();

    /**
     *
     * @return
     */
    String getReferer();

    /**
     *
     * @return
     */
    boolean isAgent();
}
