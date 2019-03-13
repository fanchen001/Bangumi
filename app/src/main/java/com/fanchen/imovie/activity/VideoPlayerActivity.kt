package com.fanchen.imovie.activity

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.arialyy.aria.core.download.DownloadEntity
import com.fanchen.imovie.R
import com.fanchen.imovie.base.BaseActivity
import com.fanchen.imovie.dialog.BaseAlertDialog
import com.fanchen.imovie.dialog.OnButtonClickListener
import com.fanchen.imovie.entity.VideoPlayUrls
import com.fanchen.imovie.entity.bmob.VideoHistory
import com.fanchen.imovie.entity.dytt.DyttLive
import com.fanchen.imovie.entity.dytt.DyttLiveBody
import com.fanchen.imovie.entity.dytt.DyttLiveUrls
import com.fanchen.imovie.entity.dytt.DyttRoot
import com.fanchen.imovie.entity.face.IPlayUrls
import com.fanchen.imovie.entity.face.IVideo
import com.fanchen.imovie.entity.face.IVideoEpisode
import com.fanchen.imovie.retrofit.RetrofitManager
import com.fanchen.imovie.retrofit.callback.RefreshCallback
import com.fanchen.imovie.retrofit.service.Dm5Service
import com.fanchen.imovie.retrofit.service.DyttService
import com.fanchen.imovie.thread.AsyTaskQueue
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl
import com.fanchen.imovie.util.DateUtil
import com.fanchen.imovie.util.DialogUtil
import com.fanchen.imovie.util.VideoUrlUtil
import com.fanchen.imovie.view.video_new.NiceVideoManager
import com.fanchen.imovie.view.video_new.NiceVideoPlayer
import com.fanchen.imovie.view.video_new.NiceVideoPlayerController
import com.fanchen.imovie.view.video_new.TxVideoPlayerController
import com.litesuits.orm.db.assit.QueryBuilder
import com.tencent.smtt.sdk.TbsVideo
import com.vbyte.p2p.old.P2PHandler
import com.vbyte.p2p.old.P2PModule
import com.xigua.p2p.P2PManager
import com.xigua.p2p.P2PMessageWhat
import com.xunlei.XLManager
import com.xunlei.downloadlib.XLService
import kotlinx.android.synthetic.main.activity_video_player.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.io.File
import java.util.*


/**
 * 视频播放页面
 */
class VideoPlayerActivity : BaseActivity() {

    private var videoUrl = ""
    private var videoTitle = ""
    private var mVideo: IVideo? = null
    private var mDyttLive: DyttLive? = null
    private var mVideoEpisode: IVideoEpisode? = null
    private var videoState: NiceVideoPlayerController.VideoState? = null
    private var mDefPlayer: String? = ""
    private var mPreferences: SharedPreferences? = null
    private var mPlayerController: TxVideoPlayerController? = null

    private val filter: IntentFilter
        get() {
            val filter = IntentFilter()
            filter.addAction(P2PMessageWhat.P2P_CALLBACK)
            filter.addAction(XLService.GET_PLAY_URL)
            return filter
        }

    //选择清晰程度播放
    private val clarityListener = TxVideoPlayerController.OnClarityListener { videoPlayer, clarity, _ ->
        P2PModule.getInstance().stopPlay()
        val urls = clarity.ext as DyttLiveUrls
        val p2pUrl = urls.p2p_url.replace("p2p://", "")
        if (!TextUtils.isEmpty(p2pUrl)) {
            val payUrl = P2PModule.getInstance().getPlayPath(p2pUrl, 1, 0)
            videoPlayer.setUp(payUrl)
        } else {
            showToast("获取播放地址失败")
        }
    }

