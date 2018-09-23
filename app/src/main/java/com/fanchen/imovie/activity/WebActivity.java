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
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.Video;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.ShareUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.imovie.util.VideoUrlUtil;
import com.fanchen.imovie.view.ContextMenuTitleView;
import com.fanchen.imovie.view.webview.SwipeWebView;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xigua.p2p.P2PManager;


import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;


/**
 * webView自定义浏览器页面
 * Created by fanchen on 2017/8/6.
 */
public class WebActivity extends BaseToolbarActivity implements View.OnClickListener {

    public static final String URL = "url";
    public static final String TITLE = "title";
    @InjectView(R.id.swv_content)
    protected SwipeWebView mWebview;
    @InjectView(R.id.fab_play)
    protected FloatingActionButton mButton;

    private String url;
    private VideoUrlUtil mVideoUrlUtil;
    private List<String> mVideoUrls = null;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String title, String url) {
        try {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra(URL, url);
            intent.putExtra(TITLE, title);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String url) {
        startActivity(context, null, url);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_web;
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mButton.setOnClickListener(this);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        WebView webView = mWebview.getWebView();
        mVideoUrls = Arrays.asList(getResources().getStringArray(R.array.video_web));
        mVideoUrlUtil = VideoUrlUtil.getInstance().init(this);
        mVideoUrlUtil.setParserTime(5 * 1000);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setDatabaseEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setDomStorageEnabled(true);// 开启DOM
        webSetting.setAllowFileAccess(true);// 设置支持文件流
        webSetting.setUseWideViewPort(true);// 调整到适合webview大小
        webSetting.setLoadWithOverviewMode(true);// 调整到适合webview大小
        webSetting.setBlockNetworkImage(true);// 提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        webSetting.setAppCacheEnabled(true);// 开启缓存机制
        webSetting.setAppCacheMaxSize(64 * 1024 * 1024);
        webSetting.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        webSetting.setAppCachePath(getDir("cache", Context.MODE_PRIVATE).getPath());
        webSetting.setGeolocationDatabasePath(getDir("database", Context.MODE_PRIVATE).getPath());
        webSetting.setUserAgentString(" Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_0 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(0);
        }
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        registerForContextMenu(webView);
        if (getIntent().hasExtra(URL)) {
            mWebview.loadUrl(getIntent().getStringExtra(URL));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        WebView.HitTestResult result = mWebview.getWebView().getHitTestResult();
        if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            url = result.getExtra();
            menu.setHeaderView(new ContextMenuTitleView(this, url));
            menu.add(Menu.NONE, 2, 0, "使用第三方程序打开").setOnMenuItemClickListener(menuItemHandler);
            menu.add(Menu.NONE, 0, 1, "复制网址").setOnMenuItemClickListener(menuItemHandler);
        } else if (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            url = result.getExtra();
            menu.setHeaderView(new ContextMenuTitleView(this, url));
            menu.add(Menu.NONE, 0, 0, "复制网址").setOnMenuItemClickListener(menuItemHandler);
            menu.add(Menu.NONE, 1, 1, "保存图片").setOnMenuItemClickListener(menuItemHandler);
            menu.add(Menu.NONE, 3, 2, "使用Google搜索").setOnMenuItemClickListener(menuItemHandler);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mWebview != null && mWebview.getWebView() != null) {
            mWebview.getWebView().destroy();
        }
        if(mVideoUrlUtil != null){
            mVideoUrlUtil.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_play:
                String[] stringArray = getResources().getStringArray(R.array.spinner_luxian);
                DialogUtil.showMaterialListDialog(this, stringArray, onItemClickListener);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebview != null && mWebview.getWebView().canGoBack()) {
            mWebview.getWebView().goBack();
            return;
        }
        super.onBackPressed();
    }

    private boolean contains(String url) {
        if (mVideoUrls != null && mVideoUrls.size() > 0) {
            for (String tUrl : mVideoUrls) {
                if (url.contains(tUrl)) {
                    return true;
                }
            }
        }
        return false;
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
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri localUri = Uri.parse(url);
            String scheme = localUri.getScheme();
            if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                view.loadUrl(url);
            } else if (scheme.equalsIgnoreCase("xg") && IMovieAppliction.app != null) {
                P2PManager.getInstance().init(IMovieAppliction.app);
                P2PManager.getInstance().setAllow3G(true);
                DialogUtil.showProgressDialog(WebActivity.this, getString(R.string.loading));
                mHandler.postDelayed(new XiguaRunnable(url), 2000);
            } else {
                String title = String.format("网页<%s>想打开本地应用，是否允许？", mWebview.getWebView().getOriginalUrl());
                DialogUtil.showCancelableDialog(WebActivity.this, title, new AppButtonClickListener(url));
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (isFinishing()) return;
            String stringExtra = getIntent().getStringExtra(TITLE);
            if (!TextUtils.isEmpty(stringExtra)) {
                getTitleView().setText(stringExtra);
            } else {
                String title = mWebview.getWebView().getTitle();
                if (TextUtils.isEmpty(title)) {
                    title = getString(R.string.app_name);
                }
                getTitleView().setText(title);
            }
            mButton.setVisibility(View.GONE);
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
            String stringExtra = getIntent().getStringExtra(TITLE);
            if (!TextUtils.isEmpty(stringExtra)) {
                getTitleView().setText(stringExtra);
            } else {
                String title = mWebview.getWebView().getTitle();
                if (TextUtils.isEmpty(title)) {
                    title = getString(R.string.app_name);
                }
                getTitleView().setText(title);
            }
            mButton.setVisibility(contains(url) ? View.VISIBLE : View.GONE);
            mWebview.getWebView().getSettings().setBlockNetworkImage(false);
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

    private MenuItem.OnMenuItemClickListener menuItemHandler = new MenuItem.OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case 0:
                    SystemUtil.putText2Clipboard(WebActivity.this, url);
                    showSnackbar("复制成功");
                    break;
                case 1:
                    break;
                case 2:
                    SystemUtil.startThreeApp(WebActivity.this, url);
                    break;
                case 3:
                    mWebview.getWebView().loadUrl("https://www.google.com/searchbyimage?image_url=" + url);
                    break;
            }
            return true;
        }
    };

    private class XiguaRunnable implements Runnable {

        private String xiguaUrl = "";

        public XiguaRunnable(String url) {
            this.xiguaUrl = url;
        }

        @Override
        public void run() {
            DialogUtil.closeProgressDialog();
            VideoPlayerActivity.startActivity(WebActivity.this, xiguaUrl);
        }

    }

    private class AppButtonClickListener implements OnButtonClickListener {
        private String inteniUrl;

        public AppButtonClickListener(String inteniUrl) {
            this.inteniUrl = inteniUrl;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn != RIGHT) return;
            SystemUtil.startThreeApp(WebActivity.this, inteniUrl);
        }

    }

    private class WebUrlListener implements VideoUrlUtil.OnParseWebUrlListener {
        private String rawUrl = "";
        private int position = 0;

        public WebUrlListener(String rawUrl, int position) {
            this.rawUrl = rawUrl;
            this.position = position;
        }

        @Override
        public void onFindUrl(String videoUrl) {
            TbsVideo.openVideo(WebActivity.this, videoUrl);
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onError(String errorMsg) {
            String referer = "http://movie.vr-seesee.com/vip";
            WebPlayerActivity.startActivity(WebActivity.this, rawUrl, referer, position);
            DialogUtil.closeProgressDialog();
        }

    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position >= WebPlayerActivity.LUXIANS.length || mWebview == null) return;
            String url = mWebview.getWebView().getUrl();
            String parserUrl = String.format(WebPlayerActivity.LUXIANS[position], url);
            DialogUtil.showProgressDialog(WebActivity.this, "正在解析视频...");
            String referer = "http://movie.vr-seesee.com/vip";
            mVideoUrlUtil.setOnParseListener(new WebUrlListener(parserUrl,position));
            mVideoUrlUtil.setLoadUrl(parserUrl, referer).startParse();
        }

    };

}
