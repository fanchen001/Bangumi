package com.fanchen.zzplayer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.VideoView;

import java.util.Map;

/**
 * Created by fanchen on 2017/4/28.
 */
public class ZZVideoView extends VideoView {

    private String mCurrentVideoPath;

    public ZZVideoView(Context context) {
        super(context);
        init(context);
    }

    public ZZVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZZVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ZZVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        //        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //        setBackgroundResource(R.drawable.surface_bg);
        //        setBackgroundColor(Color.BLUE);
        // 若activity主题为透明,则在加载过程中,videoView区域也会变成透明,体验不好
        // 但若是直接将VideoView挪到top层,则会遮盖当前区域的其他控件,所以还是直接指定背景色,然后在prepare
        // 回调中将背景色设为透明
        //        setZOrderOnTop(true);
    }

    @Override
    public void setVideoPath(String path) {
        super.setVideoPath(path);
        mCurrentVideoPath = path;
    }

    @Override
    public void setVideoURI(Uri uri) {
        super.setVideoURI(uri);
        mCurrentVideoPath = uri.getPath();
    }

    @Override
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        super.setVideoURI(uri, headers);
        mCurrentVideoPath = uri.getPath();
    }

    public String getCurrentVideoPath() {
        return mCurrentVideoPath;
    }
}
