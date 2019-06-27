package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.ShareUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.imovie.view.webview.SwipeWebView;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * 使用浏览器进行视频播放的页面
 * Created by fanchen on 2017/10/3.
 */
public class WebPlayerActivity extends BaseActivity {

    public static final String[] LUXIANS = {"https://api.47ks.com/webcloud/?v=%s","http://api.bbbbbb.me/playm3u8/?url=%s",
            "http://jx.daheiyun.com/?url=%s","https://api.daidaitv.com/index/?url=%s","http://mv.688ing.com/player?url=%s"};

    public static final String URL = "url";
    public static final String LUXIAN = "luxian";
    public static final String REFERER = "referer";

    @InjectView(R.id.toolbar_top)
    protected Toolbar mToolbar;
    @InjectView(R.id.swv_play)
    protected SwipeWebView mWebview;

    /**
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String url) {
        startActivity(context, url, null, -1);
    }

    /**
     * @param context
     * @param url
     * @param luxian
     */
    public static void startActivity(Context context, String url, int luxian) {
        startActivity(context, url, null, luxian);
    }

    /**
     * @param context
     * @param url
     * @param referer
     */
    public static void startActivity(Context context, String url, String referer) {
        startActivity(context, url, referer, -1);
    }

    /**
     * @param context
     * @param url
     * @param referer
     * @param luxian
     */
    public static void startActivity(Context context, String url, String referer, int luxian) {
        try {
            Intent intent = new Intent(context, WebPlayerActivity.class);
            intent.putExtra(URL, url);
            if (luxian >= 0) {
                intent.putExtra(LUXIAN, luxian);
            }
            if (!TextUtils.isEmpty(referer)) {
                intent.putExtra(REFERER, referer);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide the status bar
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_web_player;
    }

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        WebView webView = mWebview.getWebView();
        Bundle data = new Bundle();
        data.putBoolean("standardFullScreen", true);//true表示标准全屏，false表示X5全屏；不设置默认false，
        data.putInt("DefaultVideoScreen", 2);//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
        if (webView.getX5WebViewExtension() != null) {
            webView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);// 开启DOM
        settings.setAllowFileAccess(true);// 设置支持文件流
        settings.setUseWideViewPort(true);// 调整到适合webview大小
        settings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        settings.setBlockNetworkImage(true);// 提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        settings.setAppCacheEnabled(true);// 开启缓存机制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(0);
        }
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        registerForContextMenu(webView);
        if (getIntent().hasExtra(LUXIAN) && getIntent().hasExtra(URL)) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("luxian_hit", true)) {
                new Handler(Looper.getMainLooper()).postDelayed(tipRunnable, 1500);
            }
            String url = String.format(LUXIANS[getIntent().getIntExtra(LUXIAN, 0)], getIntent().getStringExtra(URL));
            if (getIntent().hasExtra(REFERER)) {
                Map<String, String> map = new HashMap<>();
                map.put("Referer", getIntent().getStringExtra(REFERER));
                map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
                map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                map.put("Accept-Encoding", "gzip, deflate");
                mWebview.loadUrl(url, map);
            } else {
                mWebview.loadUrl(url);
            }
        } else if (getIntent().hasExtra(URL)) {
            if (getIntent().hasExtra(REFERER)) {
                Map<String, String> map = new HashMap<>();
                map.put("Referer", getIntent().getStringExtra(REFERER));
                map.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
                map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                map.put("Accept-Encoding", "gzip, deflate");
                mWebview.loadUrl(getIntent().getStringExtra(URL), map);
            } else {
                mWebview.loadUrl(getIntent().getStringExtra(URL));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebview != null && mWebview.getWebView() != null) {
            mWebview.getWebView().destroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().hasExtra(LUXIAN)) {
            getMenuInflater().inflate(R.menu.menu_luxian, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_web, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getIntent().hasExtra(LUXIAN)) {
            switch (item.getItemId()) {
                case R.id.action_luxian:
                    DialogUtil.showMaterialListDialog(this, getResources().getStringArray(R.array.spinner_luxian), clickListener);
                    break;
            }
        } else {
            switch (item.getItemId()) {
                case R.id.action_share:
                    ShareUtil.share(this, mWebview.getWebView().getTitle(), mWebview.getWebView().getUrl());
                    break;
                case R.id.action_start:
                    SystemUtil.startThreeApp(this, mWebview.getWebView().getUrl());
                    break;
                case R.id.action_copy:
                    SystemUtil.putText2Clipboard(this, mWebview.getWebView().getUrl());
                    showToast(getString(R.string.clipboard));
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isDoubleFinish() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebview != null) {
            mWebview.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebview != null) {
            mWebview.onPause();
        }
    }

    private WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mWebview.getProgressBar().setVisibility(View.GONE);
                mWebview.getRefreshView().setRefreshing(false);
            } else {
                if (mWebview.getProgressBar().getVisibility() == View.GONE)
                    mWebview.getProgressBar().setVisibility(View.VISIBLE);
                mWebview.getProgressBar().setProgress(newProgress);
            }
        }

    };

    /**
     * // 设置web页面 // 如果页面中链接，如果希望点击链接继续在当前browser中响应， //
     * 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
     *
     * @author fanchen
     */
    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
            if (url.contains("456jjh") || url.contains("jianduankm")) {//
                return new WebResourceResponse(null, null, null);//含有广告资源屏蔽请求
            } else {
                return super.shouldInterceptRequest(webView, url);//正常加载
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri localUri = Uri.parse(url);
            String scheme = localUri.getScheme();
            if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                view.loadUrl(url);
            } else {
                SystemUtil.startThreeApp(WebPlayerActivity.this, url);
            }
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (isFinishing()) return;
            try {
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        // 设置程序的标题为网页的标题
        @Override
        public void onPageFinished(WebView view, String url) {
            if (isFinishing()) return;
            view.setLayerType(View.LAYER_TYPE_NONE, null);
            view.getSettings().setBlockNetworkImage(false);
        }

        // 当load有ssl层的https页面时，如果这个网站的安全证书在Android无法得到认证，
        // WebView就会变成一个空白页，而并不会像PC浏览器中那样跳出一个风险提示框
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 忽略证书的错误继续Load页面内容
            handler.proceed();
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            view.requestFocus();
            view.requestFocusFromTouch();
        }

    };



    private Runnable tipRunnable = new Runnable() {

        @Override
        public void run() {
            if (isFinishing()) return;
            DialogUtil.showCancelableDialog(WebPlayerActivity.this, getString(R.string.player_hit), getString(R.string.not_say), getString(R.string.ok_say), buttonClickListener);
        }

    };

    private OnButtonClickListener buttonClickListener = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn == OnButtonClickListener.LIFT) {
                PreferenceManager.getDefaultSharedPreferences(WebPlayerActivity.this).edit().putBoolean("luxian_hit", false).commit();
            }
        }

    };

    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isFinishing() || position < 0 || LUXIANS.length <= position) return;
            String url = String.format(LUXIANS[position], getIntent().getStringExtra(URL));
            mWebview.loadUrl(url);
        }

    };
}
