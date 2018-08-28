package com.fanchen.imovie.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.arialyy.aria.core.download.DownloadEntity;
import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.MaterialListDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.bmob.VideoHistory;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.retrofit.service.Dm5Service;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DateUtil;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.VideoUrlUtil;
import com.fanchen.imovie.view.video_new.NiceVideoPlayer;
import com.fanchen.imovie.view.video_new.NiceVideoPlayerManager;
import com.fanchen.imovie.view.video_new.TxVideoPlayerController;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.tencent.smtt.sdk.TbsVideo;
import com.xigua.p2p.P2PManager;
import com.xigua.p2p.P2PMessageWhat;
import com.xigua.p2p.StorageUtils;
import com.xigua.p2p.TaskVideoInfo;

import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * 视频播放页面
 * Created by fanchen on 2017/8/14.
 */
public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {
    public static final String VIDEO_URL = "url";
    public static final String VIDEO_TITLE = "title";
    public static final String ISLIVE = "islive";
    public static final String VIDEO_EPISODE = "videoEpisode";
    public static final String VIDEO = "video";
    public static final String LOCAL_VIDEO = "localVideo";
    public static final String VIDEO_HISTORY = "videoHistory";
    public static final String ORIENTATION = "orientation";

    public  static final String IJK = "ijk";
    public  static final String NATIVE = "native";
    public  static final String TBS = "tbs";

    @InjectView(R.id.nice_video_player)
    protected NiceVideoPlayer mSuperPlayerView;

    private String videoUrl = "";
    private String videoTitle = "";
    private boolean isXiguaLocalFile = false;
    private String xiguaPlayUrl;
    private VideoHistory mVideoHistory;
    private IVideo mVideo;
    private IVideoEpisode mVideoEpisode;

    private TxVideoPlayerController mPlayerController;
    private String mDefPlayer = "";
    private SharedPreferences mSharedPreferences;
    private Handler mHandler = new Handler(Looper.getMainLooper());

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
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        registerReceiver(mP2pReceiver, new IntentFilter(P2PMessageWhat.p2p_callback));
        mVideoHistory = getIntent().getParcelableExtra(VIDEO_HISTORY);
        mVideoEpisode = getIntent().getParcelableExtra(VIDEO_EPISODE);
        mVideo = getIntent().getParcelableExtra(VIDEO);
        videoUrl = getIntent().getStringExtra(VIDEO_URL);
        videoTitle = getIntent().getStringExtra(VIDEO_TITLE);
        mPlayerController = new TxVideoPlayerController(this);
        mDefPlayer = mSharedPreferences.getString("defPlayer", IJK);
        if (NATIVE.equals(mDefPlayer)) {
            mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_NATIVE); // IjkPlayer or MediaPlayer
        } else {
            mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
        }
        mPlayerController.setLenght(98000);
        mSuperPlayerView.setController(mPlayerController);
        mSuperPlayerView.setActivityFullScreen(true);
        mSuperPlayerView.enterFullScreen();
        if (getIntent().hasExtra(VIDEO_HISTORY)) {
            mPlayerController.setTitle(mVideoHistory.getTitle());
            RetrofitManager.REQUEST_URL = mVideoHistory.getUrl();
            if (mVideoHistory.getPlayType() == IVideoEpisode.PLAY_TYPE_URL) {
                RetrofitManager retrofit = getRetrofitManager();
                String serviceClass = mVideoHistory.getServiceClassName();
                if (serviceClass.equals(Dm5Service.class.getName())) {
                    //五弹幕需要特殊处理
                    String[] split = mVideoHistory.getId().split("\\?");
                    retrofit.enqueue(serviceClass, callback, "playUrl", split[0], split[1].replace("link=", ""));
                } else {
                    retrofit.enqueue(serviceClass, callback, "playUrl", mVideoHistory.getId());
                }
            } else if (mVideoHistory.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                String url = mVideoHistory.getUrl();
                if(TBS.equals(mDefPlayer)){
                    TbsVideo.openVideo(this, url);
                    this.finish();
                }else{
                    mSuperPlayerView.setUp(url);
                }
            } else {
                showToast(getString(R.string.error_video_type));
            }
        } else if (getIntent().hasExtra(VIDEO_URL) && getIntent().hasExtra(VIDEO_TITLE)) {
            if(TBS.equals(mDefPlayer)){
                TbsVideo.openVideo(this, videoUrl);
                this.finish();
            }else{
                mPlayerController.setTitle(videoTitle);
                mSuperPlayerView.setUp(videoUrl);
            }
        } else if (getIntent().hasExtra(VIDEO_URL) && videoUrl.contains("xg://")) {
            xiguaPlayUrl = Uri.parse(Uri.decode(videoUrl)).toString().replace("xg://", "ftp://");
            mPlayerController.setTitle(Uri.parse(xiguaPlayUrl).getLastPathSegment());
            mPlayerController.setLoadingVisible(View.VISIBLE);
            P2PManager.getInstance().init(IMovieAppliction.app);
            P2PManager.getInstance().setAllow3G(true);
            P2PManager.getInstance().play(xiguaPlayUrl);
        } else if (getIntent().hasExtra(VIDEO_URL)) {
            if(TBS.equals(mDefPlayer)){
                TbsVideo.openVideo(this, videoUrl);
                this.finish();
            }else{
                String segment = Uri.parse(videoUrl).getLastPathSegment();
                mPlayerController.setTitle(segment);
                mSuperPlayerView.setUp(videoUrl);
            }
        } else if (getIntent().hasExtra(VIDEO_EPISODE)) {
            if (mVideoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_SUCCESS) {
                String url = mVideoEpisode.getUrl();
                if(TBS.equals(mDefPlayer)){
                    TbsVideo.openVideo(this, url);
                    this.finish();
                }else{  //本地文件
                    mPlayerController.updateSpeed("本地文件");
                    mPlayerController.setTitle(mVideoEpisode.getTitle());
                    mSuperPlayerView.setUp(mVideoEpisode.getUrl());
                }
            } else {
                mPlayerController.setTitle(mVideoEpisode.getTitle());
                RetrofitManager.REQUEST_URL = mVideoEpisode.getUrl();
                if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_URL) {
                    RetrofitManager retrofit = getRetrofitManager();
                    String serviceClass = mVideoEpisode.getServiceClassName();
                    if (serviceClass.equals(Dm5Service.class.getName())) {
                        //五弹幕需要特殊处理
                        String[] split = mVideoEpisode.getId().split("\\?");
                        retrofit.enqueue(serviceClass, callback, "playUrl", split[0], split[1].replace("link=", ""));
                    } else {
                        retrofit.enqueue(serviceClass, callback, "playUrl", mVideoEpisode.getId());
                    }
                } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                    String url = mVideoEpisode.getUrl();
                    if(TBS.equals(mDefPlayer)){
                        TbsVideo.openVideo(this, url);
                        this.finish();
                    }else{
                        mSuperPlayerView.setUp(url);
                    }
                } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO_WEB) {
                    mPlayerController.setTitle(mVideoEpisode.getTitle());
                    mPlayerController.setLoadingVisible(View.VISIBLE);
                    playerVideo(mVideoEpisode.toPlayUrls(IVideoEpisode.PLAY_TYPE_WEB, IPlayUrls.URL_WEB), mVideoEpisode.getUrl());
                } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO_M3U8) {
                    String url = mVideoEpisode.getUrl();
                    if(TBS.equals(mDefPlayer)){
                        TbsVideo.openVideo(this, url);
                        this.finish();
                    }else{
                        mPlayerController.setTitle(mVideoEpisode.getTitle());
                        mPlayerController.setLoadingVisible(View.VISIBLE);
                        mSuperPlayerView.setUp(url);
                    }
                } else {
                    showToast(getString(R.string.error_video_type));
                }
            }
        } else if (getIntent().hasExtra(VIDEO)) {
            mPlayerController.setTitle(mVideo.getTitle());
            getRetrofitManager().enqueue(mVideo.getServiceClass(), callback, "playUrl", mVideo.getUrl());
        } else if (getIntent().hasExtra(LOCAL_VIDEO)) {
            DownloadEntity entity = getIntent().getParcelableExtra(LOCAL_VIDEO);
            String url = entity.getUrl();
            if(TBS.equals(mDefPlayer)){
                TbsVideo.openVideo(this, url);
                this.finish();
            }else{
                mPlayerController.updateSpeed("本地文件");
                mPlayerController.setTitle(entity.getFileName());
                mSuperPlayerView.setUp(url);
            }
        }
        if (mSharedPreferences.getBoolean("video_src_hit", true)) {
            getMainView().postDelayed(runnable, 2000);
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mPlayerController.setChangeClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        VideoUrlUtil.getInstance().destroy();
        unregisterReceiver(mP2pReceiver);
        if (xiguaPlayUrl != null && IMovieAppliction.app != null) {
            P2PManager.getInstance().init(IMovieAppliction.app);
            P2PManager.getInstance().setAllow3G(false);
            if (!isXiguaLocalFile && xiguaPlayUrl != null) {
                P2PManager.getInstance().remove(xiguaPlayUrl);
            }
        }
    }

    @Override
    public void finish() {
        savePlayHistory();
        super.finish();
    }

    @Override
    protected void onResume() {
        NiceVideoPlayerManager.instance().resumeNiceVideoPlayer();
        super.onResume();
    }

    @Override
    protected void onPause() {
        NiceVideoPlayerManager.instance().pauseNiceVideoPlayer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        String[] titles = new String[]{"TbsVideo", "系统播放器", "IjkPlayer"};
        DialogUtil.showMaterialListDialog(this, titles, itemClickListener);
    }

    public void savePlayHistory() {
        if (mSuperPlayerView == null) return;
        long position = mSuperPlayerView.getCurrentPosition();
        VideoHistory history = null;
        if (mVideo != null && mVideoEpisode != null) {
            history = new VideoHistory(mVideo, mVideoEpisode, position);
        } else if (mVideo != null) {
            history = new VideoHistory(mVideo, position);
        } else {
            return;
        }
        AsyTaskQueue.newInstance().execute(new SaveTaskListener(history));
    }

    /**
     * @param playUrls
     * @param url
     */
    private void playerVideo(IPlayUrls playUrls, String url) {
        int playType = playUrls.getPlayType();
        if (url.startsWith("ftp://") || url.startsWith("xg://")) {
            String title = "该视频需要西瓜播放器进行播放";
            DialogUtil.showCancelableDialog(this, title, new PlayerListener(url, playType));
        } else if (playType == IVideoEpisode.PLAY_TYPE_WEB) {
            String referer = playUrls.getReferer();
            ParseWebUrl parseWebUrl = new ParseWebUrl(playUrls);
            VideoUrlUtil.getInstance().init(this, url, referer).setOnParseListener(parseWebUrl).startParse();
        } else if (playType == IVideoEpisode.PLAY_TYPE_WEB_V) {
            String title = "该视频需要跳转到原网页下载或播放";
            DialogUtil.showCancelableDialog(this, title, new PlayerListener(url, playType));
        } else if (playType == IVideoEpisode.PLAY_TYPE_ZZPLAYER || playType == IVideoEpisode.PLAY_TYPE_VIDEO) {
            if(TBS.equals(mDefPlayer)){
                TbsVideo.openVideo(this, url);
                this.finish();
            }else{
                if (mSuperPlayerView == null) return;
                mSuperPlayerView.setOnErrorListener(new OnPlayerErrorListener(url));
                mSuperPlayerView.setUp(url);
            }
        }
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            String string = getString(R.string.video_src_hit);
            DialogUtil.showCancelableDialog(VideoPlayerActivity.this, string, "继续提醒", "不要再说了", buttonClickListener);
        }

    };

    //
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mSuperPlayerView == null) return;
            String playerUrl = mSuperPlayerView.getPlayerUrl();
            if (position == 0 && !TextUtils.isEmpty(playerUrl)) {
                TbsVideo.openVideo(VideoPlayerActivity.this, playerUrl);
                VideoPlayerActivity.this.finish();
            } else if (position == 1 && !TextUtils.isEmpty(playerUrl)) {
                mSuperPlayerView.release();
                mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_NATIVE); // IjkPlayer or MediaPlayer
                mSuperPlayerView.setActivityFullScreen(true);
                mSuperPlayerView.enterFullScreen();
                mSuperPlayerView.start();
            } else if (position == 2 && !TextUtils.isEmpty(playerUrl)) {
                mSuperPlayerView.release();
                mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
                mSuperPlayerView.setActivityFullScreen(true);
                mSuperPlayerView.enterFullScreen();
                mSuperPlayerView.start();
            }
        }

    };


    private OnButtonClickListener buttonClickListener = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (mSharedPreferences == null) return;
            if (btn == OnButtonClickListener.RIGHT) {
                mSharedPreferences.edit().putBoolean("video_src_hit", false).commit();
            }
        }

    };

    /**
     * 西瓜播放器播放广播接受者
     */
    private BroadcastReceiver mP2pReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context var1, Intent var2) {
            if (mPlayerController == null || mSuperPlayerView == null) return;
            int var3 = var2.getIntExtra("what", 0);
            if (var3 == 1 && var2.hasExtra("data")) {
                TaskVideoInfo var5 = var2.getParcelableExtra("data");
                if (!isXiguaLocalFile) {
                    String size = mSuperPlayerView.getSize(var5.getSpeed());
                    mPlayerController.updateSpeed(size);
                } else {
                    mPlayerController.updateSpeed("本地文件");
                }
            } else if (var3 == 2 && var2.hasExtra("data")) {
                List<TaskVideoInfo> infos = var2.getParcelableArrayListExtra("data");
                for (TaskVideoInfo info : infos) {
                    String url = info.getUrl();
                    if (info.getLocalSize() > 0L && url != null && url.equalsIgnoreCase(xiguaPlayUrl)) {
                        isXiguaLocalFile = true;
                        mPlayerController.updateSpeed("本地文件");
                    }
                }
            } else if (var3 == 258 && var2.hasExtra("play_url")) {
                String url = var2.getStringExtra("play_url");
                isXiguaLocalFile = var2.getIntExtra("islocal", -1) == 1;
                if(TBS.equals(mDefPlayer)){
                    TbsVideo.openVideo(VideoPlayerActivity.this, url);
                    VideoPlayerActivity.this.finish();
                }else{
                    mSuperPlayerView.setUp(url);
                    if (isXiguaLocalFile) {
                        mPlayerController.updateSpeed("本地文件");
                    }
                }
            }
        }

    };

    private class SaveTaskListener extends AsyTaskListenerImpl<Void> {

        private VideoHistory mVideoHistory;

        public SaveTaskListener(VideoHistory mVideoHistory) {
            this.mVideoHistory = mVideoHistory;
        }

        @Override
        public Void onTaskBackground() {
            if (getLiteOrm() == null || mVideoHistory == null) return null;
            List<VideoHistory> query = getLiteOrm().query(new QueryBuilder<>(VideoHistory.class).where("cover=?", mVideoHistory.getCover()));
            if (query == null || query.size() == 0) {
                getLiteOrm().insert(mVideoHistory);
            } else {
                VideoHistory history = query.get(0);
                history.setTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
                history.setPlayPosition(mVideoHistory.getPlayPosition());
                getLiteOrm().update(history);
            }
            return super.onTaskBackground();
        }

    }

    private class OtherPlayerListener implements OnButtonClickListener {
        private String url;
        private String referer = "";

        public OtherPlayerListener(String url, String referer) {
            this.url = url;
            this.referer = referer;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn == LIFT) {
                TbsVideo.openVideo(VideoPlayerActivity.this, url);
                VideoPlayerActivity.this.finish();
            } else if (btn == CENTRE) {
                WebPlayerActivity.startActivity(VideoPlayerActivity.this, url, referer);
                VideoPlayerActivity.this.finish();
            }
        }
    }

    private class PlayerListener implements OnButtonClickListener {
        private String url = "";
        private String referer = "";
        private int playType = IVideoEpisode.PLAY_TYPE_WEB;

        public PlayerListener(String url, int playType) {
            this.url = url;
            this.playType = playType;
        }

        public PlayerListener(String url, String referer) {
            this.url = url;
            this.referer = referer;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn != RIGHT) {
                VideoPlayerActivity.this.finish();
                return;
            }
            if (playType == IVideoEpisode.PLAY_TYPE_XIGUA) {
                StorageUtils.init(IMovieAppliction.app);
                P2PManager.getInstance().init(IMovieAppliction.app);
                P2PManager.getInstance().setAllow3G(true);
                DialogUtil.showProgressDialog(VideoPlayerActivity.this, getString(R.string.loading));
                mHandler.postDelayed(new XiguaRunnable(url), 3000);
            } else if (playType == IVideoEpisode.PLAY_TYPE_WEB_V) {
                WebActivity.startActivity(VideoPlayerActivity.this, url);
                VideoPlayerActivity.this.finish();
            } else if (playType == IVideoEpisode.PLAY_TYPE_WEB) {
                WebPlayerActivity.startActivity(VideoPlayerActivity.this, url, referer);
                VideoPlayerActivity.this.finish();
            }
        }

    }

    private class ParseWebUrl implements VideoUrlUtil.OnParseWebUrlListener {
        private IPlayUrls playUrls;

        public ParseWebUrl(IPlayUrls playUrls) {
            this.playUrls = playUrls;
        }

        @Override
        public void onFindUrl(String videourl) {
            if (mSuperPlayerView == null || playUrls == null) return;
            if(TBS.equals(mDefPlayer)){
                TbsVideo.openVideo(VideoPlayerActivity.this, videourl);
                VideoPlayerActivity.this.finish();
            }else{
                String referer = playUrls.getReferer();
                mSuperPlayerView.setOnErrorListener(new OnPlayerErrorListener(videourl,referer ));
                if (playUrls.m3u8Referer()) {
                    mSuperPlayerView.setUp(videourl, referer);
                } else {
                    mSuperPlayerView.setUp(videourl);
                }
            }
        }

        @Override
        public void onError(String errorMsg) {
            if (playUrls == null) return;
            String title = "该视频需要使用网页播放";
            String url = playUrls.getUrls().entrySet().iterator().next().getValue();
            DialogUtil.showCancelableDialog(VideoPlayerActivity.this, title, new PlayerListener(url, playUrls.getReferer()));
        }

    }

    private class XiguaRunnable implements Runnable {

        private String url;

        private XiguaRunnable(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            DialogUtil.closeProgressDialog();
            xiguaPlayUrl = url.replace("ftp://", "xg://");
            xiguaPlayUrl = Uri.parse(Uri.decode(url)).toString().replace("xg://", "ftp://");
            String lastPathSegment = Uri.parse(xiguaPlayUrl).getLastPathSegment();
            mPlayerController.setTitle(lastPathSegment);
            P2PManager.getInstance().init(IMovieAppliction.app);
            P2PManager.getInstance().setAllow3G(true);
            P2PManager.getInstance().play(xiguaPlayUrl);
        }

    }

    private class OnPlayerErrorListener implements IMediaPlayer.OnErrorListener {

        private String url;
        private String referer = "";

        public OnPlayerErrorListener(String url) {
            this.url = url;
        }

        public OnPlayerErrorListener(String url, String referer) {
            this.url = url;
            this.referer = referer;
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            DialogUtil.showOtherPlayerDialog(VideoPlayerActivity.this, new OtherPlayerListener(url, referer));
            return false;
        }

    }

    private RetrofitCallback<IPlayUrls> callback = new RefreshCallback<IPlayUrls>() {

        @Override
        public void onStart(int enqueueKey) {
            if (mPlayerController == null) return;
            mPlayerController.setLoadingVisible(View.VISIBLE);
        }

        @Override
        public void onFinish(int enqueueKey) {
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (mPlayerController == null) return;
            mPlayerController.setLoadingVisible(View.GONE);
            showToast(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, final IPlayUrls response) {
            if (response != null && response.isSuccess()) {
                Map<String, String> urls = response.getUrls();
                if (urls != null && !urls.isEmpty()) {
                    String url = urls.entrySet().iterator().next().getValue();
                    playerVideo(response, url);
                } else {
                    showToast(getString(R.string.error_play_conn));
                }
            } else {
                showToast(getString(R.string.error_play_conn));
            }
        }

    };

}
