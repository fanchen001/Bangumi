package com.fanchen.imovie.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by fanchen on 2017/7/16.
 */
public class NestedCoordinatorLayout extends CoordinatorLayout {
    public NestedCoordinatorLayout(Context context) {
        super(context);
    }

    public NestedCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!isEnabled())return true;
        return super.onTouchEvent(ev);
    }
}
