package com.fanchen.imovie.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dialog.PermissionDialog;
import com.fanchen.imovie.entity.JsonSerialize;
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
    private SharedPreferences preferences;

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
                    if (mBitmap != null && mImageView != null) {
                        // 加载顶部图片
                        mImageView.setBackgroundDrawable(ImageUtil.bitmapToDrawable(mBitmap));
                    } else if (mImageView != null) {
                        // 加载顶部图片
                        mBitmap = ImageUtil.readBitMap(SplashActivity.this, R.drawable.bg_start_top);
                        if (mBitmap == null) return;
                        Drawable drawable = ImageUtil.bitmapToDrawable(mBitmap);
                        if (drawable == null) return;
                        mImageView.setBackgroundDrawable(drawable);
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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String versionName = AppUtil.getVersionName(this);
        if (preferences != null && !preferences.getString("app_version", "").equals(versionName)) {
            preferences.edit().putString("app_version", versionName).apply();
            AsyTaskQueue.newInstance().execute(databaseListener);
        }
        File headerDir = AppUtil.getExternalHeaderDir(this);
        if (headerDir != null && headerDir.exists()) {
            File file = new File(headerDir.getAbsolutePath() + IMAGE_SPLASH);
            if (file.exists()) {
                mBitmap = ImageUtil.readBitMap(file.getAbsolutePath());
            }
            if (preferences != null && preferences.getBoolean("main_check", true)) {
                new BmobQuery<SplashScreen>().findObjects(this, new SplashScreenListener(file, preferences));
            }
        }
        if (preferences != null && preferences.getBoolean("auto_download", true)) {
            //开启未完成任务自动下载
            getDownloadReceiver().resumeAllTask();
            AsyTaskQueue.newInstance().execute(taskListener);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            String[] checkPermission = checkPermission();
            if (checkPermission == null || checkPermission.length == 0) {
                // 延时1s加载top图片
                mHandler.sendEmptyMessageDelayed(LOAD_TOP_IMAGE, 1000);
                // 延时3s加载主界面
                mHandler.sendEmptyMessageDelayed(LOAD_SUCCESS, 3500);
            } else {
                if (!isFinishing()) {
                    new PermissionDialog(this).setOnClickListener(clickListener).show();
                }
            }
        } else {
            // 延时1s加载top图片
            mHandler.sendEmptyMessageDelayed(LOAD_TOP_IMAGE, 1000);
            // 延时3s加载主界面
            mHandler.sendEmptyMessageDelayed(LOAD_SUCCESS, 3500);
        }
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
        if (preferences != null && !preferences.getString(APP_VERSION, "").equals(versionName)) {
            preferences.edit().putString(APP_VERSION, versionName).apply();
            // 如果是第一次进入页面加载引导界面
            MainActivity.startActivity(this);
        } else {
            // 如果不是，加载主界面
            MainActivity.startActivity(this);
        }
        finish();
    }

    /**
     * @return
     */
    private String[] checkPermission() {
        ArrayList<String> localArrayList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != 0)
            localArrayList.add(Manifest.permission.READ_PHONE_STATE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0)
            localArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != 0)
//            localArrayList.add(Manifest.permission.CAMERA);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 0)
//            localArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (localArrayList.size() > 0) {
            String[] arrayOfString = new String[localArrayList.size()];
            localArrayList.toArray(arrayOfString);
            return arrayOfString;
        }
        return null;
    }

    /**
     * @param paramArrayOfInt
     * @return
     */
    private boolean checkPermissionsResult(int[] paramArrayOfInt) {
        if (paramArrayOfInt == null || paramArrayOfInt.length == 0) return true;
        int j = paramArrayOfInt.length;
        int i = 0;
        while (i < j) {
            if (paramArrayOfInt[i] == -1)
                return false;
            i += 1;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt) {
        super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
        if ((paramInt == 1024) && checkPermissionsResult(paramArrayOfInt)) {
            loadMainUI();
            return;
        }
        showToast("应用缺少必要的权限！请点击\"权限\"，打开所需要的所有权限。");
        try {
            Intent mIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(mIntent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finish();
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String[] permission = checkPermission();
            if (permission != null && permission.length > 0) {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(permission, 1024);
                }
            }
        }

    };

    private AsyTaskListener<Void> databaseListener = new AsyTaskListenerImpl<Void>() {

        @Override
        public Void onTaskBackground() {
            LogUtil.e("AsyTaskListener", "需要删除之前的缓存数据");
            long l = System.currentTimeMillis();
            getLiteOrm().delete(JsonSerialize.class);
            LogUtil.e("AsyTaskListener", "删除成功,耗时 => " + (System.currentTimeMillis() - l));
            return super.onTaskBackground();
        }

    };

    private AsyTaskListener<List<DownloadEntity>> taskListener = new AsyTaskListenerImpl<List<DownloadEntity>>() {

        @Override
        public List<DownloadEntity> onTaskBackground() {
            if (appliction == null || getDownloadReceiver() == null) return null;
            List<DownloadEntity> list = new ArrayList<>();
            List<DownloadEntity> simpleTaskList = getDownloadReceiver().getTaskList();
            if (simpleTaskList != null) {
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
            if (simpleTaskList == null || getDownloadReceiver() == null) return;
            for (DownloadEntity entity : simpleTaskList) {
                if (!TextUtils.isEmpty(entity.getUrl()) || !TextUtils.isEmpty(entity.getDownloadPath())) {
                    String url = entity.getUrl();
                    if (url.startsWith("http") || url.startsWith("ftp")) {
                        getDownloadReceiver().load(entity).start();
                    }
                }
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
            if (list == null || list.size() == 0 || preferences == null) return;
            long timeMillis = System.currentTimeMillis();
            int version = preferences.getInt(IMAGE_VERSION, 0);
            for (SplashScreen splashScreen : list) {
                IMovieAppliction.KANKAN_COOKIE = splashScreen.getKankanCookie();
                IMovieAppliction.ALIPAYS = splashScreen.getAlipays();
                IMovieAppliction.ADVS = splashScreen.getAdvs();
                if (splashScreen.getStartTime() < timeMillis && splashScreen.getEndTime() > timeMillis && version < splashScreen.getVersion()) {
                    FileUtil.downloadBackgroud(splashScreen.getScreenImage(), file);
                    preferences.edit().putInt(IMAGE_VERSION, splashScreen.getVersion()).apply();
                    return;
                }
            }
        }

        @Override
        public void onError(int i, String s) {
            LogUtil.e(SplashActivity.class, "更新封面图片失败");
        }

    }
}
