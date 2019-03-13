package com.fanchen.imovie.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.fanchen.imovie.R
import com.fanchen.imovie.activity.WebActivity
import com.fanchen.imovie.activity.WebPlayerActivity
import com.fanchen.imovie.adapter.FreeVideoAdapter
import com.fanchen.imovie.base.BaseAdapter
import com.fanchen.imovie.base.BaseFragment
import com.fanchen.imovie.entity.VideoWeb
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.StreamUtil
import com.fanchen.imovie.util.VideoUrlUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.smtt.sdk.TbsVideo
import kotlinx.android.synthetic.main.fragment_free_video.*

class FreeVideoFragment : BaseFragment(), BaseAdapter.OnItemClickListener, View.OnClickListener {
    private var mVideoAdapter: FreeVideoAdapter? = null
    private var mVideoUrlUtil: VideoUrlUtil? = null

    override fun getLayout(): Int {
        return R.layout.fragment_free_video
    }

    override fun setListener() {
        super.setListener()
        btn_search!!.setOnClickListener(this)
        iv_reback!!.setOnClickListener(this)
        mVideoAdapter!!.setOnItemClickListener(this)
    }

    override fun initFragment(savedInstanceState: Bundle?, args: Bundle?) {
        super.initFragment(savedInstanceState, args)
        mVideoUrlUtil = VideoUrlUtil.getInstance().init(activity)
        rlv_web_list!!.layoutManager = BaseAdapter.GridLayoutManagerWrapper(activity, 3)
        mVideoAdapter = FreeVideoAdapter(activity, picasso)
        rlv_web_list!!.adapter = mVideoAdapter
        try {
            val json = String(StreamUtil.stream2bytes(activity.assets.open("free_video.json")))
            val list = Gson().fromJson<List<VideoWeb>>(json, object : TypeToken<List<VideoWeb>>() {

            }.type)
            mVideoAdapter!!.addAll(list)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClick(datas: List<*>, v: View, position: Int) {
        if (datas[position] !is VideoWeb) return
        val video = datas[position] as VideoWeb
        if (!TextUtils.isEmpty(video.url)) {
            WebActivity.startActivity(activity, video.url)
        }
    }

    override fun onClick(v: View) {
        val url = getEditTextString(ed_free_video)
        if (!TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))) {
            when (v.id) {
                R.id.btn_search -> {
                    val position = sp_luxian!!.selectedItemPosition
                    val luxians = WebPlayerActivity.LUXIANS
                    if (mVideoUrlUtil == null || luxians.size <= position) return
                    val videoUrl = String.format(luxians[position], url)
                    DialogUtil.showProgressDialog(activity, "正在解析视频...")
                    val referer = "http://movie.vr-seesee.com/vip"
                    mVideoUrlUtil!!.setParserTime(5 * 1000).setOnParseListener(FreeVideoListener(url))
                    mVideoUrlUtil!!.setLoadUrl(videoUrl, referer).startParse()
                }
                R.id.iv_reback -> WebActivity.startActivity(activity, url)
            }
        } else {
            showSnackbar(getStringFix(R.string.url_error_hit))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mVideoUrlUtil != null) mVideoUrlUtil!!.destroy()
        mVideoUrlUtil = null
    }

    private inner class FreeVideoListener(url: String) : VideoUrlUtil.OnParseWebUrlListener {

        private var url = ""

        init {
            this.url = url
        }

        override fun onFindUrl(videoUrl: String) {
            if (activity == null) return
            TbsVideo.openVideo(activity, videoUrl)
            DialogUtil.closeProgressDialog()
        }

        override fun onError(errorMsg: String) {
            if (sp_luxian == null) return
            val position = sp_luxian!!.selectedItemPosition
            val referer = "http://movie.vr-seesee.com/vip"
            WebPlayerActivity.startActivity(activity, url, referer, position)
            DialogUtil.closeProgressDialog()
        }

    }

    companion object {

        fun newInstance(): Fragment {
            return FreeVideoFragment()
        }
    }
}
