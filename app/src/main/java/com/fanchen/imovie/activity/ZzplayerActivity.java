package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.zzplayer.controller.IPlayerImpl;
import com.fanchen.zzplayer.util.OrientationUtil;
import com.fanchen.zzplayer.view.VideoPlayer;

import butterknife.InjectView;


/**
 * Zzplayer  系统自带videoview播放器
 * Created by fanchen on 2017/10/26.
 */
public class ZzplayerActivity extends BaseActivity {

    public static final String URL = "url";
    public static final String TITLE = "title";

    @InjectView(R.id.video_main)
    protected VideoPlayer mVp;

    public static void startActivity(Context context,String title,String url){
        Intent intent = new Intent(context,ZzplayerActivity.class);
        intent.putExtra(URL,url);
        intent.putExtra(TITLE,title);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_zzplayer;
    }

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        OrientationUtil.forceOrientation(this,OrientationUtil.HORIZONTAL);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mVp.setPlayerController(playerImpl);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mVp.setTitle(getIntent().getStringExtra(TITLE));
        mVp.loadAndStartVideo(this, getIntent().getStringExtra(URL));
        //设置控制栏播放/暂停/全屏/退出全屏按钮图标
        mVp.setIconPlay(R.drawable.play);
        mVp.setIconPause(R.drawable.pause);
        mVp.setIconExpand(R.drawable.expand);
        mVp.setIconShrink(R.drawable.shrink);
        // 自定义加载框图标
        mVp.setIconLoading(R.drawable.loading_red);
        // 设置进度条样式
        mVp.setProgressThumbDrawable(R.drawable.progress_thumb);
        mVp.setProgressLayerDrawables(R.drawable.biz_video_progressbar);//自定义的layer-list
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVp != null) {
            mVp.updateActivityOrientation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVp != null) {
            mVp.onHostResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVp != null) {
            mVp.onHostPause();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVp != null) {
            mVp.onHostDestroy();
        }
    }

    private IPlayerImpl playerImpl = new IPlayerImpl() {

        @Override
        public void onNetWorkError() {
            showToast(null);
        }

        @Override
        public void onBack() {
            // 全屏播放时,单击左上角返回箭头,先回到竖屏状态,再关闭
            // 这里功能最好跟onBackPressed()操作一致
            int orientation = OrientationUtil.getOrientation(ZzplayerActivity.this);
            if (orientation == OrientationUtil.HORIZONTAL) {
                OrientationUtil.forceOrientation(ZzplayerActivity.this, OrientationUtil.VERTICAL);
            } else {
                finish();
            }
        }

        @Override
        public void onError() {
            showToast("播放器发生异常");
        }
    };
}
