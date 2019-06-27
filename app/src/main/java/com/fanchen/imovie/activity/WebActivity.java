package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.jsoup.node.Node;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.ShareUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.imovie.view.ContextMenuTitleView;
import com.fanchen.imovie.view.webview.SwipeWebView;
import com.fanchen.sniffing.SniffingUICallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.x5.SniffingUtil;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xigua.p2p.P2PManager;
import com.xunlei.XLManager;

import java.util.List;

import butterknife.InjectView;


/**
 * webView自定义浏览器页面
 * Created by fanchen on 2017/8/6.
 */
public class WebActivity extends BaseToolbarActivity implements View.OnClickListener, SniffingUICallback {
    public static final String HTMLFLAG = "<SniffingVideo>SniffingVideo</SniffingVideo>";

    public static final String URL = "url";
    public static final String TITLE = "title";
    @InjectView(R.id.swv_content)
    protected SwipeWebView mWebview;
    @InjectView(R.id.fab_play)
    protected FloatingActionButton mButton;

    private String url;
    private Runnable mRunnable = null;

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
        String extra = getIntent().getStringExtra(URL);
        WebView webView = mWebview.getWebView();
        webView.setWebViewClient(new DefaultWebViewClient());
        webView.setWebChromeClient(webChromeClient);
        registerForContextMenu(webView);
        webView.loadUrl(extra);
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
        super.onDestroy();
        SniffingUtil.get().releaseAll();
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

    private WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
            if (s1.contains(HTMLFLAG)) {
                if (mButton != null && mButton.getVisibility() != View.VISIBLE) {
                    Node node = new Node(s1);
                    String video = node.attr("video", "src");
                    String source = node.attr("source", "src");
                    String iframe = node.attr("iframe", "src");
                    if (!video.isEmpty() || !iframe.isEmpty() || !source.isEmpty()) {
                        mButton.setVisibility(View.VISIBLE);
                    }
                }
                jsResult.cancel();
                return true;
            }
            return super.onJsConfirm(webView, s, s1, jsResult);
        }

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

    @Override
    public void onSniffingSuccess(View webView, String webUrl, List<SniffingVideo> videos) {
        if (videos.isEmpty()) return;
        String url = videos.get(0).getUrl();
        TbsVideo.openVideo(WebActivity.this, url);
    }

    @Override
    public void onSniffingError(View webView, String url, int errorCode) {
        showSnackbar("解析视频失败");
    }

    @Override
    public void onSniffingStart(View webView, String url) {
        DialogUtil.showProgressDialog(WebActivity.this, "正在解析视频...");
    }

    @Override
    public void onSniffingFinish(View webView, String url) {
        DialogUtil.closeProgressDialog();
    }


    /**
     * // 设置web页面 // 如果页面中链接，如果希望点击链接继续在当前browser中响应， //
     * 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
     *
     * @author fanchen
     * //
     */
    private class DefaultWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri localUri = Uri.parse(url);
            String scheme = localUri.getScheme();
            if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                view.loadUrl(url);
            } else if (!TextUtils.isEmpty(url) && IMovieAppliction.app != null && (P2PManager.isXiguaUrl(url) || XLManager.isXLUrlNoHttp(url))) {
                VideoPlayerActivity.startActivity(WebActivity.this, url);
            } else {
                String title = String.format("网页<%s>想打开本地应用，是否允许？", mWebview.getWebView().getOriginalUrl());
                DialogUtil.showCancelableDialog(WebActivity.this, title, new AppButtonClickListener(url));
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            TextView titleView = getTitleView();
            if (isFinishing() || titleView == null || mWebview == null) return;
            String stringExtra = getIntent().getStringExtra(TITLE);
            if (!TextUtils.isEmpty(stringExtra)) {
                titleView.setText(stringExtra);
            } else {
                String title = mWebview.getWebView().getTitle();
                if (TextUtils.isEmpty(title)) {
                    title = getString(R.string.app_name);
                }
                titleView.setText(title);
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
            super.onPageFinished(view, url);
            TextView titleView = getTitleView();
            if (isFinishing() || titleView == null || mWebview == null) return;
            view.setLayerType(View.LAYER_TYPE_NONE, null);
            String stringExtra = getIntent().getStringExtra(TITLE);
            if (!TextUtils.isEmpty(stringExtra)) {
                titleView.setText(stringExtra);
            } else {
                String title = mWebview.getWebView().getTitle();
                if (TextUtils.isEmpty(title)) {
                    title = getString(R.string.app_name);
                }
                titleView.setText(title);
            }
            mWebview.getWebView().getSettings().setBlockNetworkImage(false);
            mWebview.removeCallbacks(mRunnable);
            mWebview.postDelayed(mRunnable = new DelayedRunnable(), 4000);
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

    }

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

    private class DelayedRunnable implements Runnable {

        @Override
        public void run() {
            if (mWebview == null) return;
            mWebview.loadUrl("javascript:confirm(document.getElementsByTagName('html')[0].innerHTML + '" + HTMLFLAG + "');");
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

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position >= WebPlayerActivity.LUXIANS.length || mWebview == null) return;
            String url = mWebview.getWebView().getUrl();
            String parserUrl = String.format(WebPlayerActivity.LUXIANS[position], url);
            String referer = "http://movie.vr-seesee.com/vip";
            SniffingUtil.get().activity(WebActivity.this).referer(referer).url(parserUrl).callback(WebActivity.this).start();
        }

    };

}
