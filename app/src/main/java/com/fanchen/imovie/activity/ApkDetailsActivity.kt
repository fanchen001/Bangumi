package com.fanchen.imovie.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.text.Html
import android.text.TextUtils
import android.view.*
import com.fanchen.imovie.R
import com.fanchen.imovie.adapter.ApkScreenAdapter
import com.fanchen.imovie.base.BaseActivity
import com.fanchen.imovie.base.BaseAdapter
import com.fanchen.imovie.dialog.ShowImagesDialog
import com.fanchen.imovie.entity.apk.*
import com.fanchen.imovie.retrofit.callback.RefreshCallback
import com.fanchen.imovie.retrofit.callback.RetrofitCallback
import com.fanchen.imovie.retrofit.service.MoeapkService
import com.fanchen.imovie.util.AppUtil
import com.fanchen.imovie.util.DisplayUtil
import com.fanchen.imovie.view.CustomEmptyView
import com.fanchen.imovie.view.callback.AppBarStateChangeListener
import com.fanchen.imovie.view.video.SuperPlayerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_apk_details.*
import kotlinx.android.synthetic.main.layout_player_toolbar.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.net.URLEncoder

/**
 * 應用，遊戲詳情頁面
 */
class ApkDetailsActivity : BaseActivity(), View.OnClickListener, BaseAdapter.OnItemClickListener {
    private lateinit var apkItem: ApkItem
    private var mApkDetails: ApkDetails? = null
    private val gson = Gson()
    private lateinit var mApkScreenAdapter: ApkScreenAdapter
    private val playerCallBack = SuperPlayerCallBack()

    private val appBarListener = object : AppBarStateChangeListener() {

        override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {
            if (!spv_video.isStop) return
            if (state == AppBarStateChangeListener.State.EXPANDED) {
                //展开状态
                tv_bar_title_play.visibility = View.INVISIBLE
                tv_bar_title.visibility = View.VISIBLE
                if (spv_video.visibility == View.INVISIBLE) {
                    spv_video.visibility = View.VISIBLE
                    iv_game_cover.visibility = View.GONE
                }
            } else if (state == AppBarStateChangeListener.State.COLLAPSED) {
                //折叠状态
                tv_bar_title_play.visibility = View.VISIBLE
                tv_bar_title.visibility = View.INVISIBLE
            } else {
                //中间状态
                iv_game_cover.visibility = View.VISIBLE
                spv_video.visibility = View.INVISIBLE
            }
        }

    }

    private val detailsCallback = object : RefreshCallback<ApkRoot<ApkDetails>> {

        override fun onStart(enqueueKey: Int) {
            if (progressbar_apk != null)
                progressbar_apk.visibility = View.VISIBLE
            if (cev_empty != null)
                cev_empty.setEmptyType(CustomEmptyView.TYPE_NON)
            if (abl_game != null)
                abl_game.visibility = View.VISIBLE
            if (nsv_game != null)
                nsv_game.visibility = View.VISIBLE
        }

        override fun onFinish(enqueueKey: Int) {
            if (progressbar_apk != null)
                progressbar_apk.visibility = View.GONE
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            if (cev_empty != null)
                cev_empty.setEmptyType(CustomEmptyView.TYPE_ERROR)
            if (abl_game != null)
                abl_game.visibility = View.GONE
            if (nsv_game != null)
                nsv_game.visibility = View.GONE
            showSnackbar(throwable)
        }

        override fun onSuccess(enqueueKey: Int, response: ApkRoot<ApkDetails>?) {
            if (response == null || response.data == null) return
            val data = response.data
            fillViewData(data)
            if (data.hasvideo == ApkDetails.HAS_VIDEO) {
                retrofitManager.enqueue(MoeapkService::class.java, videoCallback, "videoUrl", createBody())
            }
        }

    }

    private val videoCallback = RetrofitCallback<ApkRoot<ApkVideo>> { _, response ->
        if (response == null || response.data == null) return@RetrofitCallback
        fillVideoData(response.data)
    }

