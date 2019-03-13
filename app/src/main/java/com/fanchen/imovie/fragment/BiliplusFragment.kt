package com.fanchen.imovie.fragment

import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import com.fanchen.imovie.R
import com.fanchen.imovie.activity.VideoDetailsActivity
import com.fanchen.imovie.activity.WebActivity
import com.fanchen.imovie.base.BaseFragment
import com.fanchen.imovie.entity.VideoPlayUrls
import com.fanchen.imovie.entity.face.IPlayUrls
import com.fanchen.imovie.retrofit.callback.RefreshCallback
import com.fanchen.imovie.retrofit.service.BiliplusService
import com.fanchen.imovie.util.AppUtil
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.RegularUtil
import kotlinx.android.synthetic.main.fragment_biliplus.*

class BiliplusFragment : BaseFragment(), View.OnClickListener {
    private val callback = object : RefreshCallback<IPlayUrls> {

        override fun onStart(enqueueKey: Int) {
            if (activity == null) return
            DialogUtil.showProgressDialog(activity, getStringFix(R.string.loading))
        }

        override fun onFinish(enqueueKey: Int) {
            DialogUtil.closeProgressDialog()
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            showSnackbar(throwable)
        }

        override fun onSuccess(enqueueKey: Int, response: IPlayUrls?) {
            if (response != null && response.isSuccess && response is VideoPlayUrls && activity != null) {
                val urls = response.urls
                DialogUtil.showMaterialListDialog(activity, getStringFix(R.string.plase_select), urls.keys, OnListListener(urls))
            } else {
                showSnackbar(getStringFix(R.string.get_video_info_error))
            }
        }

    }

    override fun getLayout(): Int {
        return R.layout.fragment_biliplus
    }

    override fun setListener() {
        super.setListener()
        btn_get_info!!.setOnClickListener(this)
        btn_html5_paly!!.setOnClickListener(this)
        btn_download!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val url = getEditTextString(ed_biliplus)
        if (!TextUtils.isEmpty(url) && (RegularUtil.isAllNumric(url) || url.startsWith("http") || url.startsWith("https"))) {
            val split = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val aid = split[split.size - 1].replace("av", "")
            when (v.id) {
                R.id.btn_get_info -> VideoDetailsActivity.startActivity(activity, aid, BiliplusService::class.java.name)
                R.id.btn_download -> retrofitManager!!.enqueue(BiliplusService::class.java, callback, "playUrl", *arrayOf<Any>(aid))
                R.id.btn_html5_paly -> WebActivity.startActivity(activity, String.format("https://www.biliplus.com/video/av%s", aid))
            }
        } else {
            showSnackbar(getStringFix(R.string.av_error_hit))
        }
    }

    inner class OnListListener constructor(private val valus: Map<String, String>?) : AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            if (activity == null || valus == null || valus.isEmpty()) return
            val key = parent.getItemAtPosition(position)
            val s = valus[key]
            val videoPath = AppUtil.getVideoPath(activity)
            if (!TextUtils.isEmpty(s) && !TextUtils.isEmpty(videoPath)) {
                if ((s!!.startsWith("http") || s.startsWith("ftp")) && !downloadReceiver!!.taskExists(s)) {
                    downloadReceiver!!.load(s).setDownloadPath(videoPath + "/" + key + "_" + System.currentTimeMillis() + ".mp4").start()
                    showSnackbar(getStringFix(R.string.download_add))
                } else {
                    showSnackbar(getStringFix(R.string.task_exists))
                }
            } else {
                showSnackbar(getStringFix(R.string.task_exists))
            }
        }

    }

    companion object {

        fun newInstance(): Fragment {
            return BiliplusFragment()
        }
    }
}
