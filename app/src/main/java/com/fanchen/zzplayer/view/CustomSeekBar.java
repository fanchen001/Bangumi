package com.fanchen.zzplayer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by fanchen on 2017/5/3.
 * 增加方法:是否可被拖动
 */
public class CustomSeekBar extends SeekBar {
    private boolean mSeekable = true;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setSeekable(boolean seekable) {
        this.mSeekable = seekable;
    }

    public boolean isSeekable() {
        return mSeekable;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return !mSeekable ? false : super.onTouchEvent(event);
    }
}
