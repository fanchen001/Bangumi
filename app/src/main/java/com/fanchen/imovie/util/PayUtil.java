package com.fanchen.imovie.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.net.URLEncoder;

public class PayUtil {
    // 支付宝包名
    public static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
    public static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    public static final String WECHAT_PACKAGE_NAME = "com.eg.android.AlipayGphone";
    public static final String ALIPAY_URL = "https://qr.alipay.com/fkx01963jwoywio2bn9zbf8";
    public static final String QQ_URL = "mqqapi://forward/url?plg_auth=1&url_prefix=aHR0cHM6Ly9pLnFpYW5iYW8ucXEuY29tL3dhbGxldC9zcXJjb2RlLmh0bT9tPXRlbnBheSZhPTEmdT03MTUxMjAzMTEmYWM9RjJFM0VBMTI2QzdDMDM2QUVDNzVGRTA5NTIwOTRFOTE4RkQwNUY4MEJBMDZGRjcyMkQyNTA0MUZGMjI3Mzk5RSZuPSVFNiVBMiU4MSVFNSVCMSVCMSVFNCVCQyVBRiVFNCVCRCU4RiVFOSU5OCVCMyVFNSU4RiVCMCZmPXdhbGxldCZvcGVuc2Nhbj0x";

    //第一步：检查是否安装
    public static boolean hasInstalledAlipayClient(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //第二步：调用者调用此方法跳转
    public static boolean startAlipayClient(Activity activity, String urlCode) {
        return startIntentUrl(activity, doFormUri(urlCode));
    }


    public static void startWechatClient(Activity activity) {
        try {
            //利用Intent打开微信
            Uri uri = Uri.parse("weixin://");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "无法跳转到微信，请检查是否安装了微信", Toast.LENGTH_SHORT).show();
        }
    }

    //第二步：调用者调用此方法跳转
    public static boolean startQQClient(Activity activity, String urlCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(QQ_PACKAGE_NAME, QQ_PACKAGE_NAME + ".activity.JumpActivity");
            intent.setData(Uri.parse(urlCode));
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "无法跳转到QQ，请检查是否安装了QQ", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //格式化urlCode
    private static String doFormUri(String urlCode) {
        try {
            urlCode = URLEncoder.encode(urlCode, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + urlCode;
        return alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis();
    }

    //主要功能代码：跳转
    private static boolean startIntentUrl(Activity activity, String intentFullUrl) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentFullUrl)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "无法跳转到支付宝，请检查是否安装了支付宝", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
