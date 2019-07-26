package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.entity.bmob.VideoHistory;
import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DateUtil;
import com.fanchen.imovie.view.video.SuperPlayerView;
import com.fanchen.sniffing.SniffingCallback;
import com.fanchen.sniffing.SniffingVideo;
import com.fanchen.sniffing.x5.SniffingUtil;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.List;
import java.util.Map;

/**
 * 直播
 * Created by fanchen on 2018/8/3.
 */
public class LivePlayerActivity extends BaseActivity {

    public static final String ISLIVE = "islive";
    public static final String VIDEO = "video";
    public static final String ORIENTATION = "orientation";

    protected SuperPlayerView mSuperPlayerView;

    private IBaseVideo mVideo;
    private String mDefPlayer = "";
    private long preTime = System.currentTimeMillis();


    /**
     * @param context
     * @param video
     */
    public static void startActivity(Context context, IVideo video) {
        Intent intent = new Intent(context, LivePlayerActivity.class);
        intent.putExtra(VIDEO, video);
        intent.putExtra(ISLIVE, true);
        intent.putExtra(ORIENTATION, false);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param video
     */
    public static void startActivity(Context context, IBaseVideo video) {
        Intent intent = new Intent(context, LivePlayerActivity.class);
        intent.putExtra(VIDEO, video);
        intent.putExtra(ISLIVE, true);
        intent.putExtra(ORIENTATION, true);
        context.startActivity(intent);
    }

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }

