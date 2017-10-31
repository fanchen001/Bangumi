package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.ShareUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.imovie.view.webview.SwipeWebView;

import butterknife.InjectView;

/**
 * 使用浏览器进行视频播放的页面
 * Created by fanchen on 2017/10/3.
 */
public class WebPlayerActivity extends BaseActivity{

    public static final String URL = "url";

    @InjectView(R.id.toolbar_top)
    protected Toolbar mToolbar;
    @InjectView(R.id.swv_play)
    protected SwipeWebView mWebview;

    /**
     * @param context
     * @param url
     */
    public static void startActivity(Context context,String url) {
        Intent intent = new Intent(context, WebPlayerActivity.class);
        intent.putExtra(URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide the status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("play_hit",true)){
            DialogUtil.showMaterialDialog(this, getString(R.string.player_hit),getString(R.string.not_say),getString(R.string.ok_say), new OnButtonClickListener() {
                @Override
                public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
                    dialog.dismiss();
                    if(btn == OnButtonClickListener.LIFT){
                        PreferenceManager.getDefaultSharedPreferences(WebPlayerActivity.this).edit().putBoolean("play_hit",false).commit();
                    }
                }
            });
        }
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
        if(getIntent().hasExtra(URL)){
            mWebview.loadUrl(getIntent().getStringExtra(URL));
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
                SystemUtil.startThreeApp(this,mWebview.getWebView().getUrl());
                break;
            case R.id.action_copy:
                SystemUtil.putText2Clipboard(this,mWebview.getWebView().getUrl());
                showToast(getString(R.string.clipboard));
                break;
            default:
                break;
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

    /**
     * // 设置web页面 // 如果页面中链接，如果希望点击链接继续在当前browser中响应， //
     * 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
     *
     * @author fanchen
     */
    private WebViewClient webViewClient = new WebViewClient() {

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
}
