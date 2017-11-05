package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.apk.ApkEvaluat;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.ShareUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.imovie.view.ContextMenuTitleView;
import com.fanchen.imovie.view.webview.SwipeWebView;
import com.fanchen.zzplayer.view.VideoPlayer;
import com.xigua.p2p.P2PManager;
import com.xigua.p2p.StorageUtils;

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

    private String url;

    /**
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
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
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        WebView webView = mWebview.getWebView();
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);// 开启DOM
        settings.setAllowFileAccess(true);// 设置支持文件流
        settings.setUseWideViewPort(true);// 调整到适合webview大小
        settings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        settings.setBlockNetworkImage(true);// 提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        settings.setAppCacheEnabled(true);// 开启缓存机制
        settings.setUserAgentString(" Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_0 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient);
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
    public void onClick(View v) {

    }

    /**
     * // 设置web页面 // 如果页面中链接，如果希望点击链接继续在当前browser中响应， //
     * 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
     *
     * @author fanchen
     */
    private WebViewClient webViewClient = new WebViewClient() {

        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            Uri localUri = Uri.parse(url);
            String scheme = localUri.getScheme();
            if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                view.loadUrl(url);
            } else if (scheme.equalsIgnoreCase("xg") && IMovieAppliction.app != null) {
                StorageUtils.init(IMovieAppliction.app);
                P2PManager.getInstance().init(IMovieAppliction.app);
                P2PManager.getInstance().setAllow3G(true);
                DialogUtil.showProgressDialog(WebActivity.this, getString(R.string.loading));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        DialogUtil.closeProgressDialog();
                        VideoPlayerActivity.startActivity(WebActivity.this, url);
                    }
                }, 2000);
            } else {
                SystemUtil.startThreeApp(WebActivity.this, url);
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
            if (getIntent().getStringExtra(URL).equals(mWebview.getWebView().getUrl()) && getIntent().hasExtra(TITLE)) {
                getTitleView().setText(getIntent().getStringExtra(TITLE));
            } else {
                getTitleView().setText(mWebview.getWebView().getTitle());
            }
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

}
