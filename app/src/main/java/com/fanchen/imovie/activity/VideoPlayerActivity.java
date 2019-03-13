package com.fanchen.imovie.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.arialyy.aria.core.download.DownloadEntity;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.VideoPlayUrls;
import com.fanchen.imovie.entity.dytt.DyttLive;
import com.fanchen.imovie.entity.dytt.DyttLiveBody;
import com.fanchen.imovie.entity.dytt.DyttLiveUrls;
import com.fanchen.imovie.entity.dytt.DyttRoot;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.bmob.VideoHistory;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.retrofit.service.Dm5Service;
import com.fanchen.imovie.retrofit.service.DyttService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DateUtil;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.VideoUrlUtil;
import com.fanchen.imovie.view.video_new.Clarity;
import com.fanchen.imovie.view.video_new.INiceVideoPlayer;
import com.fanchen.imovie.view.video_new.NiceVideoManager;
import com.fanchen.imovie.view.video_new.NiceVideoPlayer;
import com.fanchen.imovie.view.video_new.NiceVideoPlayerController;
import com.fanchen.imovie.view.video_new.TxVideoPlayerController;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.tencent.smtt.sdk.TbsVideo;
import com.vbyte.p2p.old.P2PHandler;
import com.vbyte.p2p.old.P2PModule;
import com.xigua.p2p.P2PManager;
import com.xigua.p2p.P2PMessageWhat;
import com.xunlei.XLAppliction;
import com.xunlei.XLManager;
import com.xunlei.downloadlib.XLService;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import me.jessyan.autosize.internal.CustomAdapt;
import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * 视频播放页面
 * Created by fanchen on 2017/8/14.
 */
public class VideoPlayerActivity extends BaseActivity implements CustomAdapt {
    public static final String VIDEO_URL = "url";
    public static final String VIDEO_TITLE = "title";
    public static final String ISLIVE = "islive";
    public static final String VIDEO_EPISODE = "videoEpisode";
    public static final String VIDEO = "video";
    public static final String LOCAL_VIDEO = "localVideo";
    public static final String FILE_VIDEO = "fileVideo";
    public static final String VIDEO_HISTORY = "videoHistory";
    public static final String DYTT_LIVE = "dyttLive";
    public static final String ORIENTATION = "orientation";
    public static final String STATE_VIDEO = "stateVideo";

    public static final String IJK = "ijk";
    public static final String NATIVE = "native";
    public static final String TBS = "tbs";

    @InjectView(R.id.nice_video_player)
    protected NiceVideoPlayer mVideoPlayer;

    private String videoUrl = "";
    private String videoTitle = "";
    private IVideo mVideo;
    private DyttLive mDyttLive;
    private IVideoEpisode mVideoEpisode;
    private NiceVideoPlayerController.VideoState videoState = null;
    private String mDefPlayer = "";
    private SharedPreferences mPreferences;
    private TxVideoPlayerController mPlayerController;

