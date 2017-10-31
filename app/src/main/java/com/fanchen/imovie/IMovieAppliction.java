package com.fanchen.imovie;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadReceiver;
import com.arialyy.aria.core.download.DownloadTask;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.NetworkUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;

/**
 * 整个应用上下文
 * Created by fanchen on 2017/9/15.
 */
public class IMovieAppliction extends Application {
    private static final String NET_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final static int TASK_UPDATE = 1;
    private final static int TASK_DELETE = 0;
    // 用来管理activity的列表,实现对程序整体异常的捕获
    private List<Activity> mActivitys = new ArrayList<>();
    // 当前应用程序fragment队列，主要是用来处理activity中的onBackPresseds事件
    private List<Fragment> mFragments = new ArrayList<>();

    private SoftReference<OnTaskRuningListener> runingListener;
    public static IMovieAppliction app = null;
    public boolean isFristNetwork = true;
    public String mAcg12Token = "";

    private Handler mHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            if(runingListener == null)return;
            OnTaskRuningListener onTaskRuningListener = runingListener.get();
            if(onTaskRuningListener == null)return;
            switch (msg.what){
                case TASK_DELETE:
                    onTaskRuningListener.onTaskCancel((DownloadTask)msg.obj);
                    break;
                case TASK_UPDATE:
                    onTaskRuningListener.onTaskUpdate((DownloadTask) msg.obj);
                    break;
            }
        }

    };

    static {
        PlatformConfig.setWeixin("wx11d8a2cfc060c228", "192e21d10c008f952823d0809e24871f");
        PlatformConfig.setSinaWeibo("3553472100", "bb22aa0b924586301609c10c7ad1afc3", "http://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1106461216", "hUIMorZnPWKdiOYG");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this); // App的策略Bean
        strategy.setAppChannel(getPackageName()); // 设置渠道
        strategy.setAppVersion(getVersion()); // App的版本
        strategy.setAppReportDelay(100); // 设置SDK处理延时，毫秒
        strategy.setCrashHandleCallback(new AppCrashHandleCallback());
        CrashReport.initCrashReport(this, "7110e4106c", false, strategy); // 自定义策略生效，必须在初始化SDK前调用
        UMShareAPI.init(this,"5978868307fe65109c0002fd");
        SMSSDK.initSDK(this, "216b8e0cd94ee", "81a4a9361ea6a111657619b8d613f8f8");
        Bmob.initialize(this, "0b1a20f9d304da48959020d40655ee3d");
        //网络改变
        isFristNetwork = true;
        registerReceiver(mNetworkReceiver, new IntentFilter(NET_ACTION));
        try{
            Aria.download(this).register();
            Aria.get(this).getDownloadConfig().setMaxTaskNum(Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("multithreading", "1")));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Aria.download(this).unRegister();
        unregisterReceiver(mNetworkReceiver);
    }

    public void setRuningListener(OnTaskRuningListener runingListener) {
        this.runingListener = new SoftReference<>(runingListener);
    }

    /**
     * 向fragment队列添加一个
     */
    public void addFragment(Fragment f) {
        if(mFragments != null)
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
        if(mFragments != null)
            mFragments.clear();
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity a) {
        if(mActivitys != null)
            mActivitys.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        if(mActivitys != null)
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
            Map<String, String> userDatas = super.onCrashHandleStart(crashType,errorType, errorMessage, errorStack);
            if (userDatas == null) {
                userDatas = new HashMap<>();
            }
            userDatas.put("DEBUG", "TRUE");
            return userDatas;
        }
    }

    @Download.onTaskStop
    public void onTaskStop(DownloadTask task) {
        mHandler.obtainMessage(TASK_UPDATE,task).sendToTarget();
    }

    @Download.onTaskRunning
    public void onTaskRunning(DownloadTask task) {
        mHandler.obtainMessage(TASK_UPDATE,task).sendToTarget();
    }

    @Download.onTaskFail
    public void onTaskFail(DownloadTask task) {
        mHandler.obtainMessage(TASK_UPDATE,task).sendToTarget();
    }

    @Download.onTaskComplete
    public void onTaskComplete(DownloadTask task) {
        mHandler.obtainMessage(TASK_UPDATE,task).sendToTarget();
    }

    @Download.onTaskCancel
    public void onTaskCancel(DownloadTask task) {
        mHandler.obtainMessage(TASK_DELETE,task).sendToTarget();
    }

    /**
     * 网络状态的广播接收者
     *
     * @author Administrator
     *
     */
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (NET_ACTION.equals(intent.getAction()) && ! isFristNetwork) {
                //打开了流量保护
                //当不是wifi时，停止所有下载任务
                if(PreferenceManager.getDefaultSharedPreferences(IMovieAppliction.this).getBoolean("gprs_check", true)){
                    AsyTaskQueue.newInstance().execute(new DownloadTaskListener(NetworkUtil.isWifiConnected(IMovieAppliction.this)));
                }
            }
            isFristNetwork = false;
        }
    };

    private class DownloadTaskListener extends AsyTaskListenerImpl<List<DownloadEntity>>{

        private boolean isWifiConnected;

        public DownloadTaskListener(boolean isWifiConnected){
            this.isWifiConnected = isWifiConnected;
        }

        @Override
        public List<DownloadEntity> onTaskBackground() {
            DownloadReceiver download = Aria.download(IMovieAppliction.this);
            if(isWifiConnected){
                download.resumeAllTask();
                List<DownloadEntity> list = new ArrayList<>();
                for (DownloadEntity entity : download.getSimpleTaskList()){
                    if(entity.getState() == IEntity.STATE_RUNNING || entity.getState() == IEntity.STATE_WAIT){
                        list.add(entity);
                    }
                }
                return  list;
            }else{
                download.stopAllTask();
            }
            return null;
        }

        @Override
        public void onTaskSuccess(List<DownloadEntity> data) {
            if(isWifiConnected){
                for (DownloadEntity entity : data){
                    Aria.download(IMovieAppliction.this).load(entity.getUrl()).start();
                }
            }
        }
    }

    /**
     *
     */
    public interface OnTaskRuningListener {
        /**
         *
         * @param task
         */
        void onTaskUpdate(DownloadTask task);

        /**
         *
         * @param task
         */
        void onTaskCancel(DownloadTask task);
    }

}