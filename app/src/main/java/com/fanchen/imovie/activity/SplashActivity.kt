package com.fanchen.imovie.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.listener.FindListener
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.inf.IEntity
import com.fanchen.imovie.IMovieAppliction
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseActivity
import com.fanchen.imovie.dialog.PermissionDialog
import com.fanchen.imovie.entity.JsonSerialize
import com.fanchen.imovie.entity.bmob.SplashScreen
import com.fanchen.imovie.thread.AsyTaskQueue
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl
import com.fanchen.imovie.util.AppUtil
import com.fanchen.imovie.util.FileUtil
import com.fanchen.imovie.util.ImageUtil
import com.fanchen.imovie.util.LogUtil
import kotlinx.android.synthetic.main.activity_splash.*
import java.io.File
import java.util.*

/**
 * 首页闪屏界面
 */
class SplashActivity : BaseActivity() {
    private var mBitmap: Bitmap? = null
    private var preferences: SharedPreferences? = null

    @SuppressLint("HandlerLeak")
    var mHandler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                LOAD_SUCCESS ->
                    // 时间到了，加载主界面
                    loadMainUI()
                LOAD_TOP_IMAGE -> if (mBitmap != null) {
                    // 加载顶部图片
                    iv_splash_top.setBackgroundDrawable(ImageUtil.bitmapToDrawable(mBitmap))
                } else {
                    // 加载顶部图片
                    mBitmap = ImageUtil.readBitMap(this@SplashActivity, R.drawable.bg_start_top)
                    iv_splash_top.setBackgroundDrawable(ImageUtil.bitmapToDrawable(mBitmap))
                }
            }
        }
    }

    private val clickListener = View.OnClickListener {
        val permission = checkPermission()
        if (permission != null && permission.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(permission, 1024)
            }
        }
    }

    private val databaseListener = object : AsyTaskListenerImpl<Void>() {

        override fun onTaskBackground(): Void? {
            LogUtil.e("AsyTaskListener", "需要删除之前的缓存数据")
            val l = System.currentTimeMillis()
            liteOrm!!.delete(JsonSerialize::class.java)
            LogUtil.e("AsyTaskListener", "删除成功,耗时 => " + (System.currentTimeMillis() - l))
            return super.onTaskBackground()
        }

    }

    private val taskListener = object : AsyTaskListenerImpl<List<DownloadEntity>>() {

        override fun onTaskBackground(): List<DownloadEntity>? {
            if (appliction == null || downloadReceiver == null) return null
            val list = ArrayList<DownloadEntity>()
            val simpleTaskList = downloadReceiver!!.taskList
            if (simpleTaskList != null) {
                for (entity in simpleTaskList) {
                    if (entity.state == IEntity.STATE_RUNNING || entity.state == IEntity.STATE_WAIT) {
                        list.add(entity)
                    }
                }
            }
            return list
        }

        override fun onTaskSuccess(simpleTaskList: List<DownloadEntity>?) {
            if (simpleTaskList == null || downloadReceiver == null) return
            for (entity in simpleTaskList) {
                if (!TextUtils.isEmpty(entity.url) || !TextUtils.isEmpty(entity.downloadPath)) {
                    val url = entity.url
                    if (url.startsWith("http") || url.startsWith("ftp")) {
                        downloadReceiver!!.load(entity).start()
                    }
                }
            }
        }

    }

    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater?) {
        super.initActivity(savedState, inflater)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val versionName = AppUtil.getVersionName(this)
        if (preferences != null && preferences!!.getString("app_version", "") != versionName) {
            preferences!!.edit().putString("app_version", versionName).apply()
            AsyTaskQueue.newInstance().execute(databaseListener)
        }
        val headerDir = AppUtil.getExternalHeaderDir(this)
        if (headerDir != null && headerDir.exists()) {
            val file = File(headerDir.absolutePath + IMAGE_SPLASH)
            if (file.exists()) {
                mBitmap = ImageUtil.readBitMap(file.absolutePath)
            }
            if (preferences != null && preferences!!.getBoolean("main_check", true)) {
                BmobQuery<SplashScreen>().findObjects(this, SplashScreenListener(file, preferences))
            }
        }
        if (preferences != null && preferences!!.getBoolean("auto_download", true)) {
            //开启未完成任务自动下载
            downloadReceiver!!.resumeAllTask()
            AsyTaskQueue.newInstance().execute(taskListener)
        }
        if (Build.VERSION.SDK_INT >= 23) {
            val checkPermission = checkPermission()
            if (checkPermission == null || checkPermission.isEmpty()) {
                // 延时1s加载top图片
                mHandler.sendEmptyMessageDelayed(LOAD_TOP_IMAGE, 1000)
                // 延时3s加载主界面
                mHandler.sendEmptyMessageDelayed(LOAD_SUCCESS, 3500)
            } else {
                if (!isFinishing) {
                    PermissionDialog(this).setOnClickListener(clickListener).show()
                }
            }
        } else {
            // 延时1s加载top图片
            mHandler.sendEmptyMessageDelayed(LOAD_TOP_IMAGE, 1000)
            // 延时3s加载主界面
            mHandler.sendEmptyMessageDelayed(LOAD_SUCCESS, 3500)
        }
    }

    override fun isSwipeActivity(): Boolean {
        return false
    }

    /**
     * 载入主页面,如果是第一次安装程序，则先加载欢迎界面
     */
    private fun loadMainUI() {
        val versionName = AppUtil.getVersionName(application)
        if (preferences != null && preferences!!.getString(APP_VERSION, "") != versionName) {
            preferences!!.edit().putString(APP_VERSION, versionName).apply()
            // 如果是第一次进入页面加载引导界面
            MainActivity.startActivity(this)
        } else {
            // 如果不是，加载主界面
            MainActivity.startActivity(this)
        }
        finish()
    }

    /**
     * @return
     */
    private fun checkPermission(): Array<String>? {
        val localArrayList = ArrayList<String>()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != 0)
            localArrayList.add(Manifest.permission.READ_PHONE_STATE)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0)
            localArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (localArrayList.size > 0) {
            val arrayOfString = arrayOf("")
            localArrayList.toTypedArray()
            return arrayOfString
        }
        return null
    }

    private fun checkPermissionsResult(paramArrayOfInt: IntArray?): Boolean {
        if (paramArrayOfInt == null || paramArrayOfInt.isEmpty()) return true
        val j = paramArrayOfInt.size
        var i = 0
        while (i < j) {
            if (paramArrayOfInt[i] == -1)
                return false
            i += 1
        }
        return true
    }

    override fun onRequestPermissionsResult(paramInt: Int, paramArrayOfString: Array<String>, paramArrayOfInt: IntArray) {
        super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt)
        if (paramInt == 1024 && checkPermissionsResult(paramArrayOfInt)) {
            loadMainUI()
            return
        }
        showToast("应用缺少必要的权限！请点击\"权限\"，打开所需要的所有权限。")
        try {
            val mIntent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
            mIntent.data = Uri.parse("package:$packageName")
            startActivity(mIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            finish()
        }
    }

    private class SplashScreenListener internal constructor(private val file: File, private val preferences: SharedPreferences?) : FindListener<SplashScreen>() {

        override fun onSuccess(list: List<SplashScreen>?) {
            if (list == null || list.isEmpty() || preferences == null) return
            val timeMillis = System.currentTimeMillis()
            val version = preferences.getInt(IMAGE_VERSION, 0)
            for (splashScreen in list) {
                IMovieAppliction.KANKAN_COOKIE = splashScreen.kankanCookie
                IMovieAppliction.ALIPAYS = splashScreen.alipays
                if (splashScreen.startTime < timeMillis && splashScreen.endTime > timeMillis && version < splashScreen.version) {
                    FileUtil.downloadBackgroud(splashScreen.screenImage, file)
                    preferences.edit().putInt(IMAGE_VERSION, splashScreen.version).apply()
                    return
                }
            }
        }

        override fun onError(i: Int, s: String) {
            LogUtil.e(SplashActivity::class.java, "更新封面图片失败")
        }

    }

    companion object {
        // 加载完成
        private const val LOAD_SUCCESS = 3
        // 加载顶部图片
        private const val LOAD_TOP_IMAGE = 2

        private const val APP_VERSION = "app_version"
        private const val IMAGE_VERSION = "image_version"
        private const val IMAGE_SPLASH = "splash.jpg"
    }
}
