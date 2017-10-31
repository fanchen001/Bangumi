package com.fanchen.imovie.jsoup;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IBangumiTimeRoot;

/**
 * Created by fanchen on 2017/9/18.
 */
public interface IBangumiParser extends IVideoMoreParser {

    /**
     *
     * @param html
     * @return
     */
    IBangumiMoreRoot ranking(String html);


    /**
     *
     * @param html
     * @return
     */
    IBangumiTimeRoot timeLine(String html);

}
