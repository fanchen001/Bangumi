package com.fanchen.imovie.entity.face;

import java.util.List;

/**
 * Created by fanchen on 2017/9/20.
 */
public interface IBangumiTimeRoot extends IRoot{

    /**
     *
     * @return
     */
    List<? extends IBangumiTimeTitle> getList();

    /**
     *
     * @return
     */
    List<? extends IViewType> getAdapterList();

    /**
     *
     * @return
     */
    int getPosition();
}
