package com.fanchen.imovie.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.*
import android.widget.AdapterView
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseActivity
import com.fanchen.imovie.dialog.OnButtonClickListener
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.ShareUtil
import com.fanchen.imovie.util.SystemUtil
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.activity_web_player.*
import java.util.*

/**
 * 使用浏览器进行视频播放的页面
 */
class WebPlayerActivity : BaseActivity() {
    private val webChromeClient = object : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == 100) {
                swv_play.progressBar.visibility = View.GONE
                swv_play.refreshView.isRefreshing = false
            } else {
                if (swv_play.progressBar.visibility == View.GONE)
                    swv_play.progressBar.visibility = View.VISIBLE
                swv_play.progressBar.progress = newProgress
            }
        }

    }

    /**
     * // 设置web页面 // 如果页面中链接，如果希望点击链接继续在当前browser中响应， //
     * 而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象。
     */
    private val webViewClient = object : WebViewClient() {

        override fun shouldInterceptRequest(webView: WebView?, url: String): WebResourceResponse {
            return if (url.contains("456jjh") || url.contains("jianduankm")) {//
                WebResourceResponse(null, null, null)//含有广告资源屏蔽请求
            } else {
                super.shouldInterceptRequest(webView, url)//正常加载
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val localUri = Uri.parse(url)
            val scheme = localUri.scheme
            if (scheme!!.equals("http", ignoreCase = true) || scheme.equals("https", ignoreCase = true)) {
                view!!.loadUrl(url)
            } else {
                SystemUtil.startThreeApp(this@WebPlayerActivity, url)
            }
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            if (isFinishing) return
            try {
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }

        // 设置程序的标题为网页的标题
        override fun onPageFinished(view: WebView?, url: String?) {
            if (isFinishing) return
            view?.setLayerType(View.LAYER_TYPE_NONE, null)
            view?.settings?.blockNetworkImage = false
        }

        // 当load有ssl层的https页面时，如果这个网站的安全证书在Android无法得到认证，
        // WebView就会变成一个空白页，而并不会像PC浏览器中那样跳出一个风险提示框
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
            // 忽略证书的错误继续Load页面内容
            handler.proceed()
        }

        override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
            super.onScaleChanged(view, oldScale, newScale)
            view?.requestFocus()
            view?.requestFocusFromTouch()
        }

    }


    private val tipRunnable = Runnable {
        if (isFinishing) return@Runnable
        DialogUtil.showCancelableDialog(this@WebPlayerActivity, getString(R.string.player_hit), getString(R.string.not_say), getString(R.string.ok_say), buttonClickListener)
    }

    private val buttonClickListener = OnButtonClickListener { dialog, btn ->
        dialog.dismiss()
        if (btn == OnButtonClickListener.LIFT) {
            PreferenceManager.getDefaultSharedPreferences(this@WebPlayerActivity).edit().putBoolean("luxian_hit", false).apply()
        }
    }

    private val clickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        if (isFinishing || position < 0 || LUXIANS.size <= position) return@OnItemClickListener
        val url = String.format(LUXIANS[position], intent.getStringExtra(URL))
        swv_play!!.loadUrl(url)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //hide the status bar
        window.setFormat(PixelFormat.TRANSLUCENT)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
    }

    override fun getLayout(): Int {
        return R.layout.activity_web_player
    }

    override fun isSwipeActivity(): Boolean {
        return false
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        setSupportActionBar(toolbar_top)
        toolbar_top.title = ""
        val webView = swv_play!!.webView
        val data = Bundle()
        data.putBoolean("standardFullScreen", true)//true表示标准全屏，false表示X5全屏；不设置默认false，
        data.putInt("DefaultVideoScreen", 2)//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
        if (webView.x5WebViewExtension != null) {
            webView.x5WebViewExtension.invokeMiscMethod("setVideoParams", data)
        }
        val settings = webView.settings
        settings.domStorageEnabled = true// 开启DOM
        settings.allowFileAccess = true// 设置支持文件流
        settings.useWideViewPort = true// 调整到适合webview大小
        settings.loadWithOverviewMode = true// 调整到适合webview大小
        settings.blockNetworkImage = true// 提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        settings.setAppCacheEnabled(true)// 开启缓存机制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = 0
        }
        settings.javaScriptEnabled = true
        webView.setWebViewClient(webViewClient)
        webView.setWebChromeClient(webChromeClient)
        registerForContextMenu(webView)
        if (intent.hasExtra(LUXIAN) && intent.hasExtra(URL)) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("luxian_hit", true)) {
                Handler(Looper.getMainLooper()).postDelayed(tipRunnable, 1500)
            }
            val url = String.format(LUXIANS[intent.getIntExtra(LUXIAN, 0)], intent.getStringExtra(URL))
            if (intent.hasExtra(REFERER)) {
                val map = HashMap<String, String>()
                map["Referer"] = intent.getStringExtra(REFERER)
                map["User-Agent"] = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"
                map["Accept"] = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
                map["Accept-Encoding"] = "gzip, deflate"
                swv_play!!.loadUrl(url, map)
            } else {
                swv_play!!.loadUrl(url)
            }
        } else if (intent.hasExtra(URL)) {
            if (intent.hasExtra(REFERER)) {
                val map = HashMap<String, String>()
                map["Referer"] = intent.getStringExtra(REFERER)
                map["User-Agent"] = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36"
                map["Accept"] = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
                map["Accept-Encoding"] = "gzip, deflate"
                swv_play!!.loadUrl(intent.getStringExtra(URL), map)
            } else {
                swv_play!!.loadUrl(intent.getStringExtra(URL))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (swv_play != null && swv_play!!.webView != null) {
            swv_play!!.webView.destroy()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (intent.hasExtra(LUXIAN)) {
            menuInflater.inflate(R.menu.menu_luxian, menu)
        } else {
            menuInflater.inflate(R.menu.menu_web, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (intent.hasExtra(LUXIAN)) {
            when (item.itemId) {
                R.id.action_luxian -> DialogUtil.showMaterialListDialog(this, resources.getStringArray(R.array.spinner_luxian), clickListener)
            }
        } else {
            when (item.itemId) {
                R.id.action_share -> ShareUtil.share(this, swv_play!!.webView.title, swv_play!!.webView.url)
                R.id.action_start -> SystemUtil.startThreeApp(this, swv_play!!.webView.url)
                R.id.action_copy -> {
                    SystemUtil.putText2Clipboard(this, swv_play!!.webView.url)
                    showToast(getString(R.string.clipboard))
                }
                else -> {
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun isDoubleFinish(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        if (swv_play != null) {
            swv_play!!.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (swv_play != null) {
            swv_play!!.onPause()
        }
    }

    companion object {

        val LUXIANS = arrayOf("https://api.daidaitv.com/index/?url=%s", "https://api.47ks.com/webcloud/?v=%s", "http://api.bbbbbb.me/playm3u8/?url=%s", "http://yun.baiyug.cn/vip/index.php?url=%s", "http://www.82190555.com/index.php?url=%s", "http://movie.vr-seesee.com/jiexi/index.php?url=%s", "http://app.baiyug.cn:2019/vip/?url=%s", "https://api.177537.com/xfsub/?url=%s")

        const val URL = "url"
        const val LUXIAN = "luxian"
        const val REFERER = "referer"

        fun startActivity(context: Context, url: String, luxian: Int) {
            startActivity(context, url, null, luxian)
        }

        @JvmOverloads
        fun startActivity(context: Context, url: String, referer: String? = null, luxian: Int = -1) {
            try {
                val intent = Intent(context, WebPlayerActivity::class.java)
                intent.putExtra(URL, url)
                if (luxian >= 0) {
                    intent.putExtra(LUXIAN, luxian)
                }
                if (!TextUtils.isEmpty(referer)) {
                    intent.putExtra(REFERER, referer)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
