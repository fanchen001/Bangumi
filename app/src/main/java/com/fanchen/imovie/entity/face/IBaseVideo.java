package com.fanchen.imovie.entity.face;

import android.os.Parcelable;

/**
 * IBaseVideo
 * Created by fanchen on 2017/9/18.
 */
public interface IBaseVideo extends IViewType,Parcelable{

    int SOURCE_PLAY = -100;

    /**
     *标题
     * @return
     */
    String getTitle();

    /**
     * 图片
     * @return
     */
    String getCover();

    /**
     * id
     * @return
     */
    String getId();

    /**
     * 请求url
     * @return
     */
    String getUrl();

    /**
     * 源，该实体数据来自哪个资源站
     * @return
     */
    int getSource();

    /**
     *
     * @return
     */
    String getServiceClass();

    /**
     *
     * @return
     */
    boolean isAgent();

    /**
     *
     * @return
     */
    String getCoverReferer();
}
