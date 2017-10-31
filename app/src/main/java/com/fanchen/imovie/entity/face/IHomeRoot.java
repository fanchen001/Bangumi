package com.fanchen.imovie.entity.face;

import java.util.List;

/**
 * 主页数据根节点
 * Created by fanchen on 2017/9/18.
 */
public interface IHomeRoot extends IRoot{

    /**
     * 转换成BaseAdapter所需的填充数据
     * @return
     */
    List<? extends IViewType> getAdapterResult();

}
