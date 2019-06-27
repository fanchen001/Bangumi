package com.fanchen.imovie;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadReceiver;
import com.arialyy.aria.core.download.DownloadTask;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.entity.VideoCategory;
import com.fanchen.imovie.picasso.PicassoListener;
import com.fanchen.imovie.picasso.download.HttpRedirectDownLoader;
import com.fanchen.imovie.service.EmptyService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.NetworkUtil;
import com.fanchen.imovie.util.SoCheckUtil;
import com.fanchen.imovie.util.StreamUtil;
import com.fanchen.m3u8.M3u8Config;
import com.fanchen.m3u8.M3u8Manager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xunlei.XLAppliction;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * 整个应用上下文
 * Created by fanchen on 2017/9/15.
 */
public class IMovieAppliction extends XLAppliction implements QbSdk.PreInitCallback {
    public static String KANKAN_COOKIE = "";
    public static String[] ADVS = null;
    public static boolean[] FLAGS = new boolean[]{false,false};
    public static List<VideoCategory> mCategorys = new ArrayList<>();
    public static String ALIPAYS = "alipays://platformapi/startapp?appId=20000067&__open_alipay__=YES&url=https://qr.alipay.com/c1x094332eotzkcdjjmx7bf";
    private static final String NET_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private final static int TASK_UPDATE = 1;
    private final static int TASK_DELETE = 0;
    // 用来管理activity的列表,实现对程序整体异常的捕获
    private List<Activity> mActivitys = new ArrayList<>();
    // 当前应用程序fragment队列，主要是用来处理activity中的onBackPresseds事件
    private List<Fragment> mFragments = new ArrayList<>();

    private Picasso picasso;
    //全局下载管理器
    private DownloadReceiver downloadReceiver;
    public static boolean isInitSdk = false;
    public static IMovieAppliction app = null;
    public String mAcg12Token = "";
    public int multithreading = 3;

    private SharedPreferences mSharedPreferences;
    private List<OnTaskRuningListener> runingListener = new ArrayList<>();

