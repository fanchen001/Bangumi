package com.fanchen.imovie.entity.face;

import com.fanchen.imovie.view.pager.IBanner;

/**
 * 横幅banner
 * Created by fanchen on 2017/9/18.
 */
public interface IVideoBanner<T> extends IBanner<T>{

    /**
     * 源，该实体数据来自哪个资源站
     * @return
     */
    int getSource();

    /**
     *
     * @return
     */
    String getUrl();

    /**
     *
     * @return
     */
    String getId();

    /**
     * ServiceClass
     * @return
     */
    String getServiceClass();

}
