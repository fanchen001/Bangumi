package com.fanchen.imovie.entity.face;

import android.os.Parcelable;

/**
 * Created by fanchen on 2017/9/18.
 */
public interface IBaseVideo extends IViewType,Parcelable{

    int VIDEO_JREN = 1;
    int VIDEO_BUMIMI = 2;
    int VIDEO_S80 = 3;
    int VIDEO_TUCAO = 4;
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
}
