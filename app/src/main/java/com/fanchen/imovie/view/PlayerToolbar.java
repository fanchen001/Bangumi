package com.fanchen.imovie.view;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by fanchen on 2017/7/7.
 */
public class PlayerToolbar extends Toolbar {

    private boolean fristMeasure = true;

    public PlayerToolbar(Context context) {
        super(context);
    }

    public PlayerToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getHeight();
        //4.4以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(height > 0 && fristMeasure) {
                fristMeasure = false;
                int statusBarHeight1 = -1;
                //获取status_bar_height资源的ID
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    //根据资源ID获取响应的尺寸值
                    statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
                }
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height += statusBarHeight1 ;
                setLayoutParams(layoutParams);
                setPadding(0, statusBarHeight1, 0, 0);
            }
        }
    }
}
