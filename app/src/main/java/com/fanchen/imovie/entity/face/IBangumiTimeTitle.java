package com.fanchen.imovie.entity.face;

import java.util.List;

/**
 * Created by fanchen on 2017/9/20.
 */
public interface IBangumiTimeTitle extends IViewType{
    /**
     *
     * @return
     */
    String getTitle();

    /**
     *
     * @return
     */
    List<? extends IBaseVideo> getList();

    /**
     *
     * @return
     */
    boolean isNow();

    /**
     *
     * @return
     */
    int getDrawable();
}
