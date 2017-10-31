package com.fanchen.imovie.entity.face;

/**
 * 使用SearchDialogFragment搜索
 * 的数据需要实现该接口
 * Created by fanchen on 2017/9/17.
 */
public interface ISearchWord extends IViewType{

    int TYPE_WORD = 1;
    int TYPE_VIDEO = 2;

    /**
     *
     * @return
     */
    int getType();

    /**
     *
     * @return
     */
    String getWord();

}