    @Override
    protected int getLayout() {
        return -1;
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, int layout) {
        return mSuperPlayerView = new SuperPlayerView(this);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mSuperPlayerView.setOnNetChangeListener(changeLisener);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
//        VideoUrlUtil.getInstance().setTimeOut(8 * 1000);
//        VideoUrlUtil.getInstance().setParserTime(2 * 1000);
        mVideo = getIntent().getParcelableExtra(VIDEO);
        mSuperPlayerView.setNetChangeListener(true);
        mSuperPlayerView.setScaleType(SuperPlayerView.SCALETYPE_FITPARENT);
        mSuperPlayerView.setLive(getIntent().getBooleanExtra(ISLIVE, false));
        mSuperPlayerView.setFullScreenOnly(getIntent().getBooleanExtra(ORIENTATION, true));
        mSuperPlayerView.setPlayerWH(0, mSuperPlayerView.getMeasuredHeight());
        if (getIntent().hasExtra(VIDEO) && mVideo != null) {
            mSuperPlayerView.setAutoSpeend(true);
            mSuperPlayerView.setTitle(mVideo.getTitle());
            if (mVideo.getSource() == IVideo.SOURCE_PLAY) {
                mSuperPlayerView.play(mVideo.getUrl());
            } else {
                RetrofitManager retrofit = getRetrofitManager();
                String serviceClassName = mVideo.getServiceClass();
                String url = RetrofitManager.REQUEST_URL = mVideo.getUrl();
                String method = mVideo instanceof IVideo ? "playUrl" : "liveUrl";
                retrofit.enqueue(serviceClassName, callback, method, url);
            }
        } else {
            showToast(R.string.non_error);
            finish();
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
        SniffingUtil.get().releaseAll();
//        VideoUrlUtil.getInstance().setParserTime(VideoUrlUtil.PARSER_TIME);
//        VideoUrlUtil.getInstance().setTimeOut(VideoUrlUtil.DEFAULT_TIME);
//        VideoUrlUtil.getInstance().destroy();
        savePlayHistory();
    }

    @Override
    public void onBackPressed() {
        long l = System.currentTimeMillis();
        if (l - preTime < 2000) {
            super.onBackPressed();
        } else if (l - preTime >= 2000) {
            preTime = l;
            showToast("再按一次退出");
        }
    }

    public void savePlayHistory() {
        if (mSuperPlayerView == null) return;
        int position = mSuperPlayerView.getVideoView().getCurrentPosition();
        VideoHistory history = null;
        if (mVideo != null) {
            history = new VideoHistory(mVideo, position);
            AsyTaskQueue.newInstance().execute(new SaveTaskListener(history));
        }
    }

    private SuperPlayerView.OnNetChangeListener changeLisener = new SuperPlayerView.OnNetChangeListener() {
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

//    private class ParseWebUrl implements VideoUrlUtil.OnParseWebUrlListener {
//        private IPlayUrls playUrls;
//
//        public ParseWebUrl(IPlayUrls playUrls) {
//            this.playUrls = playUrls;
//        }
//
//        @Override
//        public void onFindUrl(String videourl) {
//            if (mSuperPlayerView == null || playUrls == null) return;
//            mSuperPlayerView.setProgerssVisible(false);
//            if (getIntent().getBooleanExtra(ISLIVE, false) && getIntent().getBooleanExtra(ORIENTATION, true)
//                    && VideoPlayerActivity.TBS.equals(mDefPlayer)) {
//                TbsVideo.openVideo(LivePlayerActivity.this, videourl);
//                LivePlayerActivity.this.finish();
//            } else {
//                String referer = playUrls.getReferer();
//                if (playUrls.m3u8Referer()) {
//                    mSuperPlayerView.play(videourl, referer);
//                } else {
//                    mSuperPlayerView.play(videourl);
//                }
//            }
//        }
//
//        @Override
//        public void onError(String errorMsg) {
//            showToast(errorMsg);
//            finish();
//        }
//
//    }


    private RefreshCallback<IPlayUrls> callback = new RefreshCallback<IPlayUrls>() {

        private IPlayUrls mIPlayUrls;

        @Override
        public void onStart(int enqueueKey) {
            if (mSuperPlayerView == null) return;
            mSuperPlayerView.setProgerssVisible(true);
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (mSuperPlayerView == null) return;
            if (mIPlayUrls == null || mIPlayUrls.getPlayType() == IVideoEpisode.PLAY_TYPE_VIDEO
                    || mIPlayUrls.getPlayType() == IVideoEpisode.PLAY_TYPE_ZZPLAYER) {
                mSuperPlayerView.setProgerssVisible(false);
            }
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            showToast(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, final IPlayUrls response) {
            this.mIPlayUrls = response;
            if (mIPlayUrls != null && mSuperPlayerView != null && mIPlayUrls.isSuccess()) {
                Map<String, String> urls = mIPlayUrls.getUrls();
                if (urls != null && !urls.isEmpty()) {
                    int playType = mIPlayUrls.getPlayType();
                    final String value = mIPlayUrls.getUrls().entrySet().iterator().next().getValue();
                    if (!TextUtils.isEmpty(value) && playType == IVideoEpisode.PLAY_TYPE_VIDEO || playType == IVideoEpisode.PLAY_TYPE_VIDEO_M3U8) {
                        if (!TextUtils.isEmpty(mIPlayUrls.getReferer())) {
                            mSuperPlayerView.play(value, mIPlayUrls.getReferer());
                        } else {
                            mSuperPlayerView.play(value);
                        }
                    } else if (playType == IVideoEpisode.PLAY_TYPE_WEB) {
                        String referer = mIPlayUrls.getReferer();
                        SniffingUtil.get().activity(LivePlayerActivity.this).url(value).referer(referer).callback(new SniffingCallback() {

                            @Override
                            public void onSniffingSuccess(View webView, String url, List<SniffingVideo> videos) {
                                if (mSuperPlayerView == null || mIPlayUrls == null || videos.isEmpty())
                                    return;
                                String videourl = videos.get(0).getUrl();
                                mSuperPlayerView.setProgerssVisible(false);
                                if (getIntent().getBooleanExtra(ISLIVE, false) && getIntent().getBooleanExtra(ORIENTATION, true)
                                        && VideoPlayerActivity.TBS.equals(mDefPlayer)) {
                                    TbsVideo.openVideo(LivePlayerActivity.this, videourl);
                                    LivePlayerActivity.this.finish();
                                } else {
                                    String referer = mIPlayUrls.getReferer();
                                    if (mIPlayUrls.m3u8Referer()) {
                                        mSuperPlayerView.play(videourl, referer);
                                    } else {
                                        mSuperPlayerView.play(videourl);
                                    }
                                }
                            }

                            @Override
                            public void onSniffingError(View webView, String url, int errorCode) {
                                showToast("解析视频失败");
                                finish();
                            }

                        }).start();
//                        ParseWebUrl parseWebUrl = new ParseWebUrl(mIPlayUrls);
//                        VideoUrlUtil.getInstance().init(LivePlayerActivity.this, value, referer).setOnParseListener(parseWebUrl).startParse();
                    } else {
                        showToast(getString(R.string.error_play_conn));
                    }
                } else {
                    showToast(getString(R.string.error_play_conn));
                }
            } else {
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

}
