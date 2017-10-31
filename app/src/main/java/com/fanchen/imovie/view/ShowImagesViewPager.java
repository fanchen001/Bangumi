package com.fanchen.imovie.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *为了解决photoview嵌套在部分父控件时闪退的bug，github上提供的解决方案
 * Created by fanchen on 2017/7/8.
 */
public class ShowImagesViewPager  extends ViewPager {
    public ShowImagesViewPager(Context context) {
        this(context,null);
    }

    public ShowImagesViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
