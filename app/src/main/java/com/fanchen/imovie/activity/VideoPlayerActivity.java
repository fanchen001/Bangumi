package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.fanchen.imovie.entity.dytt.DyttLiveBody;
import com.fanchen.imovie.entity.dytt.DyttLiveVideoUrls;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.retrofit.service.Dm5Service;
import com.fanchen.imovie.retrofit.service.TucaoService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DateUtil;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.view.video.SuperPlayerView;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.vbyte.p2p.old.Vbyte;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;

import java.util.List;

import butterknife.InjectView;


/**
 * 视频播放页面
 * Created by fanchen on 2017/8/14.
 */
public class VideoPlayerActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String VIDEO_URL = "url";
    public static final String VIDEO_TITLE = "title";
    public static final String LIVE = "live";
    public static final String ISLIVE = "islive";
    public static final String VIDEOEPISODE = "videoEpisode";
    public static final String VIDEO = "video";
    public static final String LOCAL_VIDEO = "localVideo";
    public static final String MAGNET = "magnet";
    public static final String ORIENTATION = "orientation";

    @InjectView(R.id.spv_video)
    protected SuperPlayerView mSuperPlayerView;

    private long preTime;
    private String videoUrl = "";
    private String videoTitle = "";
    private Vbyte mVbyteManager;
    private DyttLiveBody mLiveBody;
    private IVideo mVideo;
    private TorrentFileInfo mTorrentFile;
    private IVideoEpisode mVideoEpisode;

    /**
     * @param context
     * @param body
     */
    public static void startActivity(Context context, DownloadEntity body) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(LOCAL_VIDEO, body);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param body
     */
    public static void startActivity(Context context, DyttLiveBody body) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(LIVE, body);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param info
     */
    public static void startActivity(Context context, TorrentFileInfo info) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(MAGNET, info);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param url
     * @param title
     */
    public static void startActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(VIDEO_URL, url);
        intent.putExtra(VIDEO_TITLE, title);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param video
     * @param episode
     */
    public static void startActivity(Context context, IVideo video, IVideoEpisode episode) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(VIDEOEPISODE, episode);
        intent.putExtra(VIDEO, video);
        context.startActivity(intent);
    }

    /**
     *
     * @param context
     * @param video
     */
    public static void startActivity(Context context, IVideo video,boolean isLive) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(VIDEO, video);
        intent.putExtra(ISLIVE, isLive);
        intent.putExtra(ORIENTATION, !isLive);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param url
     */
    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(VIDEO_URL, url);
        context.startActivity(intent);
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
    protected void setListener() {
        super.setListener();
        mSuperPlayerView.setOnNetChangeListener(changeListener);
        mSuperPlayerView.setDefinitionListener(this);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mLiveBody = getIntent().getParcelableExtra(LIVE);
        mVideoEpisode = getIntent().getParcelableExtra(VIDEOEPISODE);
        mVideo = getIntent().getParcelableExtra(VIDEO);
        videoUrl = getIntent().getStringExtra(VIDEO_URL);
        videoTitle = getIntent().getStringExtra(VIDEO_TITLE);
        mTorrentFile = getIntent().getParcelableExtra(MAGNET);
        mSuperPlayerView.setNetChangeListener(true);
        mSuperPlayerView.setScaleType(SuperPlayerView.SCALETYPE_FITPARENT);
        mSuperPlayerView.setLive(getIntent().getBooleanExtra(ISLIVE,false));
        mSuperPlayerView.setFullScreenOnly(getIntent().getBooleanExtra(ORIENTATION,true));
        mSuperPlayerView.setPlayerWH(0, mSuperPlayerView.getMeasuredHeight());
        if (getIntent().hasExtra(LIVE) && mLiveBody.getVideoUrls().size() > 0) {
            mSuperPlayerView.setLive(true);
            mSuperPlayerView.setTitle(mLiveBody.getVideoName());
            mSuperPlayerView.setDefinition(true);
            playLive(mLiveBody.getVideoUrls().get(0).getLocation().replace("p2p://", ""));
            mSuperPlayerView.setSettingVisible(View.INVISIBLE);
        } else if (getIntent().hasExtra(MAGNET)) {
            mSuperPlayerView.setTitle(mTorrentFile.mFileName);
            mSuperPlayerView.play(mTorrentFile.playUrl);
        } else if (getIntent().hasExtra(VIDEO_URL) && getIntent().hasExtra(VIDEO_TITLE)) {
            mSuperPlayerView.setTitle(videoTitle);
            mSuperPlayerView.play(videoUrl);
        } else if (getIntent().hasExtra(VIDEO_URL)) {
            mSuperPlayerView.play(videoUrl);
        } else if (getIntent().hasExtra(VIDEOEPISODE)) {
            if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_URL) {
                mSuperPlayerView.setTitle(mVideoEpisode.getTitle());
                RetrofitManager.REQUEST_URL = mVideoEpisode.getUrl();
                if(mVideoEpisode.getServiceClassName().equals(Dm5Service.class.getName())){
                    //五弹幕需要特殊处理
                    String[] split = mVideoEpisode.getId().split("\\?");
                    getRetrofitManager().enqueue(mVideoEpisode.getServiceClassName(), callback, "playUrl", split[0], split[1].replace("link=", ""));
                }else if(mVideoEpisode.getServiceClassName().equals(TucaoService.class.getName())){
                    //吐槽C也需要需要特殊处理
                    getRetrofitManager().enqueue(mVideoEpisode.getServiceClassName(), callback, "playUrl", mVideoEpisode.getExtend(), mVideoEpisode.getId());
                }else{
                    LogUtil.e("enqueue","playUrl");
                    getRetrofitManager().enqueue(mVideoEpisode.getServiceClassName(), callback, "playUrl", mVideoEpisode.getId());
                }
            } else if (mVideoEpisode.getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO) {
                mSuperPlayerView.setTitle(mVideoEpisode.getTitle());
                mSuperPlayerView.play(mVideoEpisode.getUrl());
            }
        }else if(getIntent().hasExtra(VIDEO)){
            mSuperPlayerView.setTitle(mVideo.getTitle());
            getRetrofitManager().enqueue(mVideo.getServiceClassName(), callback, "playUrl", mVideo.getUrl());
        }else if(getIntent().hasExtra(LOCAL_VIDEO)){
            DownloadEntity entity =getIntent().getParcelableExtra(LOCAL_VIDEO);
            mSuperPlayerView.setTitle(entity.getFileName());
            mSuperPlayerView.play(entity.getDownloadPath());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mLiveBody != null) {
            List<DyttLiveVideoUrls> videoUrls = mLiveBody.getVideoUrls();
            if (videoUrls != null && videoUrls.size() > position) {
                mSuperPlayerView.setTitle(mLiveBody.getVideoName());
                playLive(videoUrls.get(position).getLocation().replace("p2p://", ""));
            }
        }
    }

    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mSuperPlayerView != null) {
            mSuperPlayerView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSuperPlayerView != null) {
            mSuperPlayerView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSuperPlayerView != null) {
            mSuperPlayerView.onDestroy();
        }
    }

    @Override
    public void finish() {
        savePlayHistory();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        long l = System.currentTimeMillis();
        if (l - preTime < 2000) {
            if (mSuperPlayerView != null && mSuperPlayerView.onBackPressed()) {
                return;
            }
            super.onBackPressed();
        } else {
            preTime = l;
            showToast(getString(R.string.back_hit));
        }
    }

    public void savePlayHistory() {
        if (mSuperPlayerView == null) return;
        int position = mSuperPlayerView.getVideoView().getCurrentPosition();
        VideoHistory history = null;
        if (mVideo != null && mVideoEpisode != null) {
            history = new VideoHistory(mVideo, mVideoEpisode, position);
        } else if(mVideo != null){
            history = new VideoHistory(mVideo, position);
        }else if (mTorrentFile != null) {
            history = new VideoHistory(mTorrentFile, position);
        }
        if(history != null)
        AsyTaskQueue.newInstance().execute(new SaveTaskListener(history));
    }

    /**
     * @param var1
     */
    private void playLive(String var1) {
        if (this.mVbyteManager != null) {
            this.mVbyteManager.closeNative();
            this.mVbyteManager = null;
        }
        this.mVbyteManager = Vbyte.getVbyte(getApplicationContext());
        var1 = this.mVbyteManager.getPlayPath(var1, 1);
        mSuperPlayerView.getVideoView().setVideoURI(Uri.parse(var1));
        mSuperPlayerView.getVideoView().start();
    }

    private SuperPlayerView.OnNetChangeListener changeListener = new SuperPlayerView.OnNetChangeListener() {
        /**
         * 网络链接监听类
         */
        @Override
        public void onWifi() {
            showToast("当前网络环境是WIFI");
        }

        @Override
        public void onMobile() {
            showToast("当前网络环境是手机网络");
        }

        @Override
        public void onDisConnect() {
            showToast("网络链接断开");
        }

        @Override
        public void onNoAvailable() {
            showToast("无网络链接");
        }

    };

    private RetrofitCallback<IPlayUrls> callback = new RefreshCallback<IPlayUrls>() {

        @Override
        public void onStart(int enqueueKey) {
            if (isFinishing()) return;
            mSuperPlayerView.setProgerssVisible(true);
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (isFinishing()) return;
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (isFinishing()) return;
            showToast(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, final IPlayUrls response) {
            if (isFinishing() || response == null ) return;
            if(response.isSuccess() && response.getUrls() != null && !response.getUrls().isEmpty() && !TextUtils.isEmpty(response.getUrls().entrySet().iterator().next().getValue())){
                final String value = response.getUrls().entrySet().iterator().next().getValue();
                if(response.getPlayType() == IVideoEpisode.PLAY_TYPE_WEB){
                    DialogUtil.showMaterialDialog(VideoPlayerActivity.this, "该视频需要使用网页播放", new OnButtonClickListener() {
                        @Override
                        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
                            if (btn == OnButtonClickListener.RIGHT){
                                WebPlayerActivity.startActivity(VideoPlayerActivity.this, value,response.getReferer());
                                VideoPlayerActivity.this.finish();
                            }
                            dialog.dismiss();
                        }
                    });
                }else if(response.getPlayType() == IVideoEpisode.PLAY_TYPE_ZZPLAYER){
                    DialogUtil.showMaterialDialog(VideoPlayerActivity.this, "该视频需要使用ZPlayer播放", new OnButtonClickListener() {
                        @Override
                        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
                            if (btn == OnButtonClickListener.RIGHT){
                                ZzplayerActivity.startActivity(VideoPlayerActivity.this,mSuperPlayerView.getVideoTitle().toString(),value);
                                VideoPlayerActivity.this.finish();
                            }
                            dialog.dismiss();
                        }
                    });
                }else if(response.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO){
                    mSuperPlayerView.play(value);
                }
            }else{
                showToast(getString(R.string.error_play_conn));
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
}
