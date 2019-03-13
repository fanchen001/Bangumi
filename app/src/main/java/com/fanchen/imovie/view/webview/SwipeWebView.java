package com.fanchen.imovie.view.webview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fanchen.imovie.R;
import com.tencent.smtt.sdk.WebView;

import java.util.Map;

/**
 * 带下拉刷新的WebView
 *
 * @author fanchen
 */
public class SwipeWebView extends RelativeLayout implements SwipeRefreshLayout.OnRefreshListener, SafeWebView.OnTouchScrollListener {
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
    }

    public SwipeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SwipeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public void setSwipeRefreshEnabled(boolean isEnabled) {
        if (refreshView != null)
            refreshView.setEnabled(isEnabled);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_swipe_webview, this);
        refreshView = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        refreshView.setEnabled(false);
        webView = (SafeWebView) findViewById(R.id.safe_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.pg_load_web);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        refreshView.setColorSchemeColors(typedValue.data);
        webView.setOnTouchScrollListener(this);
        refreshView.setOnRefreshListener(this);
    }

    public void onPause() {
        try {
            webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        try {
            webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
        } catch (Exception e) {
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
        if ("main".equals(Thread.currentThread().getName())) {
            if (mProgressBar.getVisibility() == View.GONE) {
                webView.loadUrl(webView.getUrl());
            }
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    if (mProgressBar.getVisibility() == View.GONE) {
                        webView.loadUrl(webView.getUrl());
                    }
                }
            });
        }

    }

    public void loadUrl(final String url) {
        if ("main".equals(Thread.currentThread().getName())) {
            webView.loadUrl(url);
            count++;
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                    count++;
                }
            });
        }
    }

    public void loadUrl(final String url, final Map<String, String> map) {
        if ("main".equals(Thread.currentThread().getName())) {
            webView.loadUrl(url, map);
            count++;
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url, map);
                    count++;
                }
            });
        }

    }

    @Override
    public void touch(int scrollY) {
        if (scrollY == 0) {
            refreshView.setEnabled(true);
        } else {
            refreshView.setEnabled(false);
        }
    }

    public boolean onBackPressed() {
        if (count == 0) {
            return false;
        }
        if ("main".equals(Thread.currentThread().getName())) {
            count--;
            webView.goBack(); // goBack()表示返回WebView的上一页面
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    count--;
                    webView.goBack(); // goBack()表示返回WebView的上一页面
                }
            });
        }
        return true;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public SwipeRefreshLayout getRefreshView() {
        return refreshView;
    }
}
