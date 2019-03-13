package com.fanchen.imovie.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.view.*
import android.widget.AdapterView
import com.fanchen.imovie.IMovieAppliction
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseToolbarActivity
import com.fanchen.imovie.dialog.BaseAlertDialog
import com.fanchen.imovie.dialog.OnButtonClickListener
import com.fanchen.imovie.jsoup.node.Node
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.ShareUtil
import com.fanchen.imovie.util.SystemUtil
import com.fanchen.imovie.util.VideoUrlUtil
import com.fanchen.imovie.view.ContextMenuTitleView
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.TbsVideo
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.xigua.p2p.P2PManager
import com.xunlei.XLManager
import kotlinx.android.synthetic.main.activity_web.*

/**
 * webView自定义浏览器页面
 */
class WebActivity : BaseToolbarActivity(), View.OnClickListener, VideoUrlUtil.OnParseWebUrlListener {
    override val activityTitle: String
        get() = getString(R.string.app_name)
    private var url: String? = null

    private val webChromeClient = object : WebChromeClient() {

        override fun onJsConfirm(webView: WebView?, s: String?, s1: String?, jsResult: JsResult?): Boolean {
            val node = Node(s1)
            val video = node.attr("video", "src")
            val source = node.attr("source", "src")
            val iframe = node.attr("iframe", "src")
            if (!TextUtils.isEmpty(video) || !TextUtils.isEmpty(iframe) || !TextUtils.isEmpty(source)) {
                if (fab_play != null && fab_play!!.visibility != View.VISIBLE) {
                    fab_play!!.visibility = View.VISIBLE
                }
            }
            jsResult!!.cancel()
            return true
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == 100) {
                swv_content.progressBar.visibility = View.GONE
                swv_content.refreshView.isRefreshing = false
            } else {
                if (swv_content.progressBar.visibility == View.GONE)
                    swv_content.progressBar.visibility = View.VISIBLE
                swv_content.progressBar.progress = newProgress
            }
        }

    }


    /**
     * // 设置web页面 // 如果页面中链接，如果希望点击链接继续在当前browser中响应， //
     * 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
     *
     * @author fanchen
     */
    private val webViewClient = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val localUri = Uri.parse(url)
            val scheme = localUri.scheme
            if (scheme!!.equals("http", ignoreCase = true) || scheme.equals("https", ignoreCase = true)) {
                view!!.loadUrl(url)
            } else if (!TextUtils.isEmpty(url) && IMovieAppliction.app != null && (P2PManager.isXiguaUrl(url) || XLManager.isXLUrlNoHttp(url))) {
                VideoPlayerActivity.startActivity(this@WebActivity, url!!)
            } else {
                val title = String.format("网页<%s>想打开本地应用，是否允许？", swv_content!!.webView.originalUrl)
                DialogUtil.showCancelableDialog(this@WebActivity, title, AppButtonClickListener(url!!))
            }
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            if (isFinishing) return
            val stringExtra = intent.getStringExtra(TITLE)
            if (!TextUtils.isEmpty(stringExtra)) {
                mTitleView.text = stringExtra
            } else {
                var title = swv_content!!.webView.title
                if (TextUtils.isEmpty(title)) {
                    title = getString(R.string.app_name)
                }
                mTitleView.text = title
            }
            fab_play!!.visibility = View.GONE
            try {
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }

        // 设置程序的标题为网页的标题
        override fun onPageFinished(view: WebView?, url: String?) {
            if (isFinishing) return
            view!!.setLayerType(View.LAYER_TYPE_NONE, null)
            val stringExtra = intent.getStringExtra(TITLE)
            if (!TextUtils.isEmpty(stringExtra)) {
                mTitleView.text = stringExtra
            } else {
                var title = swv_content!!.webView.title
                if (TextUtils.isEmpty(title)) {
                    title = getString(R.string.app_name)
                }
                mTitleView.text = title
            }
            swv_content!!.webView.settings.blockNetworkImage = false
        }

        // 当load有ssl层的https页面时，如果这个网站的安全证书在Android无法得到认证，
        // WebView就会变成一个空白页，而并不会像PC浏览器中那样跳出一个风险提示框
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
            // 忽略证书的错误继续Load页面内容
            handler.proceed()
        }

        override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
            super.onScaleChanged(view, oldScale, newScale)
            view!!.requestFocus()
            view.requestFocusFromTouch()
        }

    }

    private val sniffingRunnable = Runnable {
        while (!isFinishing && swv_content != null && swv_content!!.webView != null) {
            SystemClock.sleep(3000)
            swv_content!!.loadUrl("javascript:confirm(document.getElementsByTagName('html')[0].innerHTML);")
        }
    }

    private val menuItemHandler = MenuItem.OnMenuItemClickListener { item ->
        when (item.itemId) {
            0 -> {
                SystemUtil.putText2Clipboard(this@WebActivity, url)
                showSnackbar("复制成功")
            }
            1 -> {
            }
            2 -> SystemUtil.startThreeApp(this@WebActivity, url)
            3 -> swv_content!!.webView.loadUrl("https://www.google.com/searchbyimage?image_url=" + url!!)
        }
        true
    }

    private val onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        if (position >= WebPlayerActivity.LUXIANS.size || swv_content == null) return@OnItemClickListener
        val url = swv_content!!.webView.url
        val parserUrl = String.format(WebPlayerActivity.LUXIANS[position], url)
        DialogUtil.showProgressDialog(this@WebActivity, "正在解析视频...")
        val referer = "http://movie.vr-seesee.com/vip"
        VideoUrlUtil.getInstance().setLoadUrl(parserUrl, referer).startParse()
    }

    override fun getLayout(): Int {
        return R.layout.activity_web
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFormat(PixelFormat.TRANSLUCENT)
        super.onCreate(savedInstanceState)
    }

    override fun setListener() {
        super.setListener()
        fab_play!!.setOnClickListener(this)
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        val webView = swv_content!!.webView
        VideoUrlUtil.getInstance().init(this).setParserTime(2 * 1000).setOnParseListener(this).setAutoDestroy(false)
        val webSetting = webView.settings
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.javaScriptEnabled = true
        webSetting.setSupportMultipleWindows(false)
        webSetting.databaseEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.domStorageEnabled = true// 开启DOM
        webSetting.allowFileAccess = true// 设置支持文件流
        webSetting.useWideViewPort = true// 调整到适合webview大小
        webSetting.loadWithOverviewMode = true// 调整到适合webview大小
        webSetting.blockNetworkImage = true// 提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        webSetting.setAppCacheEnabled(true)// 开启缓存机制
        webSetting.setAppCacheMaxSize((64 * 1024 * 1024).toLong())
        webSetting.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        webSetting.setAppCachePath(getDir("cache", Context.MODE_PRIVATE).path)
        webSetting.setGeolocationDatabasePath(getDir("database", Context.MODE_PRIVATE).path)
        webSetting.userAgentString = " Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_0 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.mixedContentMode = 0
        }
        webView.setWebViewClient(webViewClient)
        webView.setWebChromeClient(webChromeClient)
        registerForContextMenu(webView)
        Thread(sniffingRunnable).start()
        if (intent.hasExtra(URL)) {
            swv_content!!.loadUrl(intent.getStringExtra(URL))
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val result = swv_content!!.webView.hitTestResult
        if (result.type == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            url = result.extra
            menu.setHeaderView(ContextMenuTitleView(this, url))
            menu.add(Menu.NONE, 2, 0, "使用第三方程序打开").setOnMenuItemClickListener(menuItemHandler)
            menu.add(Menu.NONE, 0, 1, "复制网址").setOnMenuItemClickListener(menuItemHandler)
        } else if (result.type == WebView.HitTestResult.IMAGE_TYPE || result.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            url = result.extra
            menu.setHeaderView(ContextMenuTitleView(this, url))
            menu.add(Menu.NONE, 0, 0, "复制网址").setOnMenuItemClickListener(menuItemHandler)
            menu.add(Menu.NONE, 1, 1, "保存图片").setOnMenuItemClickListener(menuItemHandler)
            menu.add(Menu.NONE, 3, 2, "使用Google搜索").setOnMenuItemClickListener(menuItemHandler)
        }
    }

    override fun onResume() {
        super.onResume()
        if (swv_content != null) {
            swv_content!!.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (swv_content != null) {
            swv_content!!.onPause()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_web, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> ShareUtil.share(this, swv_content!!.webView.title, swv_content!!.webView.url)
            R.id.action_start -> SystemUtil.startThreeApp(this, swv_content!!.webView.url)
            R.id.action_copy -> {
                SystemUtil.putText2Clipboard(this, swv_content!!.webView.url)
                showToast(getString(R.string.clipboard))
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        if (swv_content != null && swv_content!!.webView != null) {
            swv_content!!.webView.destroy()
        }
        VideoUrlUtil.getInstance().destroy()
        super.onDestroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_play -> {
                val stringArray = resources.getStringArray(R.array.spinner_luxian)
                DialogUtil.showMaterialListDialog(this, stringArray, onItemClickListener)
            }
        }
    }

    override fun onBackPressed() {
        if (swv_content != null && swv_content!!.webView.canGoBack()) {
            swv_content!!.webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onFindUrl(url: String) {
        TbsVideo.openVideo(this@WebActivity, url)
        DialogUtil.closeProgressDialog()
    }

    override fun onError(errorMsg: String) {
        showToast("视频解析错误")
        DialogUtil.closeProgressDialog()
    }

    private inner class AppButtonClickListener(private val inteniUrl: String) : OnButtonClickListener {

        override fun onButtonClick(dialog: BaseAlertDialog<*>, btn: Int) {
            dialog.dismiss()
            if (btn != OnButtonClickListener.RIGHT) return
            SystemUtil.startThreeApp(this@WebActivity, inteniUrl)
        }

    }

    companion object {

        const val URL = "url"
        const val TITLE = "title"

        fun startActivity(context: Context, title: String?, url: String) {
            try {
                val intent = Intent(context, WebActivity::class.java)
                intent.putExtra(URL, url)
                intent.putExtra(TITLE, title)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, url: String) {
            startActivity(context, null, url)
        }
    }
}
