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
import android.widget.AdapterView;

import com.arialyy.aria.core.download.DownloadEntity;
import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
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
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.DateUtil;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.VideoUrlUtil;
import com.fanchen.imovie.view.video_new.NiceVideoPlayer;
import com.fanchen.imovie.view.video_new.NiceVideoPlayerController;
import com.fanchen.imovie.view.video_new.NiceVideoPlayerManager;
import com.fanchen.imovie.view.video_new.TxVideoPlayerController;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.tencent.smtt.sdk.TbsVideo;
import com.xigua.p2p.P2PManager;
import com.xigua.p2p.P2PMessageWhat;
import com.xigua.p2p.TaskVideoInfo;

import java.io.File;
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
    public static final String FILE_VIDEO = "fileVideo";
    public static final String VIDEO_HISTORY = "videoHistory";
    public static final String ORIENTATION = "orientation";

    public static final String IJK = "ijk";
    public static final String NATIVE = "native";
    public static final String TBS = "tbs";

    @InjectView(R.id.nice_video_player)
    protected NiceVideoPlayer mSuperPlayerView;

    private String videoUrl = "";
    private String videoTitle = "";
    private String xiguaPlayUrl;
    private boolean xiguaLocalFile = false;
    private VideoHistory mVideoHistory;
    private IVideo mVideo;
    private IVideoEpisode mVideoEpisode;

    private NiceVideoPlayerController mPlayerController;
    private String mDefPlayer = "";
    private SharedPreferences mSharedPreferences;

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
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        registerReceiver(mP2pReceiver, new IntentFilter(P2PMessageWhat.p2p_callback));
        mDefPlayer = mSharedPreferences.getString("defPlayer", IJK);
        Intent intent = getIntent();
        if (IJK.equals(mDefPlayer) || intent.hasExtra(FILE_VIDEO) || intent.hasExtra(LOCAL_VIDEO)) {
            mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_IJK);//本地文件用native播放时间不对，所以只能用ijk
        } else if (mVideoEpisode != null && mVideoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_SUCCESS) {
            mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_IJK);
        } else {
            mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_NATIVE);
        }
        mPlayerController = new TxVideoPlayerController(this).setLenght(98000);
        mSuperPlayerView.setController(mPlayerController);
        mSuperPlayerView.setActivityFullScreen(true);
        mSuperPlayerView.enterFullScreen();
        getVideoData(intent);
        loadVideo(intent);
        if (mSharedPreferences.getBoolean("video_src_hit", true)) {
            getMainView().postDelayed(runnable, 2000);
        }
    }

    /**
     * 獲取播放數據
     *
     * @param data
     */
    private void getVideoData(Intent data) {
        mVideoHistory = data.getParcelableExtra(VIDEO_HISTORY);
        mVideoEpisode = data.getParcelableExtra(VIDEO_EPISODE);
        mVideo = data.getParcelableExtra(VIDEO);
        videoUrl = data.getStringExtra(VIDEO_URL);
        videoTitle = data.getStringExtra(VIDEO_TITLE);
    }

    /**
     * 加載播放數據
     *
     * @param data
     */
    private void loadVideo(Intent data) {
        if (data.hasExtra(FILE_VIDEO)) {//M3u8 下载列表跳转过来的
            File file = (File) data.getSerializableExtra(FILE_VIDEO);
            openVideo(file.getAbsolutePath(), file.getName(), "本地文件");
        } else if (data.hasExtra(LOCAL_VIDEO)) {//视频下载列表跳转过来的
            DownloadEntity entity = data.getParcelableExtra(LOCAL_VIDEO);
            openVideo(entity.getDownloadPath(), entity.getFileName(), "本地文件");
        } else if (data.hasExtra(VIDEO_URL) && videoUrl.contains("xg://")) {//西瓜视频
            xiguaPlayUrl = videoUrl.replace("xg://", "ftp://");
            mPlayerController.setTitle(Uri.parse(Uri.decode(videoUrl)).getLastPathSegment());
            mPlayerController.setLoadingVisible(View.VISIBLE);
            mSuperPlayerView.setPlaySpeed(false);
            P2PManager.getInstance().isConnect();
            P2PManager.getInstance().play(xiguaPlayUrl);
        } else if (data.hasExtra(VIDEO_URL) && data.hasExtra(VIDEO_TITLE)) {//直接播放的url
            openVideo(videoUrl, videoTitle, "");
        } else if (data.hasExtra(VIDEO_URL)) {//直接播放的url
            openVideo(videoUrl, Uri.parse(videoUrl).getLastPathSegment(), "");
        } else if (data.hasExtra(VIDEO_HISTORY)) {//播放历史记录跳转过来的
            mPlayerController.setTitle(mVideoHistory.getTitle());
            RetrofitManager.REQUEST_URL = mVideoHistory.getUrl();
            RetrofitManager retrofit = getRetrofitManager();
            String serviceClass = mVideoHistory.getServiceClassName();
            if (mVideoHistory.getPlayType() == IVideoEpisode.PLAY_TYPE_URL && Dm5Service.class.getName().equals(serviceClass)) {//五弹幕
                mPlayerController.setLoadingVisible(View.VISIBLE);
                String[] split = mVideoHistory.getId().split("\\?");
                retrofit.enqueue(serviceClass, callback, "playUrl", split[0], split[1].replace("link=", ""));
            } else if (mVideoHistory.getPlayType() == IVideoEpisode.PLAY_TYPE_URL) {//其他视频
                mPlayerController.setLoadingVisible(View.VISIBLE);
                retrofit.enqueue(serviceClass, callback, "playUrl", mVideoHistory.getId());
            } else if (mVideoHistory.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                openVideo(mVideoHistory.getUrl(), "", "");//直接播放
            } else {//不支持的播放类型
                showToast(getString(R.string.error_video_type));
            }
        } else if (data.hasExtra(VIDEO_EPISODE)) {//video 章節跳過來的
            if (mVideoEpisode.getDownloadState() == IVideoEpisode.DOWNLOAD_SUCCESS) {
                openVideo(mVideoEpisode.getUrl(), mVideoEpisode.getTitle(), "本地文件");
            } else {
                mPlayerController.setTitle(mVideoEpisode.getTitle());
                RetrofitManager.REQUEST_URL = mVideoEpisode.getUrl();
                RetrofitManager retrofit = getRetrofitManager();
                String serviceClass = mVideoEpisode.getServiceClassName();
                if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_URL && Dm5Service.class.getName().equals(serviceClass)) { //五弹幕需要特殊处理
                    mPlayerController.setLoadingVisible(View.VISIBLE);
                    String[] split = mVideoEpisode.getId().split("\\?");
                    retrofit.enqueue(serviceClass, callback, "playUrl", split[0], split[1].replace("link=", ""));
                } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_URL) {
                    mPlayerController.setLoadingVisible(View.VISIBLE);
                    retrofit.enqueue(serviceClass, callback, "playUrl", mVideoEpisode.getId());
                } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                    openVideo(mVideoEpisode.getUrl(), "", "");
                } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO_WEB) {
                    mPlayerController.setTitle(mVideoEpisode.getTitle());
                    mPlayerController.setLoadingVisible(View.VISIBLE);
                    playerVideo(mVideoEpisode.toPlayUrls(IVideoEpisode.PLAY_TYPE_WEB, IPlayUrls.URL_WEB), mVideoEpisode.getUrl());
                } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO_M3U8) {
                    openVideo(mVideoEpisode.getUrl(), mVideoEpisode.getTitle(), "");
                } else {
                    showToast(getString(R.string.error_video_type));
                }
            }
        } else if (data.hasExtra(VIDEO)) {//169秀跳转过来的
            mPlayerController.setTitle(mVideo.getTitle());
            mPlayerController.setLoadingVisible(View.VISIBLE);
            RetrofitManager retrofit = getRetrofitManager();
            retrofit.enqueue(mVideo.getServiceClass(), callback, "playUrl", mVideo.getUrl());
        } else {
            showToast(getString(R.string.error_video_type));
        }
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
        if (TBS.equals(mDefPlayer) && TextUtils.isEmpty(xiguaPlayUrl)) {//TBS播放
            TbsVideo.openVideo(this, videoUrl);//西瓜视频不能用TBS播放
            VideoPlayerActivity.this.finish();
        } else {
            if (!TextUtils.isEmpty(speed)) mPlayerController.updateSpeed(speed);
            if (!TextUtils.isEmpty(title)) mPlayerController.setTitle(title);
            mSuperPlayerView.setOnErrorListener(new OnPlayerErrorListener(videoUrl, referer));
            if(!TextUtils.isEmpty(xiguaPlayUrl))mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_IJK);//西瓜视频用自带播放器放不了，只能用ijk
            if (TextUtils.isEmpty(referer)) mSuperPlayerView.setUp(videoUrl);
            else mSuperPlayerView.setUp(videoUrl, referer);
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        if(mPlayerController != null) mPlayerController.setChangeClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        VideoUrlUtil.getInstance().destroy();
        unregisterReceiver(mP2pReceiver);
    }

    @Override
    public void finish() {
        savePlayHistory();
        if (!TextUtils.isEmpty(xiguaPlayUrl) && !xiguaLocalFile) {
            String title = getString(R.string.xigua_finish_hit);
            DialogUtil.showCancelableDialog(this, title, downloadClick);
        } else {
            super.finish();
        }
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
        AsyTaskQueue queue = AsyTaskQueue.newInstance();
        if (mVideo != null && mVideoEpisode != null) {
            queue.execute(new SaveTaskListener(new VideoHistory(mVideo, mVideoEpisode, position)));
        } else if (mVideo != null) {
            queue.execute(new SaveTaskListener(new VideoHistory(mVideo, position)));
        }
    }

    /**
     * @param playUrls
     * @param url
     */
    private void playerVideo(IPlayUrls playUrls, String url) {
        int playType = playUrls.getPlayType();
        if (url.startsWith("ftp://") || url.startsWith("xg://")) {
            xiguaPlayUrl = url.replace("xg://", "ftp://");
            mPlayerController.setTitle(Uri.parse(xiguaPlayUrl).getLastPathSegment());
            mSuperPlayerView.setPlaySpeed(false);
            P2PManager.getInstance().isConnect();
            P2PManager.getInstance().play(xiguaPlayUrl);
        } else if (playType == IVideoEpisode.PLAY_TYPE_WEB) {
            String referer = playUrls.getReferer();
            ParseWebUrl parseWebUrl = new ParseWebUrl(playUrls);
            VideoUrlUtil.getInstance().init(this, url, referer).setOnParseListener(parseWebUrl).startParse();
        } else if (playType == IVideoEpisode.PLAY_TYPE_WEB_V) {
            String title = "该视频需要跳转到原网页下载或播放";
            PlayerListener listener = new PlayerListener(url, playType);
            DialogUtil.showCancelableDialog(this, title, listener);
        } else if (playType == IVideoEpisode.PLAY_TYPE_ZZPLAYER || playType == IVideoEpisode.PLAY_TYPE_VIDEO || playType == IVideoEpisode.PLAY_TYPE_VIDEO_M3U8) {
            openVideo(url, "", "");
        } else {
            showToast(getString(R.string.error_video_type));
        }
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            String string = getString(R.string.video_src_hit);
            DialogUtil.showCancelableDialog(VideoPlayerActivity.this, string, "继续提醒", "不要再说了", buttonClickListener);
        }

    };

    private OnButtonClickListener downloadClick = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> baseAlertDialog, int btn) {
            baseAlertDialog.dismiss();
            if (btn != RIGHT && !TextUtils.isEmpty(xiguaPlayUrl))
                P2PManager.getInstance().remove(xiguaPlayUrl);
            VideoPlayerActivity.super.finish();
        }

    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mSuperPlayerView == null) return;
            String playerUrl = mSuperPlayerView.getPlayerUrl();
            if (position == 0 && !TextUtils.isEmpty(playerUrl)) {
                TbsVideo.openVideo(VideoPlayerActivity.this, playerUrl);
                VideoPlayerActivity.this.finish();
            } else if (!TextUtils.isEmpty(playerUrl)) {
                mSuperPlayerView.release();
                if (position == 1 && TextUtils.isEmpty(xiguaPlayUrl)) {
                    mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_NATIVE); // IjkPlayer or MediaPlayer
                } else {//西瓜视频用自带播放器放不了，只能用ijk
                    mSuperPlayerView.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
                }
                mSuperPlayerView.setActivityFullScreen(true);
                mSuperPlayerView.enterFullScreen();
                mSuperPlayerView.setUp(playerUrl);
            }
        }

    };

    private OnButtonClickListener buttonClickListener = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (mSharedPreferences == null || btn != OnButtonClickListener.RIGHT) return;
            mSharedPreferences.edit().putBoolean("video_src_hit", false).apply();
        }

    };

    /**
     * 西瓜播放器播放广播接受者
     */
    private BroadcastReceiver mP2pReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPlayerController == null || TextUtils.isEmpty(xiguaPlayUrl)) return;
            int what = intent.getIntExtra("what", 0);
            if (what == 1 && intent.hasExtra("data")) {
                TaskVideoInfo var5 = intent.getParcelableExtra("data");
                mPlayerController.updateSpeed(AppUtil.getSize(var5.getSpeed()));
            } else if (what == 2 && intent.hasExtra("data")) {
                List<TaskVideoInfo> infos = intent.getParcelableArrayListExtra("data");
                if (infos == null || infos.isEmpty()) return;
                for (TaskVideoInfo info : infos) {
                    if (info.getLocalSize() <= 0) continue;
                    String infoUrl = info.getUrl();
                    if (infoUrl == null || !infoUrl.equalsIgnoreCase(xiguaPlayUrl)) continue;
                    xiguaLocalFile = true;
                }
            } else if (what == 258 && intent.hasExtra("play_url")) {
                String url = intent.getStringExtra("play_url");
                xiguaLocalFile = intent.getIntExtra("islocal", -1) == 1;
                String[] xigua = xiguaPlayUrl.split("/");
                openVideo(url, xigua[xigua.length - 1], xiguaLocalFile ? "本地文件" : "0.0KB");
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
            if (playType == IVideoEpisode.PLAY_TYPE_WEB_V) {
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
            String referer = playUrls.getReferer();
            referer = playUrls.m3u8Referer() ? referer : "";
            openVideo(videourl, referer, "", "");
        }

        @Override
        public void onError(String errorMsg) {
            if (playUrls == null) return;
            String title = "该视频需要使用网页播放";
            String url = playUrls.getUrls().entrySet().iterator().next().getValue();
            DialogUtil.showCancelableDialog(VideoPlayerActivity.this, title, new PlayerListener(url, playUrls.getReferer()));
        }

    }

    private class OnPlayerErrorListener implements IMediaPlayer.OnErrorListener {

        private String url;
        private String referer = "";

        public OnPlayerErrorListener(String url, String referer) {
            this.url = url;
            this.referer = referer;
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            OtherPlayerListener listener = new OtherPlayerListener(url, referer);
            DialogUtil.showOtherPlayerDialog(VideoPlayerActivity.this, listener);
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