    /**
     * 西瓜播放器，迅雷邊下邊播播放广播接受者
     */
    private val mP2pReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (mPlayerController == null) return
            val action = intent.action
            if (XLService.GET_PLAY_URL == action) {
                openVideo(intent.getStringExtra(XLService.DATA), "")
            } else if (P2PMessageWhat.P2P_CALLBACK == action && intent.hasExtra(P2PMessageWhat.PLAY_URL)) {//西瓜視頻播放
                val xigua = intent.getStringExtra(P2PMessageWhat.PLAY_URL)
                val localFile = intent.getBooleanExtra(P2PMessageWhat.LOCAL_FILE, false)
                val segment = Uri.parse(Uri.decode(videoUrl)).lastPathSegment
                openVideo(xigua, segment, if (localFile) "本地文件" else "0.0KB")
            }
        }

    }

    /**
     * RetrofitCallback
     * 電視直播網絡請求回掉
     */
    private val liveCallback = object : RefreshCallback.RefreshCallbackImpl<DyttRoot<DyttLiveBody>>() {

        override fun onStart(enqueueKey: Int) {
            setLoading(true)
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            setLoading(false)
            showToast(throwable)
        }

        override fun onSuccess(enqueueKey: Int, response: DyttRoot<DyttLiveBody>?) {
            if (response != null && response.body != null && response.body.isSuccess) {
                mPlayerController!!.setClarity(response.body.claritys, 0, clarityListener)
            } else {
                showToast(getString(R.string.error_play_conn))
            }
        }

    }

    /**
     * RetrofitCallback
     * 視頻解析網絡請求回掉
     */
    private val callback = object : RefreshCallback.RefreshCallbackImpl<IPlayUrls>() {

        override fun onStart(enqueueKey: Int) {
            setLoading(true)
        }

        override fun onFailure(enqueueKey: Int, throwable: String) {
            setLoading(false)
            showToast(throwable)
        }

        override fun onSuccess(enqueueKey: Int, response: IPlayUrls?) {
            if (response != null && response.isSuccess) {
                openVideo(response, response.mainUrl)
            } else {
                showToast(getString(R.string.error_play_conn))
            }
        }

    }

    override fun isSwipeActivity(): Boolean {
        return false
    }

    override fun getLayout(): Int {
        return R.layout.activity_video_player
    }

    override fun initActivity(savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(savedState, inflater)
        val intent = intent
        registerReceiver(mP2pReceiver, filter)

        P2PModule.getInstance().setP2PHandler(P2PHandler())

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        nice_video_player!!.setPlayerType(getDefaultPlayer(intent, mPreferences!!))
        nice_video_player!!.setController(TxVideoPlayerController(this))
        nice_video_player!!.setActivityFullScreen(true, true)
        mPlayerController!!.setLoadingVisible(View.VISIBLE)
        videoState = savedState?.getParcelable(STATE_VIDEO)
        if (savedState != null && !TextUtils.isEmpty(videoState!!.url)) {
            val listener = OnPlayerErrorListener(videoState!!.url, videoState!!.referer)
            mPlayerController!!.videoState = videoState
            nice_video_player!!.setOnErrorListener(listener)
            if (!TextUtils.isEmpty(videoUrl)) nice_video_player!!.setPlayerType(NiceVideoPlayer.TYPE_IJK)//西瓜视频用自带播放器放不了，只能用ijk
        } else {//加载网络数据
            getVideoData(intent)
            loadVideo(intent)
            if (mPreferences!!.getBoolean("video_src_hit", true)) {
                val listener = PlayerHitListener()
                mainView.postDelayed(listener, 2000)
            }
        }
    }

    override fun onSaveInstanceState(arg0: Bundle) {
        if (mPlayerController != null) {
            videoState = mPlayerController!!.videoState
            if (videoState != null) arg0.putParcelable(STATE_VIDEO, videoState)
        }
        super.onSaveInstanceState(arg0)
    }

    /**
     * 獲取播放數據
     *
     * @param data
     */
    private fun getVideoData(data: Intent) {
        mVideo = data.getParcelableExtra(VIDEO)
        videoUrl = data.getStringExtra(VIDEO_URL)
        videoTitle = data.getStringExtra(VIDEO_TITLE)
        mDyttLive = intent.getParcelableExtra(DYTT_LIVE)
        if (data.hasExtra(VIDEO_HISTORY)) {
            mVideoEpisode = data.getParcelableExtra(VIDEO_HISTORY)
        } else if (data.hasExtra(VIDEO_EPISODE)) {
            mVideoEpisode = data.getParcelableExtra(VIDEO_EPISODE)
        }
    }

    /**
     * 获取默认的播放器
     *
     * @param data
     * @param preferences
     * @return
     */
    private fun getDefaultPlayer(data: Intent, preferences: SharedPreferences): Int {
        mDefPlayer = preferences.getString("defPlayer", IJK)
        return if (IJK == mDefPlayer || data.hasExtra(FILE_VIDEO) || data.hasExtra(LOCAL_VIDEO)
                || mVideoEpisode != null && mVideoEpisode!!.downloadState == IVideoEpisode.DOWNLOAD_SUCCESS) {
            NiceVideoPlayer.TYPE_IJK//本地文件用native播放时间不对，所以只能用ijk
        } else {
            NiceVideoPlayer.TYPE_NATIVE
        }
    }

    /**
     * 加載播放數據
     * 播放历史记录,剧集跳转过来的
     *
     * @param data
     */
    private fun loadVideo(data: Intent) {
        val retrofit = retrofitManager
        if (data.hasExtra(FILE_VIDEO)) {//M3u8 下载列表跳转过来的
            val file = data.getSerializableExtra(FILE_VIDEO) as File
            openVideo(file.absolutePath, file.name, "本地文件")
        } else if (data.hasExtra(LOCAL_VIDEO)) {//视频下载列表跳转过来的
            val entity = data.getParcelableExtra<DownloadEntity>(LOCAL_VIDEO)
            openVideo(entity.downloadPath, entity.fileName, "本地文件")
        } else if (data.hasExtra(VIDEO_URL) && XLManager.isXLUrlNoHttp(videoUrl)) {//迅雷视频
            XLManager.get(this).addAndPlay(videoUrl)
        } else if (data.hasExtra(VIDEO_URL) && P2PManager.isXiguaUrl(videoUrl)) {//西瓜视频
            P2PManager.getInstance().play(videoUrl)
        } else if (data.hasExtra(VIDEO_URL) && data.hasExtra(VIDEO_TITLE)) {//直接播放的url
            openVideo(videoUrl, videoTitle)
        } else if (data.hasExtra(VIDEO_URL)) {//直接播放的url
            val parse = Uri.parse(Uri.decode(videoUrl))
            openVideo(videoUrl, parse.lastPathSegment)
        } else if (mVideoEpisode != null && mVideoEpisode!!.downloadState == IVideoEpisode.DOWNLOAD_SUCCESS) {//已经下载完成了
            openVideo(mVideoEpisode!!.url, mVideoEpisode!!.title, "本地文件")
        } else if (data.hasExtra(DYTT_LIVE)) {//电视直播
            mPlayerController!!.setTitle(mDyttLive!!.title)
            val contentId = mDyttLive!!.contentId
            val l = System.currentTimeMillis()
            retrofit.enqueue(DyttService::class.java, liveCallback, "livesInfo", contentId, "6560", l)
        } else if (data.hasExtra(VIDEO) && !data.hasExtra(VIDEO_EPISODE)) {//169秀跳转过来的
            mPlayerController!!.setTitle(mVideo!!.title)
            RetrofitManager.REQUEST_URL = mVideo!!.url
            val aClass = mVideo!!.serviceClass
            retrofit.enqueue(aClass, callback, "playUrl", mVideo!!.url)
        } else if (data.hasExtra(VIDEO_EPISODE) || data.hasExtra(VIDEO_HISTORY)) {
            val id = mVideoEpisode!!.id
            RetrofitManager.REQUEST_URL = mVideoEpisode!!.url
            val serviceClass = mVideoEpisode!!.serviceClassName
            if (mVideoEpisode!!.playerType == IVideoEpisode.PLAY_TYPE_URL && Dm5Service::class.java.name == serviceClass) {
                mPlayerController!!.setTitle(mVideoEpisode!!.title)
                val split = id.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()//五弹幕需要特殊处理
                retrofit.enqueue(serviceClass, callback, "playUrl", split[0], split[1].replace("link=", ""))
            } else if (mVideoEpisode!!.playerType == IVideoEpisode.PLAY_TYPE_URL) {
                mPlayerController!!.setTitle(mVideoEpisode!!.title)
                retrofit.enqueue(serviceClass, callback, "playUrl", id)//联网获取播放地址
            } else if (mVideoEpisode!!.playerType == IVideoEpisode.PLAY_TYPE_XUNLEI) {
                videoUrl = mVideoEpisode!!.url
                openVideo(mVideoEpisode!!.toPlayUrls(IVideoEpisode.PLAY_TYPE_XUNLEI, IPlayUrls.URL_FILE), videoUrl)
            } else if (mVideoEpisode!!.playerType == IVideoEpisode.PLAY_TYPE_VIDEO_M3U8) {
                openVideo(mVideoEpisode!!.url, mVideoEpisode!!.title)//直接M3U8播放地址
            } else if (mVideoEpisode!!.playerType == IVideoEpisode.PLAY_TYPE_VIDEO) {
                openVideo(mVideoEpisode!!.url, mVideoEpisode!!.title)//直接视频播放地址
            } else if (mVideoEpisode!!.playerType == IVideoEpisode.PLAY_TYPE_VIDEO_WEB) {
                openVideo(mVideoEpisode!!.toPlayUrls(IVideoEpisode.PLAY_TYPE_WEB, IPlayUrls.URL_WEB), mVideoEpisode!!.url)
            } else {
                showToast(getString(R.string.error_video_type))
            }
        } else {
            showToast(getString(R.string.error_video_type))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mP2pReceiver)
        NiceVideoManager.instance().release()
        P2PModule.getInstance().stopPlay()
        VideoUrlUtil.getInstance().destroy()
        if (mPlayerController != null) mPlayerController!!.release()
        savePlayHistory(if (nice_video_player == null) 0 else nice_video_player!!.currentPosition)
    }

    override fun onResume() {
        super.onResume()
        NiceVideoManager.instance().resume()
    }

    override fun onPause() {
        super.onPause()
        NiceVideoManager.instance().pause()
    }

    override fun onBackPressed() {
        if (NiceVideoManager.instance().onBackPressd()) return
        super.onBackPressed()
    }

    /**
     * 開始播放
     *
     * @param videoUrl
     * @param title
     * @param speed
     */
    private fun openVideo(videoUrl: String, title: String?, speed: String = "") {
        openVideo(videoUrl, "", title, speed)
    }

    /**
     * 開始播放
     *
     * @param videoUrl
     * @param referer
     * @param title
     * @param speed
     */
    private fun openVideo(videoUrl: String, referer: String, title: String?, speed: String) {
        if (TBS == mDefPlayer && !P2PManager.isXiguaUrl(videoUrl) && !XLManager.isXLUrlNoHttp(videoUrl)) {//TBS播放
            TbsVideo.openVideo(this, videoUrl)//西瓜视频不能用TBS播放
            this@VideoPlayerActivity.finish()
        } else {//西瓜视频用自带播放器放不了，只能用ijk
            nice_video_player!!.setOnErrorListener(OnPlayerErrorListener(videoUrl, referer))
            if (P2PManager.isXiguaUrl(videoUrl))
                nice_video_player!!.setPlayerType(NiceVideoPlayer.TYPE_IJK)
            mPlayerController!!.updateSpeed(speed)
            mPlayerController!!.setTitle(title)
            nice_video_player!!.setUp(videoUrl, referer)
        }
    }

    /**
     * 開始播放
     *
     * @param playUrls
     * @param url
     */
    private fun openVideo(playUrls: IPlayUrls, url: String) {
        val playType = playUrls.playType
        if (playType == IVideoEpisode.PLAY_TYPE_XIGUA || P2PManager.isXiguaUrl(url)) {//西瓜视频
            P2PManager.getInstance().play(url)
        } else if (playType == IVideoEpisode.PLAY_TYPE_XUNLEI || XLManager.isXLUrlNoHttp(url)) {//迅雷视频
            XLManager.get(this).addAndPlay(url)
        } else if (playType == IVideoEpisode.PLAY_TYPE_WEB) {//需要 webview 解析视频链接
            val referer = playUrls.referer
            val parseWebUrl = ParseUrlListener(playUrls)
            val init = VideoUrlUtil.getInstance().init(this, url, referer)
            init.setOnParseListener(parseWebUrl).startParse()
        } else if (playType == IVideoEpisode.PLAY_TYPE_WEB_V) {//横向网页，跳转网页播放
            val title = "该视频需要跳转到原网页下载或播放"
            val listener = ParseUrlListener(url, playType)
            DialogUtil.showCancelableDialog(this, title, listener)
        } else if (playUrls.isDirectPlay) {
            openVideo(url, "")
        } else {
            showToast(getString(R.string.error_video_type))
        }
    }

    /**
     * 保存播放记录
     * savePlayHistory
     */
    private fun savePlayHistory(position: Long) {
        val queue = AsyTaskQueue.newInstance()
        if (mVideo != null && mVideoEpisode != null) {
            queue.execute(SaveTaskListener(VideoHistory(mVideo!!, mVideoEpisode!!, position)))
        } else if (mVideo != null) {
            queue.execute(SaveTaskListener(VideoHistory(mVideo!!, position)))
        }
    }

    /**
     * setLoading
     */
    private fun setLoading(visible: Boolean) {
        if (mPlayerController == null) return
        mPlayerController!!.setLoadingVisible(if (visible) View.VISIBLE else View.GONE)
    }

    /**
     * 播放記錄保存Task
     * SaveTaskListener
     */
    private inner class SaveTaskListener(private val mVideoHistory: VideoHistory?) : AsyTaskListenerImpl<Void>() {

        override fun onTaskBackground(): Void? {
            if (mVideoHistory == null) return null
            val liteOrm = liteOrm ?: return null
            val builder = QueryBuilder(VideoHistory::class.java)
            val query = liteOrm.query(builder.where("cover=?", mVideoHistory.cover))
            if (query == null || query.size == 0) {
                liteOrm.insert(mVideoHistory)
            } else {
                val history = query[0]
                history.time = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss")
                history.playPosition = mVideoHistory.playPosition
                liteOrm.update(history)
            }
            return super.onTaskBackground()
        }

    }

    /**
     * 视频播放提示 Runnable
     * PlayerHitListener
     */
    private inner class PlayerHitListener : Runnable, OnButtonClickListener {

        override fun run() {
            val string = getString(R.string.video_src_hit)
            DialogUtil.showCancelableDialog(this@VideoPlayerActivity, string, "继续提醒", "不要再说了", this)
        }

        override fun onButtonClick(baseAlertDialog: BaseAlertDialog<*>, btn: Int) {
            baseAlertDialog.dismiss()
            if (mPreferences == null || btn != OnButtonClickListener.RIGHT) return
            mPreferences!!.edit().putBoolean("video_src_hit", false).apply()
        }

    }

    /**
     * webview解析視頻鏈接的回掉
     * ParseUrlListener
     */
    private inner class ParseUrlListener : VideoUrlUtil.OnParseWebUrlListener, OnButtonClickListener {

        private var playUrls: IPlayUrls? = null

        constructor(playUrls: IPlayUrls) {
            this.playUrls = playUrls
        }

        constructor(url: String, type: Int) {
            val playUrls = VideoPlayUrls()
            val map = HashMap<String, String>()
            map["標清"] = url
            playUrls.playType = type
            playUrls.urls = map
            playUrls.isSuccess = true
            this.playUrls = playUrls
        }

        override fun onFindUrl(videourl: String) {
            if (nice_video_player == null || playUrls == null) return //解析成功，播放視頻
            openVideo(videourl, playUrls!!.referer, "", "")
        }

        override fun onError(errorMsg: String) {
            if (playUrls == null) return //解析失敗，提示用戶打開網頁播放
            val title = "该视频需要使用网页播放"
            DialogUtil.showCancelableDialog(this@VideoPlayerActivity, title, this)
        }

        override fun onButtonClick(baseAlertDialog: BaseAlertDialog<*>, btn: Int) {
            baseAlertDialog.dismiss()
            if (playUrls == null) return
            val mainUrl = playUrls!!.mainUrl
            if (btn == OnButtonClickListener.RIGHT && playUrls!!.playType == IVideoEpisode.PLAY_TYPE_WEB_V) {
                WebActivity.startActivity(this@VideoPlayerActivity, mainUrl)
            } else if (btn == OnButtonClickListener.RIGHT && playUrls!!.playType == IVideoEpisode.PLAY_TYPE_WEB) {
                val referer = playUrls!!.referer
                WebPlayerActivity.startActivity(this@VideoPlayerActivity, mainUrl, referer)
            }
            this@VideoPlayerActivity.finish()
        }
    }

    /**
     * NiceVideoPlayer 播放視頻錯誤后的回掉方法
     * OnPlayerErrorListener
     */
    private inner class OnPlayerErrorListener(private val url: String, referer: String) : IMediaPlayer.OnErrorListener, OnButtonClickListener {
        private var referer = ""

        init {
            this.referer = referer
        }

        override fun onError(iMediaPlayer: IMediaPlayer, i: Int, i1: Int): Boolean {
            DialogUtil.showOtherPlayerDialog(this@VideoPlayerActivity, this)
            return false
        }

        override fun onButtonClick(baseAlertDialog: BaseAlertDialog<*>, btn: Int) {
            baseAlertDialog.dismiss()
            if (btn == OnButtonClickListener.LIFT) {
                TbsVideo.openVideo(this@VideoPlayerActivity, url)
                this@VideoPlayerActivity.finish()
            } else if (btn == OnButtonClickListener.CENTRE) {
                WebPlayerActivity.startActivity(this@VideoPlayerActivity, url, referer)
                this@VideoPlayerActivity.finish()
            }
        }

    }

    companion object {
        const val VIDEO_URL = "url"
        const val VIDEO_TITLE = "title"
        const val ISLIVE = "islive"
        const val VIDEO_EPISODE = "videoEpisode"
        const val VIDEO = "video"
        const val LOCAL_VIDEO = "localVideo"
        const val FILE_VIDEO = "fileVideo"
        const val VIDEO_HISTORY = "videoHistory"
        const val DYTT_LIVE = "dyttLive"
        const val ORIENTATION = "orientation"
        const val STATE_VIDEO = "stateVideo"

        const val IJK = "ijk"
        const val NATIVE = "native"
        const val TBS = "tbs"

        fun startActivity(context: Context, body: DownloadEntity) {
            try {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(LOCAL_VIDEO, body)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, dyttLive: DyttLive) {
            try {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(DYTT_LIVE, dyttLive)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, file: File) {
            try {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(FILE_VIDEO, file)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, history: VideoHistory) {
            try {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(VIDEO_HISTORY, history as Parcelable)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, url: String, title: String) {
            try {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(VIDEO_URL, url)
                intent.putExtra(VIDEO_TITLE, title)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, video: IVideo, episode: IVideoEpisode) {
            try {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(VIDEO_EPISODE, episode)
                intent.putExtra(VIDEO, video)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, video: IVideo) {
            try {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(VIDEO, video)
                intent.putExtra(ISLIVE, false)
                intent.putExtra(ORIENTATION, true)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun startActivity(context: Context, url: String) {
            try {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(VIDEO_URL, url)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
