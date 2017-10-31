package com.fanchen.imovie.entity.face;

import java.util.Map;

/**
 * Created by fanchen on 2017/9/28.
 */
public interface IPlayUrls extends IRoot{

    /**
     *
     * @return
     */
    Map<String,String> getUrls();

    /**
     *
     * @return
     */
    int getPlayType();
}
