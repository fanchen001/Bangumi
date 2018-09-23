package com.fanchen.imovie.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fanchen.imovie.jsoup.node.Node;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * VideoUrlUtil
 * Created by fanchen on 2018/6/8.
 */
public class VideoUrlUtil {
    private static VideoUrlUtil parseWebUrlHelper;

    private String webUrl = "";
    private Context mContext;
    private WebView webView;
    private int timeOut = 20 * 1000;//解析超时时间
    private int parserTime = 5000;//延时解析时间
    private String referer = "";// referer

    private boolean isVideoJs = false;
    private boolean isTimeout = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    private SoftReference<ViewGroup> mMainViewReference;//content view
    private OnParseWebUrlListener mOnParseWebUrlListener;//解析回掉


    public static VideoUrlUtil getInstance() {
        if (parseWebUrlHelper == null) {
            synchronized (VideoUrlUtil.class) {
                if (parseWebUrlHelper == null)
                    parseWebUrlHelper = new VideoUrlUtil();
            }
        }
        return parseWebUrlHelper;
    }

    /**
     *
     * @param act
     * @param url
     * @return
     */
    public VideoUrlUtil init(Activity act, String url) {
        return init(act, url, "");
    }

    /**
     *
     * @param act
     * @return
     */
    public VideoUrlUtil init(Activity act) {
        init(act, "");
        return this;
    }

    /**
     *
     * @param act
     * @param url
     * @param referer
     * @return
     */
    public VideoUrlUtil init(Activity act, String url, String referer) {
        synchronized (VideoUrlUtil.class){
            this.mContext = act.getApplication();
            this.webUrl = url;
            this.referer = referer;
            ViewGroup mainView = (ViewGroup) act.findViewById(android.R.id.content);
            if (mMainViewReference != null) {
                mMainViewReference = new SoftReference<>(mainView);
            }
            this.webView = new WebView(mContext);
            this.webView.setLayoutParams(new LinearLayout.LayoutParams(1, 1));
            mainView.addView(this.webView);
            initWebSettings();
        }
        return this;
    }

    /**
     * 設置超時時間
     * @param timeOut
     * @return
     */
    public VideoUrlUtil setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    /**
     * 設置延時解析時間
     * @param parserTime
     * @return
     */
    public VideoUrlUtil setParserTime(int parserTime) {
        this.parserTime = parserTime;
        return this;
    }

    /**
     * 設置url 和 referer
     * @param url
     * @param referer
     * @return
     */
    public VideoUrlUtil setLoadUrl(String url, String referer) {
        this.webUrl = url;
        this.referer = referer;
        return this;
    }

    /**
     * 設置url
     * @param url
     * @return
     */
    public VideoUrlUtil setLoadUrl(String url) {
        this.webUrl = url;
        return this;
    }

    /**
     * 釋放資源
     */
    public void destroy() {
        referer = "";
        if (handler != null) {
            handler.removeCallbacks(timeoutRunnable);
            handler.removeCallbacks(confirmRunnable);
            handler.removeCallbacks(parserRunnable);
        }
        if (mMainViewReference != null) {
            final ViewGroup viewGroup = mMainViewReference.get();
            if (viewGroup != null && webView != null) {
                webView.destroy();
                webView.removeAllViews();
                viewGroup.removeView(webView);
            }
        }
        mOnParseWebUrlListener = null;
    }

    /**
     * 設置解析回掉
     * @param onParseListener
     * @return
     */
    public VideoUrlUtil setOnParseListener(OnParseWebUrlListener onParseListener) {
        this.mOnParseWebUrlListener = onParseListener;
        return this;
    }

    /**
     * 開始解析
     * @return
     */
    public VideoUrlUtil startParse() {
        if(TextUtils.isEmpty(webUrl))return this;
        isTimeout = false;
        isVideoJs = false;
        postDelayed(timeoutRunnable,timeOut);//每次開始解析的時候，復位超時Runnable
        if (!TextUtils.isEmpty(referer)) {
            Map<String, String> map = new HashMap<>();
            map.put("Referer", referer);
            map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
            map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            map.put("Accept-Encoding", "gzip, deflate");
            webView.loadUrl(this.webUrl, map);
        } else {
            webView.loadUrl(this.webUrl);
        }
        referer = webUrl;
        LogUtil.e(VideoUrlUtil.class.getSimpleName(), "startParse => " + this.webUrl);
        return this;
    }


