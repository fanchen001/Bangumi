package com.fanchen.imovie.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanchen.imovie.R
import com.fanchen.imovie.adapter.EpisodeAdapter
import com.fanchen.imovie.adapter.RecomAdapter
import com.fanchen.imovie.base.BaseActivity
import com.fanchen.imovie.base.BaseAdapter
import com.fanchen.imovie.dialog.DownloadDialog
import com.fanchen.imovie.dialog.OnButtonClickListener
import com.fanchen.imovie.entity.bmob.BmobObj
import com.fanchen.imovie.entity.bmob.DialogBanner
import com.fanchen.imovie.entity.bmob.VideoCollect
import com.fanchen.imovie.entity.face.IVideo
import com.fanchen.imovie.entity.face.IVideoDetails
import com.fanchen.imovie.entity.face.IVideoEpisode
import com.fanchen.imovie.picasso.PicassoWrap
import com.fanchen.imovie.picasso.download.RefererDownloader
import com.fanchen.imovie.picasso.trans.BlurTransform
import com.fanchen.imovie.retrofit.callback.RefreshCallback
import com.fanchen.imovie.thread.AsyTaskQueue
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl
import com.fanchen.imovie.util.*
import com.fanchen.imovie.view.CustomEmptyView
import com.fanchen.m3u8.M3u8Config
import com.fanchen.m3u8.M3u8Manager
import com.fanchen.m3u8.bean.M3u8
import com.fanchen.m3u8.bean.M3u8File
import com.fanchen.m3u8.listener.OnM3u8InfoListener
import com.google.gson.Gson
import com.litesuits.orm.db.assit.QueryBuilder
import com.xigua.p2p.P2PManager
import com.xunlei.XLManager
import kotlinx.android.synthetic.main.activity_video_details.*
import kotlinx.android.synthetic.main.layout_toolbar_video.*
import kotlinx.android.synthetic.main.layout_video_info.*
import java.io.File

/**
 * 视频详情
 */
class VideoDetailsActivity : BaseActivity(), View.OnClickListener, BaseAdapter.OnItemClickListener, DialogInterface.OnDismissListener, DownloadDialog.OnDownloadSelectListener, OnM3u8InfoListener, VideoUrlUtil.OnParseWebUrlListener {

    private var mVideo: IVideo? = null
    private var mVideoCollect: VideoCollect? = null
    private var className: String? = null
    private var vid: String? = null
    private var details: IVideoDetails? = null
    private var picasso: PicassoWrap? = null
    private var mEpisodeAdapter: EpisodeAdapter? = null
    private var mRecomAdapter: RecomAdapter? = null
    private var mVideoUrlUtil: VideoUrlUtil? = null
    private var mDownload: DownloadDialog.DownloadTemp? = null
    private var mDownloads: MutableList<DownloadDialog.DownloadTemp>? = null

