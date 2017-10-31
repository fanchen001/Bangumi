package com.fanchen.imovie.view.webview;

import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fanchen.imovie.R;

/**
 * 带下拉刷新的WebView
 *
 * @author fanchen
 */
public class SwipeWebView extends RelativeLayout implements SwipeRefreshLayout.OnRefreshListener, SafeWebView.OnTouchScrollListener {

    private Context ctx;
    /**
     * 安全WebView
     */
    private SafeWebView webView;

    private ProgressBar mProgressBar;
    /**
     * 下拉刷新View
     */
    private SwipeRefreshLayout refreshView;

    private int count = 0;

    public SwipeWebView(Context context) {
        super(context);
        initView(context);
        initWebView();
    }

    public SwipeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initWebView();
    }

    public SwipeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
        initWebView();
    }

    private void initView(Context context) {
        ctx = context;
        LayoutInflater.from(context).inflate(R.layout.view_swipe_webview, this);
        refreshView = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        webView = (SafeWebView) findViewById(R.id.safe_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.pg_load_web);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        refreshView.setColorSchemeColors(typedValue.data);
        webView.setOnTouchScrollListener(this);
        refreshView.setOnRefreshListener(this);
    }

    /**
     * 以后扩展，解决WebView各种问题
     */
    private void initWebView() {

        // 问题1：SDK11，开启硬件加速，会导致白屏。 这里取消硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //问题2：基本都需要支持JS
        webView.getSettings().setJavaScriptEnabled(true);


        //问题3：加载任何url，直接跳到系统浏览器去了。覆写下面的函数，可以解决
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                count ++;
                return true;
            }
        });
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebChromeClient(chromeClient);

        //问题4：onPageFinished回调，经常没有被调用。使用 onProgressChange替换
//        webView.setWebChromeClient(new WebChromeClient() {
//
//            @Override
//            public void onProgressChanged(WebView view, final int newProgress) {
//                super.onProgressChanged(view, newProgress);
//                if (newProgress == 100) {
//                    //这里表示页面加载完成
//                }
//            }
//        });


//        //问题5：点击页面内的下载链接，无反应。这里直接监听，跳到系统浏览器去下载
//        webView.setDownloadListener(new DownloadListener() {
//
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
//                                        long contentLength) {
//                // 实现下载的代码
//                Uri uri = Uri.parse(url);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                ctx.startActivity(intent);
//            }
//        });
    }

    public void onPause(){
        try{
            webView.getClass().getMethod("onPause").invoke(webView,  (Object[])null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onResume(){
        try{
            webView.getClass().getMethod("onResume").invoke(webView,  (Object[])null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 设置刷新成功
     */
    public void setRefreshSuccess() {
        refreshView.setRefreshing(false);
    }

    /**
     * 设置刷新失败
     */
    public void setRefreshFail() {
        refreshView.setRefreshing(false);
    }

    /**
     * 设置刷新是否启用
     *
     * @param isEnable
     */
    public void setRefreshEnable(boolean isEnable) {
        webView.setCanPullDown(isEnable);
    }

    /**
     * 设置刷新中回调
     *
     * @param listener
     */
    public void setOnRefreshWebViewListener(SwipeRefreshLayout.OnRefreshListener listener) {
        refreshView.setOnRefreshListener(listener);
    }

    /**
     * 获取WebView
     *
     * @return
     */
    public WebView getWebView() {
        return webView;
    }

    @Override
    public void onRefresh() {
        if(mProgressBar.getVisibility() == View.GONE){
            webView.loadUrl(webView.getUrl());
        }
    }

    private WebChromeClient chromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
                refreshView.setRefreshing(false);
            } else {
                if (mProgressBar.getVisibility() == View.GONE)
                    mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
        }

    };

    public void loadUrl(String url){
        webView.loadUrl(url);
        count ++;
    }

    @Override
    public void touch(int scrollY) {
        if(scrollY == 0){
            refreshView.setEnabled(true);
        }else{
            refreshView.setEnabled(false);
        }
    }

    public boolean onBackPressed() {
        if (count == 0) {
            return false;
        }
        count--;
        webView.goBack(); // goBack()表示返回WebView的上一页面
        return true;
    }
}
