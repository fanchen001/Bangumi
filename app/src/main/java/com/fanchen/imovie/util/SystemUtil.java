package com.fanchen.imovie.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.fanchen.imovie.base.BaseActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;


/**
 * 系统级操作相关工具类
 *
 * @author fanchen
 */
@SuppressLint("ShowToast")
public class SystemUtil {
    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {
        String hostIp = "127.0.0.1";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;

    }

    public static void putTextIntoClipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy text", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 判断是否开启了自动亮度调节
     */

    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    // 然后就是要觉得当前的亮度了，这个就比较纠结了：

    /**
     * 获取屏幕的亮度
     */

    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    // 那如何修改屏幕的亮度呢？

    /**
     * 设置亮度
     */

    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    // 那么，能设置了，但是为什么还是会出现，设置了，没反映呢？

    // 嘿嘿，那是因为，开启了自动调节功能了，那如何关闭呢？这才是最重要的：

    /**
     * 停止自动亮度调节
     */

    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    // 能开启，那自然应该能关闭了哟哟，那怎么关闭呢？很简单的：

    /**
     * * 开启亮度自动调节 *
     *
     * @param activity
     */

    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    // 至此，应该说操作亮度的差不多都有了，结束！
    // 哎，本来认为是应该结束了，但是悲剧得是，既然像刚才那样设置的话，只能在当前的activity中有作用，一段退出的时候，会发现毫无作用，悲剧，原来是忘记了保存了。汗！

    /**
     * 保存亮度设置状态
     */

    public static void saveBrightness(ContentResolver resolver, int brightness) {
        Uri uri = Settings.System
                .getUriFor("screen_brightness");
        Settings.System.putInt(resolver, "screen_brightness",
                brightness);
        resolver.notifyChange(uri, null);
    }

    public static final int INSTALL_ROM = 0;
    public static final int INSTALL_SD = 1;
    public static final int UNINSTALL_UNBACKUP = 2;
    public static final int UNINSTALL_BACKUP = 3;