    private val callback = object : RefreshCallback<IVideoDetails> {

        override fun onStart(enqueueKey: Int) {
            if (progressbar_apk != null)
                progressbar_apk!!.visibility = View.VISIBLE
            if (cev_empty != null)
                cev_empty.setEmptyType(CustomEmptyView.TYPE_NON)
            if (abl_root != null)
                abl_root.visibility = View.VISIBLE
            if (nsv_root != null)
                nsv_root.visibility = View.VISIBLE
        }

        override fun onFinish(enqueueKey: Int) {
            if (progressbar_apk != null)
                progressbar_apk.visibility = View.GONE
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            if (cev_empty != null)
                cev_empty.setEmptyType(CustomEmptyView.TYPE_ERROR)
            if (abl_root != null)
                abl_root.visibility = View.GONE
            if (nsv_root != null)
                nsv_root.visibility = View.GONE
            showSnackbar(throwable)
        }

        override fun onSuccess(enqueueKey: Int, response: IVideoDetails?) {
            if (response == null || !response.isSuccess || mRecomAdapter == null) {
                onFailure(enqueueKey, "未知錯誤")
                return
            }
            details = if (mVideo == null) if (mVideoCollect == null) response else response.setVideo(mVideoCollect) else response.setVideo(mVideo)
            if (!TextUtils.isEmpty(response.coverReferer)) {
                picasso = PicassoWrap(this@VideoDetailsActivity, RefererDownloader(applicationContext, response.coverReferer))
                mRecomAdapter?.setPicasso(picasso)
            }
            picasso!!.loadVertical(response.cover, VideoDetailsActivity::class.java, false, iv_bangumi_cover)
            if (!TextUtils.isEmpty(response.cover)) {
                picasso!!.picasso.load(response.cover).transform(BlurTransform()).into(iv_bangumi_image)
            }
            tv_bangumi_title.text = response.title
            tv_bangumi_area.text = response.extras
            tv_bangumi_type.text = response.last
            tv_bangumi_info.text = response.introduce
            val episodes = response.episodes
            if (episodes == null || episodes.size == 0) {
                tv_non_episode!!.visibility = View.VISIBLE
            } else {
                tv_non_episode!!.visibility = View.GONE
                mEpisodeAdapter!!.addAll(episodes)
            }
            val recoms = response.recoms
            if (recoms == null || recoms.size == 0) {
                tv_non_recom!!.visibility = View.VISIBLE
            } else {
                tv_non_recom!!.visibility = View.GONE
                mRecomAdapter!!.addAll(recoms)
            }
        }

    }

    /**
     *
     */
    private val buttonClickListener = OnButtonClickListener { dialog, btn ->
        if (btn == OnButtonClickListener.RIGHT && details != null) {
            val count = liteOrm!!.queryCount(QueryBuilder(VideoCollect::class.java).where("id = ?", details!!.id))
            if (count <= 0) {
                val collect = VideoCollect(details!!)
                collect.save(CollectListener(collect))
            } else {
                showSnackbar(getString(R.string.collect_repetition))
            }
        }
        dialog.dismiss()
    }

    override fun getLayout(): Int {
        return R.layout.activity_video_details
    }

