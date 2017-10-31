package com.fanchen.imovie.entity.face;

import java.util.List;

/**
 * 主页数据
 * Created by fanchen on 2017/9/18.
 */
public interface IBangumiTitle extends IViewType{
    /**
     * 是否有加载更多按钮
     * @return
     */
    boolean hasMore();

    /**
     * 加载更多Url
     * @return
     */
    String getFormatUrl();

    /**
     * 标题
     * @return
     */
    String getTitle();

    /**
     * 图标
     * @return
     */
    int getDrawable();

    /**
     * 数据
     * @return
     */
    List<? extends IVideo> getList();

    /**
     *
     * @return
     */
    String getServiceClassName();

    /**
     *
     * @return
     */
    String getId();
}
