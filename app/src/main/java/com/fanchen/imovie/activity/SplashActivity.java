package com.fanchen.imovie.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.entity.bmob.SplashScreen;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListener;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.FileUtil;
import com.fanchen.imovie.util.ImageUtil;
import com.fanchen.imovie.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * 首页闪屏界面
 * Created by fanchen on 2017/8/2.
 */
public class SplashActivity extends BaseActivity {
    // 加载完成
    private final static int LOAD_SUCCESS = 3;
    // 加载顶部图片
    private final static int LOAD_TOP_IMAGE = 2;

    private final static String APP_VERSION = "app_version";
    private final static String IMAGE_VERSION = "image_version";
    private final static String IMAGE_SPLASH = "splash.jpg";

    @InjectView(R.id.iv_splash_top)
    protected ImageView mImageView;

    private Bitmap mBitmap;

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    // 时间到了，加载主界面
                    loadMainUI();
                    break;
                case LOAD_TOP_IMAGE:
                    if (mBitmap != null) {
                        // 加载顶部图片
                        mImageView.setBackgroundDrawable(ImageUtil.bitmapToDrawable(mBitmap));
                    } else {
                        // 加载顶部图片
                        mBitmap = ImageUtil.readBitMap(SplashActivity.this, R.drawable.bg_start_top);
                        mImageView.setBackgroundDrawable(ImageUtil.bitmapToDrawable(mBitmap));
                    }
                    break;
                default:
            }
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        File headerDir = AppUtil.getExternalHeaderDir(this);
        if(headerDir != null && headerDir.exists()){
            File file = new File(headerDir.getAbsolutePath() + IMAGE_SPLASH);
            if (file.exists()) {
                mBitmap = ImageUtil.readBitMap(file.getAbsolutePath());
            }
            if(preferences.getBoolean("main_check", true)){
                new BmobQuery<SplashScreen>().findObjects(this,new SplashScreenListener(file,preferences));
            }
        }
        if (preferences.getBoolean("auto_download", true)) {
            //开启未完成任务自动下载
            Aria.download(appliction).resumeAllTask();
            AsyTaskQueue.newInstance().execute(taskListener);
        }
        // 延时1s加载top图片
        mHandler.sendEmptyMessageDelayed(LOAD_TOP_IMAGE, 1000);
        // 延时3s加载主界面
        mHandler.sendEmptyMessageDelayed(LOAD_SUCCESS, 3500);
    }

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }

    /**
     * 载入主页面,如果是第一次安装程序，则先加载欢迎界面
     */
    private void loadMainUI() {
        String versionName = AppUtil.getVersionName(getApplication());
        SharedPreferences pf = getPreferences(Context.MODE_PRIVATE);
        if (!pf.getString(APP_VERSION, "").equals(versionName)) {
            pf.edit().putString(APP_VERSION, versionName).commit();
            // 如果是第一次进入页面
            // 加载引导界面
            startActivity(MainActivity.class);
        } else {
            // 如果不是，加载主界面
            startActivity(MainActivity.class);
        }
        finish();
    }

    private AsyTaskListener<List<DownloadEntity>> taskListener = new AsyTaskListenerImpl<List<DownloadEntity>>() {

        @Override
        public List<DownloadEntity> onTaskBackground() {
            if (appliction == null) return null;
            List<DownloadEntity> list = new ArrayList<>();
            List<DownloadEntity> simpleTaskList = Aria.download(appliction).getSimpleTaskList();
            if(simpleTaskList != null){
                for (DownloadEntity entity : simpleTaskList) {
                    if (entity.getState() == IEntity.STATE_RUNNING || entity.getState() == IEntity.STATE_WAIT) {
                        list.add(entity);
                    }
                }
            }
            return list;
        }

        @Override
        public void onTaskSuccess(List<DownloadEntity> simpleTaskList) {
            if (simpleTaskList == null) return;
            for (DownloadEntity entity : simpleTaskList) {
                Aria.download(appliction).load(entity.getUrl()).start();
            }
        }

    };

    private static class SplashScreenListener extends FindListener<SplashScreen> {

        private File file;
        private SharedPreferences preferences;

        public SplashScreenListener(File file, SharedPreferences preferences) {
            this.file = file;
            this.preferences = preferences;
        }

        @Override
        public void onSuccess(List<SplashScreen> list) {
            if (list == null || list.size() == 0) return;
            long timeMillis = System.currentTimeMillis();
            int version = preferences.getInt(IMAGE_VERSION, 0);
            for (SplashScreen splashScreen : list) {
                if (splashScreen.getStartTime() < timeMillis && splashScreen.getEndTime() > timeMillis && version < splashScreen.getVersion()) {
                   FileUtil.downloadBackgroud(splashScreen.getScreenImage(),file);
                    preferences.edit().putInt(IMAGE_VERSION,splashScreen.getVersion()).commit();
                    return;
                }
            }
        }

        @Override
        public void onError(int i, String s) {
            LogUtil.e(SplashActivity.class,"更新封面图片失败");
        }

    }
}
