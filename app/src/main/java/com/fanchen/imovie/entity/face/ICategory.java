package com.fanchen.imovie.entity.face;

import android.os.Parcelable;

/**
 * 分类
 * Created by fanchen on 2017/9/17.
 */
public interface ICategory extends IViewType,Parcelable{
    /*

     */
     int getDrawable();

    /**
     *
     * @return
     */
     String getTitle();

    /**
     *
     * @return
     */
     String getDrawableUrl();

    /**
     *
     * @return
     */
     int getSource();

    /**
     *
     * @param d
     */
     void setDrawable(int d);

    /**
     *
     * @return
     */
     String getData();

}
