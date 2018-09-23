package com.fanchen.imovie.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APP 工具类<br>
 */
public class AppUtil {
    public static final int IO_BUFFER_SIZE = 10 * 1024;

    /**
     * Check if OS version has a http URLConnection bug. See here for more
     * information:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     *
     * @return
     */
    public static boolean hasHttpConnectionBug() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
    }

    public static Map<String, String> getDownloadHeader(){
        return getDownloadHeader("");
    }

    public static Map<String, String> getDownloadHeader(String ref) {
        Map<String, String> map = new HashMap<>();
        map.put("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_1_1 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3 XiaoMi/MiuiBrowser/10.1.1");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("Accept-Language", "zh-CN,zh;q=0.9");
        if(!TextUtils.isEmpty(ref))map.put("Referer", ref);
        return map;
    }

    public static String getSize(long var1) {
        long var4 = var1;
        if (var1 == 0L) {
            var4 = 1L;
        }
        float var3 = (float) var4;
        return var3 == 0.0F ? "-- KB" : (var3 < 1048576.0F ? (float) Math.round(var3 / 1024.0F * 10.0F) / 20.0F + " KB" : (var3 >= 1048576.0F && var3 < 1.07374195E9F ? (float) Math.round(var3 / 1024.0F / 1024.0F * 10.0F) / 20.0F + " MB" : (var3 >= 1.07374195E9F ? (float) Math.round(var3 / 1024.0F / 1024.0F / 1024.0F * 100.0F) / 200.0F + " GB" : "-- KB")));
    }

    public static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (hasHttpConnectionBug()) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Get the size in bytes of a bitmap.
     *
     * @param bitmap
     * @return size in bytes
     */
    @SuppressLint("NewApi")
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Check if OS version has built-in external cache dir method.
     *
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @SuppressLint("NewApi")
    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null)
                return cacheDir;
        }
        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        File file = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getExternalHeaderDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }
        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/header/";
        File file = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取包信息.
     *
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获得APP的label名字
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        if (context == null) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            String appName = context.getResources().getString(labelRes);
            return appName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序版本名称信息
     */
    public static String getVersionName(Context context) {
        if (context == null) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序包名
     */
    public static String getPackageName(Context context) {
        if (context == null) {
            return null;
        }
        String pkgName = context.getPackageName();
        return pkgName;
    }

    public static Drawable getIcon(Context context) {
        if (context == null) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int icon = packageInfo.applicationInfo.icon;
            Drawable drawable = context.getResources().getDrawable(icon);
            return drawable;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 包名判断是否为主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        return context.getPackageName().equals(getProcessName(context));
    }

    /**
     * 获取进程名称
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    /**
     * @param context
     * @return
     */
    public static String getVideoPath(Context context) {
        File mDownloadDir = new File(Environment.getExternalStorageDirectory() + "/android/data/" + context.getPackageName() + "/video/");
        if (!mDownloadDir.exists()) mDownloadDir.mkdirs();
        return mDownloadDir.getAbsolutePath();
    }

    public static String getApkPath(Context context) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/android/data/" + context.getPackageName() + "/download/apk/");
        if (!dir.exists()) dir.mkdirs();
        return dir.getAbsolutePath();
    }
}
