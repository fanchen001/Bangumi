package com.fanchen.imovie.entity.face;


/**
 * 数据item
 * Created by fanchen on 2017/9/18.
 */
public interface IVideo extends IBaseVideo{

    /**
     * 最新更新
     * @return
     */
    String getLast();

    /**
     * 附加的显示信息
     * @return
     */
    String getExtras();

    /**
     *
     * @return
     */
    String getDanmaku();

    /**
     *
     * @return
     */
    int getDrawable();

    /**
     *
     * @return
     */
    boolean hasVideoDetails();

}