    /**
     * @param context
     * @param body
     */
    public static void startActivity(Context context, DownloadEntity body) {
        try {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(LOCAL_VIDEO, body);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Context context, DyttLive dyttLive) {
        try {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(DYTT_LIVE, dyttLive);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Context context, File file) {
        try {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(FILE_VIDEO, file);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Context context, VideoHistory history) {
        try {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(VIDEO_HISTORY, (Parcelable) history);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param url
     * @param title
     */
    public static void startActivity(Context context, String url, String title) {
        try {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(VIDEO_URL, url);
            intent.putExtra(VIDEO_TITLE, title);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param video
     * @param episode
     */
    public static void startActivity(Context context, IVideo video, IVideoEpisode episode) {
        try {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(VIDEO_EPISODE, episode);
            intent.putExtra(VIDEO, video);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param video
     */
    public static void startActivity(Context context, IVideo video) {
        try {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(VIDEO, video);
            intent.putExtra(ISLIVE, false);
            intent.putExtra(ORIENTATION, true);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String url) {
        try {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra(VIDEO_URL, url);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        Intent intent = getIntent();
        registerReceiver(mP2pReceiver, getFilter());

        P2PModule.getInstance().setP2PHandler(new P2PHandler());

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mVideoPlayer.setPlayerType(getDefaultPlayer(intent, mPreferences));
        mVideoPlayer.setController(mPlayerController = new TxVideoPlayerController(this));
        mVideoPlayer.setActivityFullScreen(true, true);
        mPlayerController.setLoadingVisible(View.VISIBLE);
        if (savedState != null && (videoState = savedState.getParcelable(STATE_VIDEO)) != null && !TextUtils.isEmpty(videoState.url)) {
            OnPlayerErrorListener listener = new OnPlayerErrorListener(videoState.url, videoState.referer);
            mPlayerController.setVideoState(videoState);
            mVideoPlayer.setOnErrorListener(listener);
            if (!TextUtils.isEmpty(videoUrl))
                mVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK);//西瓜视频用自带播放器放不了，只能用ijk
        } else {//加载网络数据
            getVideoData(intent);
            loadVideo(intent);
            if (mPreferences.getBoolean("video_src_hit", true)) {
                PlayerHitListener listener = new PlayerHitListener();
                getMainView().postDelayed(listener, 2000);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle arg0) {
        if (mPlayerController != null) {
            videoState = mPlayerController.getVideoState();
            if (videoState != null) arg0.putParcelable(STATE_VIDEO, videoState);
        }
        super.onSaveInstanceState(arg0);
    }

    /**
     * 獲取播放數據
     *
     * @param data
     */
    private void getVideoData(Intent data) {
        mVideo = data.getParcelableExtra(VIDEO);
        videoUrl = data.getStringExtra(VIDEO_URL);
        videoTitle = data.getStringExtra(VIDEO_TITLE);
        mDyttLive = getIntent().getParcelableExtra(DYTT_LIVE);
        if (data.hasExtra(VIDEO_HISTORY)) {
            mVideoEpisode = data.getParcelableExtra(VIDEO_HISTORY);
        } else if (data.hasExtra(VIDEO_EPISODE)) {
            mVideoEpisode = data.getParcelableExtra(VIDEO_EPISODE);
        }
    }

    /**
     * 获取默认的播放器
     *
     * @param data
     * @param preferences
     * @return
     */
    private int getDefaultPlayer(Intent data, SharedPreferences preferences) {
        mDefPlayer = preferences.getString("defPlayer", IJK);
        if (IJK.equals(mDefPlayer) || data.hasExtra(FILE_VIDEO) || data.hasExtra(LOCAL_VIDEO)
                || (mVideoEpisode != null && mVideoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_SUCCESS)) {
            return NiceVideoPlayer.TYPE_IJK;//本地文件用native播放时间不对，所以只能用ijk
        } else {
            return NiceVideoPlayer.TYPE_NATIVE;
        }
    }

    private IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(P2PMessageWhat.P2P_CALLBACK);
        filter.addAction(XLService.GET_PLAY_URL);
        return filter;
    }

    /**
     * 加載播放數據
     * 播放历史记录,剧集跳转过来的
     *
     * @param data
     */
    private void loadVideo(Intent data) {
        RetrofitManager retrofit = getRetrofitManager();
        if (data.hasExtra(FILE_VIDEO)) {//M3u8 下载列表跳转过来的
            File file = (File) data.getSerializableExtra(FILE_VIDEO);
            openVideo(file.getAbsolutePath(), file.getName(), "本地文件");
        } else if (data.hasExtra(LOCAL_VIDEO)) {//视频下载列表跳转过来的
            DownloadEntity entity = data.getParcelableExtra(LOCAL_VIDEO);
            openVideo(entity.getDownloadPath(), entity.getFileName(), "本地文件");
        } else if (data.hasExtra(VIDEO_URL) && XLManager.isXLUrlNoHttp(videoUrl)) {//迅雷视频
            XLManager.get(this).addAndPlay(videoUrl);
        } else if (data.hasExtra(VIDEO_URL) && P2PManager.isXiguaUrl(videoUrl)) {//西瓜视频
            P2PManager.getInstance().play(videoUrl);
        } else if (data.hasExtra(VIDEO_URL) && data.hasExtra(VIDEO_TITLE)) {//直接播放的url
            openVideo(videoUrl, videoTitle);
        } else if (data.hasExtra(VIDEO_URL)) {//直接播放的url
            Uri parse = Uri.parse(Uri.decode(videoUrl));
            openVideo(videoUrl, parse.getLastPathSegment());
        } else if (mVideoEpisode != null && mVideoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_SUCCESS) {//已经下载完成了
            openVideo(mVideoEpisode.getUrl(), mVideoEpisode.getTitle(), "本地文件");
        } else if (data.hasExtra(DYTT_LIVE)) {//电视直播
            mPlayerController.setTitle(mDyttLive.getTitle());
            String contentId = mDyttLive.getContentId();
            long l = System.currentTimeMillis();
            retrofit.enqueue(DyttService.class, liveCallback, "livesInfo", contentId, "6560", l);
        } else if (data.hasExtra(VIDEO) && !data.hasExtra(VIDEO_EPISODE)) {//169秀跳转过来的
            mPlayerController.setTitle(mVideo.getTitle());
            RetrofitManager.REQUEST_URL = mVideo.getUrl();
            String aClass = mVideo.getServiceClass();
            retrofit.enqueue(aClass, callback, "playUrl", mVideo.getUrl());
        } else if (data.hasExtra(VIDEO_EPISODE) || data.hasExtra(VIDEO_HISTORY)) {
            String id = mVideoEpisode.getId();
            videoUrl = mVideoEpisode.getUrl();
            RetrofitManager.REQUEST_URL = mVideoEpisode.getUrl();
            String serviceClass = mVideoEpisode.getServiceClassName();
            if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_URL && Dm5Service.class.getName().equals(serviceClass)) {
                mPlayerController.setTitle(mVideoEpisode.getTitle());
                String[] split = id.split("\\?");//五弹幕需要特殊处理
                retrofit.enqueue(serviceClass, callback, "playUrl", split[0], split[1].replace("link=", ""));
            } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_URL) {
                mPlayerController.setTitle(mVideoEpisode.getTitle());
                retrofit.enqueue(serviceClass, callback, "playUrl", id);//联网获取播放地址
            } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_XUNLEI) {
                openVideo(mVideoEpisode.toPlayUrls(IVideoEpisode.PLAY_TYPE_XUNLEI, IPlayUrls.URL_FILE), videoUrl);
            } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO_M3U8) {
                openVideo(mVideoEpisode.getUrl(), mVideoEpisode.getTitle());//直接M3U8播放地址
            } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                openVideo(mVideoEpisode.getUrl(), mVideoEpisode.getTitle());//直接视频播放地址
            } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO_WEB) {
                openVideo(mVideoEpisode.toPlayUrls(IVideoEpisode.PLAY_TYPE_WEB, IPlayUrls.URL_WEB), mVideoEpisode.getUrl());
            } else {
                showToast(getString(R.string.error_video_type));
            }
        } else {
            showToast(getString(R.string.error_video_type));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mP2pReceiver);
        NiceVideoManager.instance().release();
        P2PModule.getInstance().stopPlay();
        VideoUrlUtil.getInstance().destroy();
        if (mPlayerController != null) mPlayerController.release();
        savePlayHistory(mVideoPlayer == null ? 0 : mVideoPlayer.getCurrentPosition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiceVideoManager.instance().resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiceVideoManager.instance().pause();
    }

    @Override
    public void onBackPressed() {
        if (NiceVideoManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }

    /**
     * 開始播放
     *
     * @param videoUrl
     */
    private void openVideo(String videoUrl, String title) {
        openVideo(videoUrl, title, "");
    }

    /**
     * 開始播放
     *
     * @param videoUrl
     * @param title
     * @param speed
     */
    private void openVideo(String videoUrl, String title, String speed) {
        openVideo(videoUrl, "", title, speed);
    }

    /**
     * 開始播放
     *
     * @param videoUrl
     * @param referer
     * @param title
     * @param speed
     */
    private void openVideo(String videoUrl, String referer, String title, String speed) {
        if (TBS.equals(mDefPlayer) && !P2PManager.isXiguaUrl(videoUrl) && !XLManager.isXLUrlNoHttp(videoUrl)) {//TBS播放
            TbsVideo.openVideo(this, videoUrl);//西瓜视频不能用TBS播放
            VideoPlayerActivity.this.finish();
        } else {//西瓜视频用自带播放器放不了，只能用ijk
            mVideoPlayer.setOnErrorListener(new OnPlayerErrorListener(videoUrl, referer));
            if (P2PManager.isXiguaUrl(videoUrl))
                mVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK);
            mPlayerController.updateSpeed(speed);
            mPlayerController.setTitle(title);
            mVideoPlayer.setUp(videoUrl, referer);
        }
    }

    /**
     * 開始播放
     *
     * @param playUrls
     * @param url
     */
    private void openVideo(IPlayUrls playUrls, String url) {
        int playType = playUrls.getPlayType();
        if (playType == IVideoEpisode.PLAY_TYPE_XIGUA || P2PManager.isXiguaUrl(url)) {//西瓜视频
            P2PManager.getInstance().play(url);
        } else if (playType == IVideoEpisode.PLAY_TYPE_XUNLEI || XLManager.isXLUrlNoHttp(url)) {//迅雷视频
            XLManager.get(this).addAndPlay(url);
        } else if (playType == IVideoEpisode.PLAY_TYPE_WEB) {//需要 webview 解析视频链接
            String referer = playUrls.getReferer();
            ParseUrlListener parseWebUrl = new ParseUrlListener(playUrls);
            VideoUrlUtil init = VideoUrlUtil.getInstance().init(this, url, referer);
            init.setOnParseListener(parseWebUrl).startParse();
        } else if (playType == IVideoEpisode.PLAY_TYPE_WEB_V) {//横向网页，跳转网页播放
            String title = "该视频需要跳转到原网页下载或播放";
            ParseUrlListener listener = new ParseUrlListener(url, playType);
            DialogUtil.showCancelableDialog(this, title, listener);
        } else if (playUrls.isDirectPlay()) {
            openVideo(videoUrl = url, "");
        } else {
            showToast(getString(R.string.error_video_type));
        }
    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 320;
    }

    /**
     * 保存播放记录
     * savePlayHistory
     *
     * @param position
     */
    public void savePlayHistory(long position) {
        AsyTaskQueue queue = AsyTaskQueue.newInstance();
        if (mVideo != null && mVideoEpisode != null) {
            queue.execute(new SaveTaskListener(new VideoHistory(mVideo, mVideoEpisode, position)));
        } else if (mVideo != null) {
            queue.execute(new SaveTaskListener(new VideoHistory(mVideo, position)));
        }
    }

    /**
     * setLoading
     *
     * @param visible
     */
    private void setLoading(boolean visible) {
        if (mPlayerController == null) return;
        mPlayerController.setLoadingVisible(visible ? View.VISIBLE : View.GONE);
    }

    //选择清晰程度播放
    private TxVideoPlayerController.OnClarityListener clarityListener = new TxVideoPlayerController.OnClarityListener() {

        @Override
        public void onClarityPlay(INiceVideoPlayer videoPlayer, Clarity clarity, long current) {
            P2PModule.getInstance().stopPlay();
            DyttLiveUrls urls = (DyttLiveUrls) clarity.ext;
            String p2p_url = urls.getP2p_url().replace("p2p://", "");
            if (!TextUtils.isEmpty(p2p_url)) {
                String payUrl = P2PModule.getInstance().getPlayPath(p2p_url, 1, 0);
                videoPlayer.setUp(payUrl);
            } else {
                showToast("获取播放地址失败");
            }
        }

    };

    /**
     * 西瓜播放器，迅雷邊下邊播播放广播接受者
     */
    private BroadcastReceiver mP2pReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPlayerController == null) return;
            String action = intent.getAction();
            LogUtil.e("=====","action -> " + action);
            try {
                if (XLService.GET_PLAY_URL.equals(action) && intent.hasExtra(XLService.GET_PLAY_URL)) {
                    String extra = intent.getStringExtra(XLService.DATA);
                    openVideo(extra, "");
                } else if (P2PMessageWhat.P2P_CALLBACK.equals(action) && intent.hasExtra(P2PMessageWhat.PLAY_URL)) {//西瓜視頻播放
                    String xigua = intent.getStringExtra(P2PMessageWhat.PLAY_URL);
                    boolean localFile = intent.getBooleanExtra(P2PMessageWhat.LOCAL_FILE, false);
                    String segment = Uri.parse(Uri.decode(videoUrl)).getLastPathSegment();
                    openVideo(xigua, segment, localFile ? "本地文件" : "0.0KB");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    /**
     * 播放記錄保存Task
     * SaveTaskListener
     */
    private class SaveTaskListener extends AsyTaskListenerImpl<Void> {

        private VideoHistory mVideoHistory;

        public SaveTaskListener(VideoHistory mVideoHistory) {
            this.mVideoHistory = mVideoHistory;
        }

        @Override
        public Void onTaskBackground() {
            if (mVideoHistory == null) return null;
            LiteOrm liteOrm = getLiteOrm();
            if (liteOrm == null) return null;
            QueryBuilder<VideoHistory> builder = new QueryBuilder<>(VideoHistory.class);
            List<VideoHistory> query = liteOrm.query(builder.where("cover=?", mVideoHistory.getCover()));
            if (query == null || query.size() == 0) {
                liteOrm.insert(mVideoHistory);
            } else {
                VideoHistory history = query.get(0);
                history.setTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
                history.setPlayPosition(mVideoHistory.getPlayPosition());
                liteOrm.update(history);
            }
            return super.onTaskBackground();
        }

    }

    /**
     * 视频播放提示 Runnable
     * PlayerHitListener
     */
    private class PlayerHitListener implements Runnable, OnButtonClickListener {

        @Override
        public void run() {
            String string = getString(R.string.video_src_hit);
            DialogUtil.showCancelableDialog(VideoPlayerActivity.this, string, "继续提醒", "不要再说了", this);
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> baseAlertDialog, int btn) {
            baseAlertDialog.dismiss();
            if (mPreferences == null || btn != RIGHT) return;
            mPreferences.edit().putBoolean("video_src_hit", false).apply();
        }

    }

    /**
     * webview解析視頻鏈接的回掉
     * ParseUrlListener
     */
    private class ParseUrlListener implements VideoUrlUtil.OnParseWebUrlListener, OnButtonClickListener {

        private IPlayUrls playUrls;

        public ParseUrlListener(IPlayUrls playUrls) {
            this.playUrls = playUrls;
        }

        public ParseUrlListener(String url, int type) {
            VideoPlayUrls playUrls = new VideoPlayUrls();
            HashMap<String, String> map = new HashMap<>();
            map.put("標清", url);
            playUrls.setPlayType(type);
            playUrls.setUrls(map);
            playUrls.setSuccess(true);
            this.playUrls = playUrls;
        }

        @Override
        public void onFindUrl(String videourl) {
            if (mVideoPlayer == null || playUrls == null) return;//解析成功，播放視頻
            openVideo(videourl, playUrls.getReferer(), "", "");
        }

        @Override
        public void onError(String errorMsg) {
            if (playUrls == null) return;//解析失敗，提示用戶打開網頁播放
            String title = "该视频需要使用网页播放";
            DialogUtil.showCancelableDialog(VideoPlayerActivity.this, title, this);
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> baseAlertDialog, int btn) {
            baseAlertDialog.dismiss();
            if (playUrls == null) return;
            String mainUrl = playUrls.getMainUrl();
            if (btn == RIGHT && playUrls.getPlayType() == IVideoEpisode.PLAY_TYPE_WEB_V) {
                WebActivity.startActivity(VideoPlayerActivity.this, mainUrl);
            } else if (btn == RIGHT && playUrls.getPlayType() == IVideoEpisode.PLAY_TYPE_WEB) {
                String referer = playUrls.getReferer();
                WebPlayerActivity.startActivity(VideoPlayerActivity.this, mainUrl, referer);
            }
            VideoPlayerActivity.this.finish();
        }
    }

    /**
     * NiceVideoPlayer 播放視頻錯誤后的回掉方法
     * OnPlayerErrorListener
     */
    private class OnPlayerErrorListener implements IMediaPlayer.OnErrorListener, OnButtonClickListener {

        private String url;
        private String referer = "";

        public OnPlayerErrorListener(String url, String referer) {
            this.url = url;
            this.referer = referer;
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            DialogUtil.showOtherPlayerDialog(VideoPlayerActivity.this, this);
            return false;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> baseAlertDialog, int btn) {
            baseAlertDialog.dismiss();
            if (btn == OnButtonClickListener.LIFT) {
                TbsVideo.openVideo(VideoPlayerActivity.this, url);
                VideoPlayerActivity.this.finish();
            } else if (btn == OnButtonClickListener.CENTRE) {
                WebPlayerActivity.startActivity(VideoPlayerActivity.this, url, referer);
                VideoPlayerActivity.this.finish();
            }
        }

    }

    /**
     * RetrofitCallback
     * 電視直播網絡請求回掉
     */
    private RetrofitCallback<DyttRoot<DyttLiveBody>> liveCallback = new RefreshCallback.RefreshCallbackImpl<DyttRoot<DyttLiveBody>>() {

        @Override
        public void onStart(int enqueueKey) {
            setLoading(true);
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            setLoading(false);
            showToast(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, final DyttRoot<DyttLiveBody> response) {
            if (response != null && response.getBody() != null && response.getBody().isSuccess()) {
                mPlayerController.setClarity(response.getBody().getClaritys(), 0, clarityListener);
            } else {
                showToast(getString(R.string.error_play_conn));
            }
        }

    };

    /**
     * RetrofitCallback
     * 視頻解析網絡請求回掉
     */
    private RetrofitCallback<IPlayUrls> callback = new RefreshCallback.RefreshCallbackImpl<IPlayUrls>() {

        @Override
        public void onStart(int enqueueKey) {
            setLoading(true);
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            setLoading(false);
            showToast(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, final IPlayUrls response) {
            if (response != null && response.isSuccess()) {
                openVideo(response, response.getMainUrl());
            } else {
                showToast(getString(R.string.error_play_conn));
            }
        }

    };

}