    public static void putText2Clipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy text", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * [获取cpu类型和架构]
     *
     * @return 三个参数类型的数组，第一个参数标识是不是ARM架构，第2个参数标识是不是neon指令集
     */
    public static String[] getCpuArchitecture() {
        String[] mArmArchitecture = new String[2];
        try {
            InputStream is = new FileInputStream("/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            try {
                String nameProcessor = "Processor";
                String nameFeatures = "Features";
                String nameModel = "model name";
                String nameCpuFamily = "cpu family";
                while (true) {
                    String line = br.readLine();
                    String[] pair = null;
                    if (line == null) {
                        break;
                    }
                    pair = line.split(":");
                    if (pair.length != 2)
                        continue;
                    String key = pair[0].trim();
                    String val = pair[1].trim();
                    if (key.compareTo(nameProcessor) == 0) {
                        for (int i = val.indexOf("ARMv") + 4; i < val.length(); i++) {
                            String temp = val.charAt(i) + "";
                            if (temp.matches("\\d")) {
                            } else {
                                break;
                            }
                        }
                        mArmArchitecture[0] = "ARM";
                        // mArmArchitecture[1] = Integer.parseInt(n);
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameFeatures) == 0) {
                        if (val.contains("neon")) {
                            mArmArchitecture[1] = "neon";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameModel) == 0) {
                        if (val.contains("Intel")) {
                            mArmArchitecture[0] = "INTEL";
                            mArmArchitecture[1] = "atom";
                        }
                        continue;
                    }

                    if (key.compareToIgnoreCase(nameCpuFamily) == 0) {
                        // mArmArchitecture[1] = Integer.parseInt(val);
                        continue;
                    }
                }
            } finally {
                br.close();
                ir.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArmArchitecture;
    }

    /***
     * @param context
     * @param file
     */
    public static void installApk(Context context, String file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(file)), "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启第三方应用
     *
     * @param urlString
     */
    public static void startThreeApp(BaseActivity context, String urlString) {
        startThreeApp(context, urlString, null, null, null);
    }

    /**
     * @param context
     * @param urlString
     * @param handler
     */
    public static void startThreeApp(BaseActivity context, String urlString,
                                     String handler) {
        Intent mIntent = new Intent();
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.setAction("android.intent.action.VIEW");
        mIntent.setData(Uri.parse(handler));
        startThreeApp(context, urlString, null, null, mIntent);
    }

    /**
     * @param context
     * @param urlString
     */
    public static void startThreeApp(BaseActivity context, String urlString,
                                     String packageName, String className) {
        startThreeApp(context, urlString, packageName, className, null);
    }

    /**
     * 开启第三方应用
     *
     * @param url
     * @param packageName
     * @param className
     */
    public static void startThreeApp(BaseActivity context, String url,
                                     String packageName, String className, Intent handler) {
        if (context == null || context.isFinishing()) return;
        boolean isException = false;
        try {
            Intent mIntent = new Intent();
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntent.setAction("android.intent.action.VIEW");
            if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className))
                mIntent.setClassName(packageName, className);
            if (!TextUtils.isEmpty(url))
                mIntent.setData(Uri.parse(url));
            context.startActivityForResult(mIntent, 0);
        } catch (Throwable e) {
            e.printStackTrace();
            isException = true;
        } finally {
            if (isException && handler != null) {
                try {
                    context.showToast("应用未找到，请先下载");
                    context.startActivityForResult(handler, 0);
                } catch (Throwable e) {
                    e.printStackTrace();
                    context.showToast("未找到对应第三方应用");
                }
            } else if (isException && handler == null) {
                context.showToast("未找到对应第三方应用");
            }
        }
    }

    /**
     * 获取系统可用内存空间大小
     *
     * @param context
     * @return long byte大小
     */
    public static long getAvailMemSize(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * 获取系统全部的内存空间大小
     *
     * @param context
     * @return long byte大小
     */
    public static long getTotalMemSize(Context context) {
        try {
            File file = new File("/proc/meminfo");
            FileInputStream fis = new FileInputStream(file);
            // MemTotal: 516452 kB
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            char[] chars = line.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : chars) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            br.close();
            fis.close();
            return Integer.parseInt(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 安装一个应用程序
     *
     * @param context
     * @param apkfile
     */
    public static void installApplication(Context context, File apkfile) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apkfile),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 静默安装应用（需要root权限），可以指定安装的位置 也可以用来对已经安装的应用进行搬家（rom-》sd或者sd-》rom）
     *
     * @param context 上下文
     * @param path    应用的全路径
     * @param flag    标示安装位置
     */
    public static void installAgain(Context context, String path, int flag) {
        Process p = null;
        PrintStream ps = null;
        try {
            p = Runtime.getRuntime().exec("su");
            ps = new PrintStream(p.getOutputStream());
            switch (flag) {
                case INSTALL_ROM:
                    ps.print("pm install -r " + path);
                    break;
                case INSTALL_SD:
                    ps.print("pm install -r -s " + path);
                    break;
            }

        } catch (IOException e) {
            Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.flush();
                ps.close();
            }
        }
    }

    /**
     * 开启一个应用程序
     *
     * @param context  上下文
     * @param packname 要运行的包名
     */
    public static void startApplication(Context context, String packname) {
        // 开启这个应用程序的第一个activity. 默认情况下 第一个activity就是具有启动能力的activity.
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(packname,
                    PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activityinfos = packinfo.activities;
            if (activityinfos != null && activityinfos.length > 0) {
                String className = activityinfos[0].name;
                Intent intent = new Intent();
                intent.setClassName(packname, className);
                context.startActivity(intent);
            } else {
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 强力卸载一个应用程序（根据应用的保存路径来删除）需要root权限 可以用来卸载系统自带的应用程序，这是一个危险的操作，请谨慎使用
     * 当然，你也可以在卸载之前备份这个应用，使用UNINSTALL_BACKUP即可
     *
     * @param context    上下文
     * @param path       要卸载的应用的绝对路径
     * @param flag       是否备份这个应用
     * @param backupPath 备份应用的保存路径，如果是UNINSTALL_UNBACKUP，这个参数可以直接填写null
     */
    public static void uninstallsApplication(Context context, String path,
                                             int flag, String backupPath) {
        Process p = null;
        PrintStream ps = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            p = Runtime.getRuntime().exec("su");
            ps = new PrintStream(p.getOutputStream());
            switch (flag) {
                case UNINSTALL_UNBACKUP:
                    ps.print("rm -f " + path);
                    break;
                case UNINSTALL_BACKUP:
                    fis = new FileInputStream(path);
                    File dir = new File(backupPath);
                    if (!dir.exists())
                        dir.createNewFile();
                    File file = new File(dir, path.substring(path.lastIndexOf(".")));
                    fos = new FileOutputStream(file);
                    byte[] buff = new byte[1024];
                    int len = -1;
                    while ((len = fis.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                    }
                    fos.flush();
                    ps.print("rm -f " + path);
                    break;
            }

        } catch (IOException e) {
            Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.flush();
                ps.close();
            }
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                }
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                }

        }

    }

    /**
     * 卸载应用程序
     *
     * @param context  上下文
     * @param packname 要卸载的包名
     */
    public static void uninstallApplication(Context context, String packname) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        // 附加的额外的参数
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + packname));
        context.startActivity(intent);
    }

    /**
     * 应用的快捷方式的创建 . 需要如下权限:com.android.launcher.permission.INSTALL_SHORTCUT
     *
     * @param mContext 应用上下文
     * @param name     快捷方式的名称
     * @param clazz    点击快捷方式启动的界面
     * @param drawable 快捷方式的图标
     */
    public static <T extends Context> void installShortCut(Context mContext,
                                                           String name, Class<T> clazz, int drawable) {
        Intent shortIntent = new Intent();
        // 设置创建快捷方式的过滤器action
        shortIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        // 设置生成的快捷方式的名字
        shortIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        // 设置生成的快捷方式的图标
        shortIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(mContext, drawable));
        Intent mIntent = new Intent(mContext, clazz);
        shortIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mIntent);
        // 发送广播生成快捷方式
        mContext.sendBroadcast(shortIntent);
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean getRootPermission(Context context) {
        String packageCodePath = context.getPackageCodePath();
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + packageCodePath;
            // 切换到root帐号
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 设置是否启用WIFI网络
     *
     * @param context
     * @param status
     */
    public static void toggleWiFi(Context context, boolean status) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (status == true && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else if (status == false && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 设置启用数据流量
     *
     * @param context
     */
    public final static void setMobileNetEnable(Context context) {
        Object[] arg = null;
        try {
            boolean isMobileDataEnable = invokeMethod(context,
                    "getMobileDataEnabled", arg);
            if (!isMobileDataEnable) {
                invokeBooleanArgMethod(context, "setMobileDataEnabled", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置不启用数据流量
     */
    public final static void setMobileNetUnable(Context context) {
        Object[] arg = null;
        try {
            boolean isMobileDataEnable = invokeMethod(context,
                    "getMobileDataEnabled", arg);
            if (isMobileDataEnable) {
                invokeBooleanArgMethod(context, "setMobileDataEnabled", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //

    /**
     * 执行某个影藏方法
     *
     * @param context
     * @param methodName
     * @param arg
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean invokeMethod(Context context, String methodName,
                                       Object[] arg) throws Exception {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Class ownerClass = mConnectivityManager.getClass();
        Class[] argsClass = null;
        if (arg != null) {
            argsClass = new Class[1];
            argsClass[0] = arg.getClass();
        }
        Method method = ownerClass.getMethod(methodName, argsClass);
        Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
        return isOpen;
    }

    /**
     * 调用context某个方法
     *
     * @param context
     * @param methodName
     * @param value
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object invokeBooleanArgMethod(Context context,
                                                String methodName, boolean value) throws Exception {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Class ownerClass = mConnectivityManager.getClass();
        Class[] argsClass = new Class[1];
        argsClass[0] = boolean.class;
        Method method = ownerClass.getMethod(methodName, argsClass);
        return method.invoke(mConnectivityManager, value);
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
