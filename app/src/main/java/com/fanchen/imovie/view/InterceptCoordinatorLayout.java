package com.fanchen.imovie.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * InterceptCoordinatorLayout
 * fix bug  java.lang.ArrayIndexOutOfBoundsException
 *     length=2; index=2
 *     android.support.v4.widget.ViewDragHelper.saveLastMotion(Unknown Source:21)
 * Created by fanchen on 2018/11/6.
 */
public class InterceptCoordinatorLayout extends CoordinatorLayout {

    public InterceptCoordinatorLayout(Context context) {
        super(context);
    }

    public InterceptCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
