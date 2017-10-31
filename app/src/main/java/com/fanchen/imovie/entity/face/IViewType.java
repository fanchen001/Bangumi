package com.fanchen.imovie.entity.face;

/**
 * Created by fanchen on 2017/9/23.
 */
public interface IViewType {
    int TYPE_CATEGORY = 256;
    int TYPE_FOOTER = -128;
    int TYPE_HEADER = 128;
    int TYPE_NORMAL = 1;
    int TYPE_TITLE = 2;

    /**
     *
     * @return
     */
    int getViewType();
}
