package com.fanchen.imovie.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fanchen on 2017/7/17.
 */
public class EnabledScrollView extends NestedScrollView {

    private View paddingView;

    public EnabledScrollView(Context context) {
        super(context);
    }

    public EnabledScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EnabledScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        super.setNestedScrollingEnabled(enabled);
        int childCount = getChildCount();
        if (childCount > 0) {
            View childAt = getChildAt(0);
            if (childAt instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) childAt;
                int groupChildCount = viewGroup.getChildCount();
                if (groupChildCount > 0) {
                    if(!enabled){
                        View lastView = viewGroup.getChildAt(groupChildCount - 1);
                        int h = (int) (lastView.getMeasuredHeight() / 1.3);
                        if(paddingView == null){
                            paddingView = new View(getContext());
                        }
                        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, h);
                        if(paddingView.getParent() != null){
                            ((ViewGroup)paddingView.getParent() ).removeView(paddingView);
                        }
                        viewGroup.addView(paddingView,params);
                    }else if(paddingView != null){
                        viewGroup.removeView(paddingView);
                    }
                }
            }
        }
    }


}