    private void initWebSettings() {
        WebView mWebView = this.webView;
        mWebView.clearFocus();
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setBuiltInZoomControls(true);// 隐藏缩放按钮
        mWebSettings.setUseWideViewPort(true);// 可任意比例缩放
        mWebSettings.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        mWebSettings.setSavePassword(true);
        mWebSettings.setSaveFormData(true);// 保存表单数据
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setTextZoom(100);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setSupportMultipleWindows(true);// 新加//我就是没有这一行，死活不出来。MD，硬是没有人写这一句！
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebSettings.setMediaPlaybackRequiresUserGesture(true);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            mWebSettings.setAllowFileAccessFromFileURLs(true);
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setAppCachePath(mContext.getCacheDir().getAbsolutePath());
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setGeolocationDatabasePath(mContext.getDir("database", 0).getPath());
        mWebSettings.setGeolocationEnabled(true);
        CookieManager instance = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(mContext.getApplicationContext());
        }
        instance.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            instance.setAcceptThirdPartyCookies(mWebView, true);
        }
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
        enabledCookie(webView);//启用cookie
    }

    /*启用cookie*/
    private void enabledCookie(WebView web) {
        CookieManager instance = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(mContext);
        }
        instance.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            instance.setAcceptThirdPartyCookies(web, true);
        }
    }

    /**
     * 延時執行某個 run
     * @param r
     * @param time
     */
    private void postDelayed(Runnable r, long time) {
        if (handler == null) return;
        handler.removeCallbacks(r);
        handler.postDelayed(r, time);
    }

    /**
     * 執行js 獲取 html
     * @param js
     */
    private void evalScript(String js) {
        if (webView == null || TextUtils.isEmpty(js)) return;
        String newJs = "javascript:" + js + "(document.getElementsByTagName('html')[0].innerHTML);";
        isVideoJs = true; //標記本次的javascript 由代碼手動觸發
        webView.loadUrl(newJs);
    }

    /**
     * 對url進行包裝
     * @param url
     * @return
     */
    private String warpUrl(String url) {
        try {
            if (url.startsWith("//")) {
                url = "http:" + url;
            } else if (url.startsWith("/")) {
                String[] split = webUrl.split("/");
                url = split[0] + "//" + split[2] + url;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 解析
     * @param html
     */
    private synchronized void parserUrl(String html) {
        if (!isVideoJs) return;
        isVideoJs = false;//标记复位
        if (html.contains("html") && html.contains("body")) {
            Node node = new Node(html);
            String video = node.attr("video", "src");
            String source = node.attr("source", "src");
            String iframe = node.attr("iframe", "src");
            if (!TextUtils.isEmpty(video) || ! TextUtils.isEmpty(source)) {//找到了video url
                handler.removeCallbacks(timeoutRunnable);//找到了视频地址，移除超时runnable
                video = TextUtils.isEmpty(video) ? source : video;
                String newUrl = warpUrl(video);
                if(mOnParseWebUrlListener != null)
                    mOnParseWebUrlListener.onFindUrl(newUrl);
                LogUtil.e(VideoUrlUtil.class.getSimpleName(), "onFindUrl url => " + newUrl);
                destroy();
            } else if (!TextUtils.isEmpty(iframe)) {//还需要请求一次
                webUrl = warpUrl(iframe);
                startParse();
                LogUtil.e(VideoUrlUtil.class.getSimpleName(), "包裹了iframe 需要再次解析 url => " + webUrl);
            } else {
                LogUtil.e(VideoUrlUtil.class.getSimpleName(), "未获取到视频地址");
                if(mOnParseWebUrlListener != null)
                    mOnParseWebUrlListener.onError("未获取到视频地址");
            }
        } else if (isTimeout) {
            isTimeout = false;//标记复位
            if(mOnParseWebUrlListener != null)
                mOnParseWebUrlListener.onError("获取视频地址  超時");
            LogUtil.e(VideoUrlUtil.class.getSimpleName(), "获取视频地址  超時");
        }
    }

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();//證書不對的時候，繼續加載
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            final long fParserTime = parserTime <= 0 ? 100 : parserTime;
            postDelayed(parserRunnable, fParserTime);//最少延時100ms執行
            LogUtil.e(VideoUrlUtil.class.getSimpleName(), "延时 " + fParserTime + "ms執行 evalScript，获取html");
        }

    };

    private WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
            parserUrl(s1);
            jsResult.cancel();
            return true;
        }

        @Override
        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
            handler.removeCallbacks(confirmRunnable);//Alert 已经执行，可以移除Confirm
            parserUrl(s1);
            jsResult.cancel();
            return true;
        }
    };

    //webview請求超時計時器
    private Runnable timeoutRunnable = new Runnable() {

        @Override
        public void run() {
            handler.removeCallbacks(this);//超时时间结束，自动调用，获取html
            isTimeout = true; //標記本次Runnable是由timeout觸發的
            final long fParserTime = parserTime <= 0 ? 100 : parserTime;
            postDelayed(parserRunnable, fParserTime);//最少延時100ms執行
            LogUtil.e(VideoUrlUtil.class.getSimpleName(), "超时结束，自动调用，获取html");
        }

    };

    //延時獲取Html
    private Runnable parserRunnable = new Runnable() {

        @Override
        public void run() {
            handler.removeCallbacks(this);
            evalScript("alert");
            postDelayed(confirmRunnable, parserTime);
        }

    };

    //如果alert 沒有執行，將在parserTime后執行confirm
    private Runnable confirmRunnable = new Runnable() {

        @Override
        public void run() {
            handler.removeCallbacks(this);
            evalScript("confirm");
            LogUtil.e(VideoUrlUtil.class.getSimpleName(), "alert 沒執行，執行confirm");
        }

    };

    /**
     * OnParseWebUrlListener
     * 通過url解析出video的真實地址
     */
    public interface OnParseWebUrlListener {
        /**
         * 找到了video url
         *
         * @param url
         */
        void onFindUrl(String url);

        /**
         * 沒有找到video url
         *
         * @param errorMsg
         */
        void onError(String errorMsg);
    }
}