    override fun setListener() {
        super.setListener()
        cev_empty!!.setOnClickListener(this)
        ll_bangumi_collect!!.setOnClickListener(this)
        ll_bangumi_share!!.setOnClickListener(this)
        ll_bangumi_download!!.setOnClickListener(this)
        iv_top_back!!.setOnClickListener(this)
        tv_bangumi_more_info!!.setOnClickListener(this)
        tv_bangumi_more_episode!!.setOnClickListener(this)
        mEpisodeAdapter!!.setOnItemClickListener(this)
        mRecomAdapter!!.setOnItemClickListener(this)
        M3u8Manager.registerInfoListeners(this)
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        toolbar_top!!.title = ""
        setSupportActionBar(toolbar_top)
        getIntentData(intent)
        tv_top_title!!.setText(R.string.bangumi_details)
        mVideoUrlUtil = VideoUrlUtil.getInstance().init(this)
        recyclerview_episode!!.layoutManager = BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.HORIZONTAL, false)
        recyclerview_recom!!.layoutManager = BaseAdapter.GridLayoutManagerWrapper(this, 3)
        mEpisodeAdapter = EpisodeAdapter(this)
        recyclerview_episode!!.adapter = mEpisodeAdapter
        recyclerview_episode!!.isNestedScrollingEnabled = false
        mRecomAdapter = RecomAdapter(this, PicassoWrap(getPicasso()))
        recyclerview_recom!!.adapter = mRecomAdapter
        recyclerview_episode!!.isNestedScrollingEnabled = false
        recyclerview_recom!!.isNestedScrollingEnabled = false
        val path = if (mVideo == null) if (mVideoCollect == null) vid else mVideoCollect!!.id else mVideo!!.id
        retrofitManager.enqueue(className, callback, "details", path)
    }

    override fun onDestroy() {
        super.onDestroy()
        M3u8Manager.unregisterInfoListeners(this)
    }

    override fun onDownloadSelect(downloads: MutableList<DownloadDialog.DownloadTemp>?) {
        if (downloads == null || downloads.isEmpty()) return
        this.mDownloads = downloads
        DialogUtil.showProgressDialog(this, getString(R.string.loading))
        download(mDownloads!!.removeAt(0))
    }

    private fun download(temp: DownloadDialog.DownloadTemp?) {
        if (temp == null) return
        this.mDownload = temp
        when {
            this.mDownload!!.type == DownloadDialog.DownloadTemp.TYPE_MP4 -> {
                val fileNmae = details!!.title + "_" + temp.episode.title + ".mp4"
                val eurl = mDownload!!.episode.url
                val path = File(M3u8Config.m3u8Path, fileNmae).absolutePath
                val header = AppUtil.getDownloadHeader()
                downloadReceiver!!.load(temp.url).setExtendField(eurl).setFilePath(path).addHeaders(header).start()
                temp.episode.downloadState = IVideoEpisode.DOWNLOAD_RUN
                showToast(String.format("<%s>添加下载任务成功", fileNmae))
                downloadNext()
            }
            this.mDownload!!.type == DownloadDialog.DownloadTemp.TYPE_XIGUA -> {
                P2PManager.getInstance().play(temp.url)
                temp.episode.downloadState = IVideoEpisode.DOWNLOAD_RUN
                showToast(String.format("<%s>添加下载任务成功", temp.episode.title))
                downloadNext()
            }
            this.mDownload!!.type == DownloadDialog.DownloadTemp.TYPE_XUNLEI -> {
                XLManager.get(this).addTask(temp.url)
                temp.episode.downloadState = IVideoEpisode.DOWNLOAD_RUN
                showToast(String.format("<%s>添加下载任务成功", temp.episode.title))
                downloadNext()
            }
            this.mDownload!!.type == DownloadDialog.DownloadTemp.TYPE_M3U8 -> {
                val m3u8File = M3u8File()
                if (temp.m3u8Url != null && temp.m3u8Url.contains("=") && temp.m3u8Url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].contains(".m3u")) {
                    m3u8File.url = temp.m3u8Url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                } else if (temp.m3u8Url != null) {
                    m3u8File.url = temp.m3u8Url
                }
                m3u8File.onlyId = temp.onlyId
                m3u8File.m3u8VideoName = String.format("%s_%s.mp4", details!!.title, temp.episode.title)
                M3u8Manager.download(m3u8File)
            }
            this.mDownload!!.type == DownloadDialog.DownloadTemp.TYPE_URL -> {
                mVideoUrlUtil!!.setLoadUrl(temp.url, temp.referer)
                mVideoUrlUtil!!.setOnParseListener(this)
                mVideoUrlUtil!!.startParse()
            }
            else -> {
                showToast(String.format("<%s>不支持下载", temp.episode.title))
                downloadNext()
            }
        }
    }

    private fun downloadNext() {
        if (mDownloads != null && mDownloads!!.size > 0) {
            download(mDownloads!!.removeAt(0))
        } else {
            DialogUtil.closeProgressDialog()
            if (mEpisodeAdapter != null) mEpisodeAdapter!!.notifyDataSetChanged()
        }
    }

    private fun getIntentData(data: Intent) {
        if (data.data != null) {
            mVideo = VideoJsonUtil.json2Video(data.data!!.getQueryParameter("info"))
            className = if (mVideo == null) "" else mVideo!!.serviceClass
        }
        if (intent.hasExtra(VIDEO_BANNWE)) {
            mVideo = VideoJsonUtil.json2Video(data.getStringExtra(VIDEO_BANNWE))
            className = if (mVideo == null) "" else mVideo!!.serviceClass
        }
        if (intent.hasExtra(VIDEO)) {
            mVideo = intent.getParcelableExtra(VIDEO)
        }
        if (intent.hasExtra(COLLECT)) {
            mVideoCollect = intent.getParcelableExtra(COLLECT)
        }
        if (intent.hasExtra(VID)) {
            vid = intent.getStringExtra(VID)
        }
        if (intent.hasExtra(CLASS_NAME)) {
            className = intent.getStringExtra(CLASS_NAME)
        }
    }

    override fun onFindUrl(url: String) {
        if (mDownload == null || details == null) return
        val title = mDownload!!.episode.title
        val format = String.format("%s_%s.mp4", details!!.title, title)
        if (url.contains(".m3u")) {
            val m3u8File = M3u8File()
            if (url.contains("=") && url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].contains(".m3u")) {
                m3u8File.url = url.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            } else {
                m3u8File.url = url
            }
            m3u8File.onlyId = mDownload!!.onlyId
            m3u8File.m3u8VideoName = format
            M3u8Manager.download(m3u8File)
        } else if (url.contains(".rm") || url.contains(".mp4") || url.contains(".avi") || url.contains(".wmv")) {
            mDownload!!.episode.downloadState = IVideoEpisode.DOWNLOAD_RUN
            val eurl = mDownload!!.episode.url
            val path = File(M3u8Config.m3u8Path, format).absolutePath
            val header = AppUtil.getDownloadHeader()
            downloadReceiver!!.load(url).setExtendField(eurl).setFilePath(path).addHeaders(header).start()
            showToast(String.format("<%s>添加下载任务成功", format))
            downloadNext()
        } else {
            showToast(String.format("<%s>不支持下载", format))
            downloadNext()
        }
    }

    override fun onError(errorMsg: String) {
        if (mDownload == null || mDownload!!.episode == null || details == null) return
        val format = String.format("%s_%s.mp4", details!!.title, mDownload!!.episode.title)
        showToast(String.format("<%s>解析M3u8任务失败", format))
        downloadNext()
    }

    override fun onSuccess(m3u8File: M3u8File, list: List<M3u8>) {
        if (mDownload == null || mDownload!!.episode == null) return
        mDownload!!.episode.downloadState = IVideoEpisode.DOWNLOAD_RUN
        M3u8Manager.download(list)
        showToast(String.format("<%s>添加下载任务成功", m3u8File.m3u8VideoName))
        downloadNext()
    }

    override fun onError(m3u8File: M3u8File, throwable: Throwable) {
        showToast(String.format("<%s>下载失败", m3u8File.m3u8VideoName))
        downloadNext()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.cev_empty) {
            val path = if (mVideo == null) if (mVideoCollect == null) vid else mVideoCollect!!.id else mVideo!!.id
            retrofitManager.enqueue(className, callback, "details", path)
            return
        } else if (v.id == R.id.iv_top_back) {
            finish()
            return
        }
        if (details == null) return
        when (v.id) {
            R.id.tv_bangumi_more_episode -> if (details!!.episodes == null || details!!.episodes.size == 0) {
                showSnackbar(getString(R.string.non_episode))
            } else {
                EpisodeActivity.startActivity(this, details)
            }
            R.id.ll_bangumi_collect -> if (checkLogin()) {
                val format = String.format(getString(R.string.collect_hit), details!!.title)
                DialogUtil.showMaterialDialog(this, format, buttonClickListener)
            }
            R.id.ll_bangumi_share -> try {
                val manager = packageManager
                val infos = manager.getInstalledApplications(PackageManager.GET_META_DATA)
                if (infos == null || infos.isEmpty()) {
                    showToast("获取可分享应用信息失败,请给应用授权")
                } else {
                    val info = Uri.parse("https://details").buildUpon().appendQueryParameter("info", Gson().toJson(details))
                    ShareUtil.share(this, details!!.title, details!!.introduce, info.toString())
                }
            } catch (e: Throwable) {
                showToast(String.format("分享失败 <%s>", e.toString()))
            }

            R.id.ll_bangumi_download -> if (details!!.canDownload()) {
                DialogUtil.showDownloadDialog(this, details, this)
            } else {
                showSnackbar(getString(R.string.not_download))
            }
            R.id.tv_bangumi_more_info -> {
                val dip2px = DisplayUtil.dip2px(this, 34f)
                val layoutParams = tv_bangumi_info!!.layoutParams
                if (layoutParams.height == dip2px) {
                    tv_bangumi_more_info!!.text = getString(R.string.close_more_jianjie)
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    tv_bangumi_more_info!!.text = getString(R.string.more_jianjie)
                    layoutParams.height = dip2px
                }
                tv_bangumi_info!!.layoutParams = layoutParams
            }
        }
    }

    override fun onItemClick(datas: List<*>, v: View?, position: Int) {
        if (details == null || v == null || v.parent == null) return
        when ((v.parent as View).id) {
            R.id.recyclerview_recom -> {
                if (datas[position] !is IVideo) return
                val iVideo = datas[position] as IVideo
                VideoDetailsActivity.startActivity(this, iVideo, className)
            }
            R.id.recyclerview_episode -> {
                if (datas[position] !is IVideoEpisode) return
                val episode = datas[position] as IVideoEpisode
                when {
                    episode.playerType == IVideoEpisode.PLAY_TYPE_XUNLEI -> VideoPlayerActivity.startActivity(this, episode.url)
                    episode.playerType == IVideoEpisode.PLAY_TYPE_NOT -> showSnackbar(getString(R.string.video_not_play))
                    else -> VideoPlayerActivity.startActivity(this, details!!, episode)
                }
            }
            else -> {
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (mEpisodeAdapter != null) mEpisodeAdapter!!.notifyDataSetChanged()
    }

    private inner class CollectListener(private val collect: VideoCollect) : BmobObj.OnRefreshListener() {

        override fun onStart() {
            if (isFinishing) return
            DialogUtil.showProgressDialog(this@VideoDetailsActivity, getString(R.string.collect_ing))
        }

        override fun onFinish() {
            if (isFinishing) return
            DialogUtil.closeProgressDialog()
        }

        override fun onSuccess() {
            AsyTaskQueue.newInstance().execute(SaveTaskListener(collect))
            if (isFinishing) return
            showSnackbar(getString(R.string.collect_asy_success))
        }

        override fun onFailure(i: Int, s: String) {
            if (isFinishing) return
            showSnackbar(getString(R.string.collect_asy_error))
        }

    }

    private inner class SaveTaskListener(private val collect: VideoCollect) : AsyTaskListenerImpl<Int>() {

        var SUCCESS = 0
        var ERROR = 2

        override fun onTaskBackground(): Int? {
            if (details == null || liteOrm == null) return ERROR
            liteOrm!!.insert(collect)
            return SUCCESS
        }

        override fun onTaskSuccess(data: Int?) {
            if (data == SUCCESS) {
                showSnackbar(getString(R.string.collect_success))
            } else {
                showSnackbar(getString(R.string.collect_error))
            }
        }

    }

    companion object {

        const val VIDEO = "video"
        const val COLLECT = "collect"
        const val VID = "vid"
        const val VIDEO_BANNWE = "video_bannwe"
        const val CLASS_NAME = "className"

        fun startActivity(context: Context, id: String, className: String) {
            try {
                val intent = Intent(context, VideoDetailsActivity::class.java)
                intent.putExtra(VID, id)
                intent.putExtra(CLASS_NAME, className)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        @JvmOverloads
        fun startActivity(context: Context, item: IVideo, className: String? = item.serviceClass) {
            try {
                val intent = Intent(context, VideoDetailsActivity::class.java)
                intent.putExtra(VIDEO, item)
                intent.putExtra(CLASS_NAME, className)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, banner: DialogBanner) {
            try {
                val intent = Intent(context, VideoDetailsActivity::class.java)
                intent.putExtra(VIDEO_BANNWE, banner.baseJson)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, videoCollect: VideoCollect) {
            try {
                val intent = Intent(context, VideoDetailsActivity::class.java)
                intent.putExtra(COLLECT, videoCollect as Parcelable)
                intent.putExtra(CLASS_NAME, videoCollect.serviceClassName)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
