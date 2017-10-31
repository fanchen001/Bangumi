package com.fanchen.imovie.entity.face;

import java.util.List;

/**
 * Created by fanchen on 2017/9/19.
 */
public interface IBangumiMoreRoot extends  IRoot{

    /**
     *
     * @return
     */
    List<? extends IVideo> getList();

}