    override fun getLayout(): Int {
        return R.layout.activity_apk_details
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        nav_top_bar.title = ""
        setSupportActionBar(nav_top_bar)
        apkItem = intent.getParcelableExtra(APK_ITEM)
        mApkScreenAdapter = ApkScreenAdapter(this, picasso)
        rv_screen.layoutManager = BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.HORIZONTAL, false)
        rv_screen.adapter = mApkScreenAdapter
        spv_video.setNetChangeListener(true)
        spv_video.setShowTopControl(false)
        spv_video.setScaleType(SuperPlayerView.SCALETYPE_FILLPARENT)
        spv_video.setPlayerWH(spv_video.measuredWidth, spv_video.measuredHeight)//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
        //加载数据
        retrofitManager.enqueue(MoeapkService::class.java, detailsCallback, "details", createBody())
    }

    override fun setListener() {
        super.setListener()
        cev_empty.setOnClickListener(this)
        tv_app_more_info.setOnClickListener(this)
        iv_bar_back.setOnClickListener(this)
        fab_apk_play.setOnClickListener(this)
        tv_bar_title_play.setOnClickListener(this)
        fab_apk_download.setOnClickListener(this)
        spv_video.onComplete(playerCallBack)
        spv_video.setOnNetChangeListener(playerCallBack)
        spv_video.setOnPlayStateChangeListener(playerCallBack)
        mApkScreenAdapter.setOnItemClickListener(this)
        (nav_top_bar.parent.parent as AppBarLayout).addOnOffsetChangedListener(appBarListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_apk, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_translate -> {
            }
            R.id.action_share -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 创建请求参数
     *
     * @return
     */
    private fun createBody(): RequestBody {
        val data = gson.toJson(ApkParamData(apkItem.packagename))
        val user = gson.toJson(ApkParamUser())
        val format = String.format("data=%s&user=%s", URLEncoder.encode(data), URLEncoder.encode(user))
        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), format)
    }

    /**
     * 设置推广视频信息
     *
     * @param video
     */
    private fun fillVideoData(video: ApkVideo) {
        if (fab_apk_play == null || spv_video == null) return
        //有推广视频
        fab_apk_play.visibility = View.VISIBLE
        spv_video.playUrl(video.best)//设置视频的titleName
    }

    /**
     * 填充数据
     *
     * @param details
     */
    private fun fillViewData(details: ApkDetails) {
        this.mApkDetails = details
        tv_bar_title.text = details.title
        spv_video.setTitle(details.title)
        if (!TextUtils.isEmpty(details.cover))
            picasso.load(details.cover)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.image_load_pre)
                    .error(R.drawable.image_load_error).into(iv_game_cover)//背景
        if (!TextUtils.isEmpty(details.ico))
            picasso.load(details.ico).config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.image_load_icon_pre)
                    .error(R.drawable.image_load_icon_error).into(iv_icon)//图标
        tv_bar_title.text = details.title
        tv_title.text = details.title
        tv_intor.text = Html.fromHtml(details.intro)
        tv_app_description.text = Html.fromHtml(details.description)
        tv_app_mark.text = Html.fromHtml(details.recentchanges)
        tv_app_update_time.text = details.updatetime
        tv_app_package_name.text = details.packagename
        mApkScreenAdapter.clear()
        mApkScreenAdapter.addAll(details.screenShots)
    }

    private fun togoMoreInfo() {
        val layoutParams = tv_app_description.layoutParams
        if (tv_app_description.height == DisplayUtil.dip2px(this, DIP_80.toFloat())) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            tv_app_description.layoutParams = layoutParams
            tv_app_more_info.setText(R.string.more_info_sq)
        } else {
            layoutParams.height = DisplayUtil.dip2px(this, DIP_80.toFloat())
            tv_app_description.layoutParams = layoutParams
            tv_app_more_info.setText(R.string.more_info)
        }
    }

    private fun playVideo() {
        if (spv_video == null && TextUtils.isEmpty(spv_video.url)) return
        iv_game_cover.visibility = View.GONE
        if (spv_video.isStop) {
            //开始播放
            spv_video.start()
        } else {
            //从头开始播放
            spv_video.play()//开始播放视频
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cev_empty -> retrofitManager.enqueue(MoeapkService::class.java, detailsCallback, "details", createBody())
            R.id.fab_apk_play -> playVideo()
            R.id.fab_apk_download -> {
                if (mApkDetails == null) return
                val format = String.format("https://api.apk.moe/client/app/downloadApk?package=%s", mApkDetails!!.packagename)
                if (!TextUtils.isEmpty(format)) {
                    if (downloadReceiver.taskExists(format)) {
                        showSnackbar(getString(R.string.task_exists))
                    } else {
                        val apkPath = AppUtil.getApkPath(this)
                        if (TextUtils.isEmpty(apkPath)) return
                        if (!format.startsWith("http") && !format.startsWith("ftp")) return
                        downloadReceiver.load(format).setDownloadPath(apkPath + " /" + mApkDetails!!.packagename + ".apk").start()
                        showSnackbar(getString(R.string.download_add))
                    }
                }
            }
            R.id.iv_bar_back -> finish()
            R.id.tv_bar_title_play -> fab_apk_play.callOnClick()
            R.id.tv_app_more_info -> togoMoreInfo()
        }
    }


    override fun onItemClick(datas: List<*>, v: View, position: Int) {
        if (datas[position] !is ShowImagesDialog.IPhotoImage) return
        ShowImagesDialog.showDialog(this, datas as List<ShowImagesDialog.IPhotoImage>, position)
    }

    /**
     * 下面的这几个Activity的生命状态很重要
     */
    override fun onPause() {
        super.onPause()
        if (spv_video != null) {
            spv_video.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (spv_video != null) {
            spv_video.onResume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (spv_video != null) {
            spv_video.onDestroy()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (spv_video != null) {
            spv_video.onConfigurationChanged(newConfig)
            if (!DisplayUtil.isScreenChange(this)) {
                //全屏播放
                (spv_video.parent as ViewGroup).removeView(spv_video)
                ctl_game.addView(spv_video, 0)
                setContentView(mainView)
            } else if (mainView != null) {
                //非全屏播放
                ctl_game.removeView(spv_video)
                setContentView(spv_video)
            }
        }
    }

    override fun isSwipeActivity(): Boolean {
        return false
    }

    override fun onBackPressed() {
        if (spv_video != null && spv_video.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    private inner class SuperPlayerCallBack : Runnable, SuperPlayerView.OnNetChangeListener, SuperPlayerView.OnPlayStateChangeListener {

        override fun run() {
            //监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
            if (DisplayUtil.isScreenChange(this@ApkDetailsActivity)) {
                //横屏播放完成之后退出横屏
                onBackPressed()
            }
        }

        /**
         * 网络链接监听类
         */
        override fun onWifi() {
            showToast("当前网络环境是WIFI")
        }

        override fun onMobile() {
            showToast("当前网络环境是手机网络")
        }

        override fun onDisConnect() {
            showToast("网络链接断开")
        }

        override fun onNoAvailable() {
            showToast("无网络链接")
        }

        override fun onStateChange(state: Int) {
            if (DisplayUtil.isScreenChange(this@ApkDetailsActivity)) return
            if (state == SuperPlayerView.STATUS_PLAYING) {
                //播放状态下按钮不可见，视图不可以滚动
                fab_apk_play.visibility = View.GONE
                nav_top_bar.visibility = View.GONE
                spv_video.setIsShowBottomControl(true)
                nsv_game.isNestedScrollingEnabled = false
                mainView.isEnabled = false
            } else {
                //暂停等状态下，可以滚动屏幕
                mainView.isEnabled = true
                nsv_game.isNestedScrollingEnabled = true
                fab_apk_play.visibility = View.VISIBLE
                nav_top_bar.visibility = View.VISIBLE
                spv_video.setIsShowBottomControl(false)
            }
        }
    }

    companion object {

        const val DIP_80 = 80

        const val APK_ITEM = "apk"

        fun startActivity(activity: Activity, item: ApkItem) {
            try {
                val intent = Intent(activity, ApkDetailsActivity::class.java)
                intent.putExtra(APK_ITEM, item)
                activity.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