    public Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (runingListener == null || msg == null || runingListener.isEmpty()) return;
            for (OnTaskRuningListener onTaskRuningListener : runingListener) {
                if (msg.what == TASK_DELETE) {
                    onTaskRuningListener.onTaskCancel((DownloadTask) msg.obj);
                } else if (msg.what == TASK_UPDATE) {
                    onTaskRuningListener.onTaskUpdate((DownloadTask) msg.obj);
                }
            }
        }

    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        com.fanchen.sniffing.LogUtil.E = false;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(app = this);
        multithreading = getMultithreading(mSharedPreferences);
        boolean swith_mode = mSharedPreferences.getBoolean("swith_mode", true);
        AppCompatDelegate.setDefaultNightMode(swith_mode ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
        if (SoCheckUtil.check(new String[]{"bmob", "Bugly", "ijkffmpeg", "ijkplayer", "ijksdl",
                "p2p", "vbyte-v7a", "xl_stat", "xl_thunder_sdk", "xluagc"})) {
            startService(new Intent(this, EmptyService.class));
            initSdk(getApplicationContext(), AppUtil.isMainProcess(this));
        } else {
            if (AppUtil.isMainProcess(this)) {
                Toast.makeText(this, "应用不支持该机型", Toast.LENGTH_LONG).show();
            } else {
                finishActivity();
            }
        }


    }

    /**
     * initSdk
     *
     * @param context
     * @param mainProcess
     */
    private void initSdk(Context context, boolean mainProcess) {
        try {
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this); // App的策略Bean
            strategy.setAppChannel(getPackageName()).setAppVersion(AppUtil.getVersionName(this)).setAppReportDelay(100).setCrashHandleCallback(new AppCrashHandleCallback()); // 设置渠道
            CrashReport.initCrashReport(this, "7110e4106c", false, strategy); // 自定义策略生效，必须在初始化SDK前调用
//            P2PApi.init(context);
            if (mainProcess && !isInitSdk) {
                registerReceiver(mNetworkReceiver, new IntentFilter(NET_ACTION)); //网络改变
                initM3u8Config(context);
                initMainSdk();
                checkURLConnection();
            } else {
                initX5Sdk();
            }
        } catch (Throwable e) {
            String format = String.format("应用初始化错误 <%s>", e.toString());
            Toast.makeText(this, format, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkURLConnection() throws Throwable {
        String json = new String(StreamUtil.stream2bytes(getAssets().open("category.json")));
        List<VideoCategory> list = new Gson().fromJson(json, new TypeToken<List<VideoCategory>>() {}.getType());

        int limit = list.size() / 2;
        List<VideoCategory> subList1 = list.subList(0, limit);
        List<VideoCategory> subList2 = list.subList(limit, list.size());

        mCategorys.addAll(list);

        new CheckURLThread(0,subList1).start();
        new CheckURLThread(1,subList2).start();
    }

    /**
     * 初始化m3u8下载器
     *
     * @param context
     * @throws Exception
     */
    private void initM3u8Config(Context context) throws Throwable {
        M3u8Config config = M3u8Config.INSTANCE;
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String packageName = AppUtil.getPackageName(context);
        String separator = File.separator;
        String format = String.format("%s%s%s%s%s%s%s%s%s", absolutePath, separator, "Android", separator, "data", separator, packageName, separator, "M3u8");
        config.setContext(getApplicationContext());
        config.setConnTimeout(30 * 1000);
        config.setReadTimeout(60 * 1000);
        config.setThreadCount(multithreading);
        config.setM3u8Path(format);
    }

    /**
     * 初始化友盟，bmob
     *
     * @throws Exception
     */
    public void initMainSdk() throws Throwable {
        UMShareAPI.init(this, "5978868307fe65109c0002fd");
        SMSSDK.initSDK(this, "216b8e0cd94ee", "81a4a9361ea6a111657619b8d613f8f8");
        Bmob.initialize(this, "0b1a20f9d304da48959020d40655ee3d");
        PlatformConfig.setWeixin("wx11d8a2cfc060c228", "20426f9814f9f05da2ae37d89616c577");
        PlatformConfig.setSinaWeibo("3553472100", "bb22aa0b924586301609c10c7ad1afc3", "http://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1106461216", "hUIMorZnPWKdiOYG");
        getDownloadReceiver().register();
        Aria.get(this).getDownloadConfig().setMaxTaskNum(multithreading);
        isInitSdk = true;
    }

    private int getMultithreading(SharedPreferences preferences) {
        try {
            String string = preferences.getString("multithreading", "1");
            return Integer.parseInt(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multithreading;
    }

    public void initX5Sdk() throws Exception {
        QbSdk.initX5Environment(this, this);
        isInitSdk = true;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mHandler = null;
        if (!AppUtil.isMainProcess(this)) return;
        getDownloadReceiver().unRegister();
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public ComponentName startService(Intent service) {
        try {
            return super.startService(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        try {
            super.startActivity(intent, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            return super.registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        try {
            return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
        } catch (Exception e) {
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
                    picasso = new Picasso.Builder(this).listener(new PicassoListener()).downloader(new HttpRedirectDownLoader(getImageCacheClient())).build();
                    Picasso.setSingletonInstance(picasso);
                }
            }
        }
        return picasso;
    }

    public OkHttpClient getImageCacheClient() {
        File cacheDir = AppUtil.getExternalCacheDir(this);
        if (!cacheDir.exists()) cacheDir.mkdirs();
        Cache cache = new Cache(cacheDir, 128 * 1024 * 1024);
        List<Protocol> protocols = Collections.singletonList(Protocol.HTTP_1_1);
        SSLSocketFactory sslSocketFactory = StreamUtil.getSSLSocketFactory();
        return new OkHttpClient.Builder().cache(cache).readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).protocols(protocols).sslSocketFactory(sslSocketFactory).build();
    }

    /**
     * @param listener
     */
    public void addRuningListener(OnTaskRuningListener listener) {
        if (!runingListener.contains(listener)) runingListener.add(listener);
    }

    /**
     * @param listener
     */
    public void removeRuningListener(OnTaskRuningListener listener) {
        runingListener.remove(listener);
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
        }// 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public void onCoreInitFinished() {
        LogUtil.e(getClass().getSimpleName(), "onCoreInitFinished");
    }

    @Override
    public void onViewInitFinished(boolean b) {
        LogUtil.e(getClass().getSimpleName(), "onViewInitFinished");
    }


    // bugly回调
    private class AppCrashHandleCallback extends CrashReport.CrashHandleCallback {

        @Override
        public synchronized Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
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
        if (mHandler != null && runingListener != null && !runingListener.isEmpty())
            mHandler.obtainMessage(TASK_UPDATE, task).sendToTarget();
    }

    @Download.onTaskRunning
    public void onTaskRunning(DownloadTask task) {
        if (mHandler != null && runingListener != null && !runingListener.isEmpty())
            mHandler.obtainMessage(TASK_UPDATE, task).sendToTarget();
    }

    @Download.onTaskFail
    public void onTaskFail(DownloadTask task) {
        if (mHandler != null && runingListener != null && !runingListener.isEmpty())
            mHandler.obtainMessage(TASK_UPDATE, task).sendToTarget();
    }

    @Download.onTaskComplete
    public void onTaskComplete(DownloadTask task) {
        if (mHandler != null && runingListener != null && !runingListener.isEmpty())
            mHandler.obtainMessage(TASK_UPDATE, task).sendToTarget();
    }

    @Download.onTaskCancel
    public void onTaskCancel(DownloadTask task) {
        if (mHandler != null && runingListener != null && !runingListener.isEmpty())
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
            if (NET_ACTION.equals(intent.getAction()) && mSharedPreferences.getBoolean("gprs_check", true)) {
                boolean connected = NetworkUtil.isWifiConnected(IMovieAppliction.this);//打开了流量保护
                AsyTaskQueue.newInstance().execute(new DownloadTaskListener(connected));//当不是wifi时，停止所有下载任务
                if (connected) {
                    M3u8Manager.INSTANCE.start();
                } else {
                    M3u8Manager.INSTANCE.stop();
                }
            } else if (NetworkUtil.isNetWorkAvailable(context)) {
                AsyTaskQueue.newInstance().execute(new DownloadTaskListener(false));//当不是wifi时，停止所有下载任务
                M3u8Manager.INSTANCE.start();
            }
        }

    };

    private class DownloadTaskListener extends AsyTaskListenerImpl<List<DownloadEntity>> {

        private boolean isWifiConnected;

        public DownloadTaskListener(boolean isWifiConnected) {
            this.isWifiConnected = isWifiConnected;
        }

        @Override
        public List<DownloadEntity> onTaskBackground() {
            if (getDownloadReceiver() == null) return null;
            if (isWifiConnected) {
                getDownloadReceiver().resumeAllTask();
                List<DownloadEntity> list = new ArrayList<>();
                List<DownloadEntity> taskList = getDownloadReceiver().getTaskList();
                if (taskList != null)
                    for (DownloadEntity entity : taskList) {
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
            if (!(isWifiConnected && data != null)) return;
            for (DownloadEntity entity : data) {
                if (!TextUtils.isEmpty(entity.getUrl()) && !TextUtils.isEmpty(entity.getDownloadPath())) {
                    String url = entity.getUrl();
                    if (url.startsWith("http") || url.startsWith("ftp")) {
                        getDownloadReceiver().load(entity).start();
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

    private static class CheckURLThread extends Thread {
        private List<VideoCategory> mCategory;
        private int position;

        public CheckURLThread(int position,List<VideoCategory> mCategory) {
            this.mCategory = mCategory;
            this.position = position;
        }

        @Override
        public void run() {
            for (VideoCategory videoCategory : mCategory) {
                String url = videoCategory.getUrl();
                if (StreamUtil.check(url)) {
                    videoCategory.setSuccess(true);
                    LogUtil.e("IMovieAppliction", "url -> " + url + "  连接成功 ");
                } else {
                    videoCategory.setSuccess(false);
                    LogUtil.e("IMovieAppliction", "url -> " + url + "  连接失败 ");
                }
            }
            IMovieAppliction.FLAGS[position] = true;
        }

    }

}