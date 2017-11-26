package com.fanchen.imovie;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadReceiver;
import com.arialyy.aria.core.download.DownloadTask;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.service.EmptyService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.NetworkUtil;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * 整个应用上下文
 * Created by fanchen on 2017/9/15.
 */
public class IMovieAppliction extends MultiDexApplication implements QbSdk.PreInitCallback {
    private static final String NET_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final static int TASK_UPDATE = 1;
    private final static int TASK_DELETE = 0;
    // 用来管理activity的列表,实现对程序整体异常的捕获
    private List<Activity> mActivitys = new ArrayList<>();
    // 当前应用程序fragment队列，主要是用来处理activity中的onBackPresseds事件
    private List<Fragment> mFragments = new ArrayList<>();

    private SoftReference<OnTaskRuningListener> runingListener;
    private Picasso picasso;
    //全局下载管理器
    private DownloadReceiver downloadReceiver;
    public static boolean isInitSdk = false;
    public static IMovieAppliction app = null;
    public boolean isFristNetwork = true;
    public String mAcg12Token = "";

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (runingListener == null) return;
            OnTaskRuningListener onTaskRuningListener = runingListener.get();
            if (onTaskRuningListener == null) return;
            switch (msg.what) {
                case TASK_DELETE:
                    onTaskRuningListener.onTaskCancel((DownloadTask) msg.obj);
                    break;
                case TASK_UPDATE:
                    onTaskRuningListener.onTaskUpdate((DownloadTask) msg.obj);
                    break;
            }
        }

    };

    static {
        PlatformConfig.setWeixin("wx11d8a2cfc060c228", "20426f9814f9f05da2ae37d89616c577");
        PlatformConfig.setSinaWeibo("3553472100", "bb22aa0b924586301609c10c7ad1afc3", "http://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1106461216", "hUIMorZnPWKdiOYG");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("swith_mode", true) ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this); // App的策略Bean
        strategy.setAppChannel(getPackageName()).setAppVersion(getVersion()).setAppReportDelay(100).setCrashHandleCallback(new AppCrashHandleCallback()); // 设置渠道
        CrashReport.initCrashReport(this, "7110e4106c", false, strategy); // 自定义策略生效，必须在初始化SDK前调用
        if (AppUtil.isMainProcess(this) && !isInitSdk) {
            startService(new Intent(this, EmptyService.class));
            initMainSdk();
        } else {
            initX5Sdk();
        }
    }

    public void initMainSdk() {
        UMShareAPI.init(this, "5978868307fe65109c0002fd");
        SMSSDK.initSDK(this, "216b8e0cd94ee", "81a4a9361ea6a111657619b8d613f8f8");
        Bmob.initialize(this, "0b1a20f9d304da48959020d40655ee3d");
        //网络改变
        isFristNetwork = true;
        registerReceiver(mNetworkReceiver, new IntentFilter(NET_ACTION));
        getDownloadReceiver().register();
        try {
            int num = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("multithreading", "1"));
            Aria.get(this).getDownloadConfig().setMaxTaskNum(num);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        isInitSdk = true;
    }

    public void initX5Sdk() {
        QbSdk.initX5Environment(this, this);
        isInitSdk = true;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (AppUtil.isMainProcess(this)) {
            getDownloadReceiver().unRegister();
            unregisterReceiver(mNetworkReceiver);
        }
    }

    @Override
    public ComponentName startService(Intent service) {
        try {
            return super.startService(service);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        try {
            super.startActivity(intent, options);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            return super.registerReceiver(receiver, filter);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        try {
            return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取全局下载管理器
     *
     * @return
     */
    public DownloadReceiver getDownloadReceiver() {
        if (downloadReceiver == null) {
            synchronized (IMovieAppliction.class) {
                if (downloadReceiver == null) {
                    downloadReceiver = Aria.download(this);
                }
            }
        }
        return downloadReceiver;
    }

    /**
     * 获取全局图片加载器
     *
     * @return
     */
    public Picasso getPicasso() {
        if (picasso == null) {
            synchronized (IMovieAppliction.class) {
                if (picasso == null) {
                    File cacheDir = AppUtil.getExternalCacheDir(this);
                    if (!cacheDir.exists()) cacheDir.mkdirs();
                    //64Mb的缓存
                    OkHttpClient client = new OkHttpClient.Builder().cache(new Cache(cacheDir, 64 * 1024 * 1024)).build();
                    picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(client)).build();
                }
            }
        }
        return picasso;
    }


    public void setRuningListener(OnTaskRuningListener runingListener) {
        this.runingListener = new SoftReference<>(runingListener);
    }

    /**
     * 向fragment队列添加一个
     */
    public void addFragment(Fragment f) {
        if (mFragments != null)
            mFragments.add(f);
    }

    /**
     * 弹出最上层的fragment
     */
    public Fragment popuFragment() {
        if (mFragments != null && mFragments.size() > 0)
            return mFragments.remove(mFragments.size() - 1);
        return null;
    }

    /**
     * 获取最上层的fragment
     */
    public Fragment getTopFragment() {
        if (mFragments != null && mFragments.size() > 0)
            return mFragments.get(mFragments.size() - 1);
        return null;
    }

    /**
     * 清空fragment队列
     */
    public void clearFragment() {
        if (mFragments != null)
            mFragments.clear();
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity a) {
        if (mActivitys != null)
            mActivitys.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        if (mActivitys != null)
            mActivitys.add(a);
    }

    /**
     * 获取最上层的Activity
     */
    public Activity getTopActivity() {
        if (mActivitys != null && mActivitys.size() >= 1)
            return mActivitys.get(mActivitys.size() - 1);
        return null;
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        while (mActivitys != null && mActivitys.size() > 0) {
            mActivitys.remove(0).finish();
        }
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    private String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return "version:" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "version:";
        }
    }

    @Override
    public void onCoreInitFinished() {

    }

    @Override
    public void onViewInitFinished(boolean b) {

    }

    // bugly回调
    private class AppCrashHandleCallback extends CrashReport.CrashHandleCallback {
        @Override
        public synchronized Map<String, String> onCrashHandleStart(
                int crashType, String errorType, String errorMessage,
                String errorStack) {
            String crashTypeName = null;
            switch (crashType) {
                case CrashReport.CrashHandleCallback.CRASHTYPE_JAVA_CATCH:
                    crashTypeName = "JAVA_CATCH";
                    break;
                case CrashReport.CrashHandleCallback.CRASHTYPE_JAVA_CRASH:
                    crashTypeName = "JAVA_CRASH";
                    break;
                case CrashReport.CrashHandleCallback.CRASHTYPE_NATIVE:
                    crashTypeName = "JAVA_NATIVE";
                    break;
                case CrashReport.CrashHandleCallback.CRASHTYPE_U3D:
                    crashTypeName = "JAVA_U3D";
                    break;
                default:
                    crashTypeName = "unknown";
            }
            Map<String, String> userDatas = super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack);
            if (userDatas == null) {
                userDatas = new HashMap<>();
            }
            userDatas.put("DEBUG", "TRUE");
            return userDatas;
        }
    }

    @Download.onTaskStop
    public void onTaskStop(DownloadTask task) {
        mHandler.obtainMessage(TASK_UPDATE, task).sendToTarget();
    }

    @Download.onTaskRunning
    public void onTaskRunning(DownloadTask task) {
        mHandler.obtainMessage(TASK_UPDATE, task).sendToTarget();
    }

    @Download.onTaskFail
    public void onTaskFail(DownloadTask task) {
        mHandler.obtainMessage(TASK_UPDATE, task).sendToTarget();
    }

    @Download.onTaskComplete
    public void onTaskComplete(DownloadTask task) {
        mHandler.obtainMessage(TASK_UPDATE, task).sendToTarget();
    }

    @Download.onTaskCancel
    public void onTaskCancel(DownloadTask task) {
        mHandler.obtainMessage(TASK_DELETE, task).sendToTarget();
    }

    /**
     * 网络状态的广播接收者
     *
     * @author Administrator
     */
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (NET_ACTION.equals(intent.getAction()) && !isFristNetwork) {
                //打开了流量保护
                //当不是wifi时，停止所有下载任务
                if (PreferenceManager.getDefaultSharedPreferences(IMovieAppliction.this).getBoolean("gprs_check", true)) {
                    AsyTaskQueue.newInstance().execute(new DownloadTaskListener(NetworkUtil.isWifiConnected(IMovieAppliction.this)));
                }
            }
            isFristNetwork = false;
        }
    };

    private class DownloadTaskListener extends AsyTaskListenerImpl<List<DownloadEntity>> {

        private boolean isWifiConnected;

        public DownloadTaskListener(boolean isWifiConnected) {
            this.isWifiConnected = isWifiConnected;
        }

        @Override
        public List<DownloadEntity> onTaskBackground() {
            if(getDownloadReceiver() == null)return null;
            if (isWifiConnected) {
                getDownloadReceiver().resumeAllTask();
                List<DownloadEntity> list = new ArrayList<>();
                for (DownloadEntity entity : getDownloadReceiver().getSimpleTaskList()) {
                    if (entity.getState() == IEntity.STATE_RUNNING || entity.getState() == IEntity.STATE_WAIT) {
                        list.add(entity);
                    }
                }
                return list;
            } else {
                getDownloadReceiver().stopAllTask();
            }
            return null;
        }

        @Override
        public void onTaskSuccess(List<DownloadEntity> data) {
            if (isWifiConnected && data != null) {
                for (DownloadEntity entity : data) {
                    if (!TextUtils.isEmpty(entity.getUrl()) && !TextUtils.isEmpty(entity.getDownloadPath())) {
                        String url = entity.getUrl();
                        if(url.startsWith("http") || url.startsWith("ftp")){
                            getDownloadReceiver().load(entity).start();
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    public interface OnTaskRuningListener {
        /**
         * @param task
         */
        void onTaskUpdate(DownloadTask task);

        /**
         * @param task
         */
        void onTaskCancel(DownloadTask task);
    }

}