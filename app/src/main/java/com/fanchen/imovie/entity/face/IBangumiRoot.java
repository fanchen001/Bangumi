package com.fanchen.imovie.entity.face;

import android.os.Parcelable;

import com.fanchen.imovie.view.pager.IBanner;

import java.util.List;

/**
 * Created by fanchen on 2017/9/23.
 */
public interface IBangumiRoot extends IHomeRoot,Parcelable{
    /**
     * 横幅广告
     * @return
     */
    List<? extends IVideoBanner<? extends IBanner>> getHomeBanner();

    /**
     * 下方数据
     * @return
     */
    List<? extends IBangumiTitle> getResult();

}
