package com.fanchen.imovie.jsoup;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;

/**
 * Created by fanchen on 2017/10/28.
 */
public interface IVideoMoreParser extends IVideoParser{

    /**
     *
     * @param html
     * @return
     */
    IBangumiMoreRoot more(String html);
}
