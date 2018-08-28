package com.fanchen.imovie.view.video_new;

import android.widget.Toast;

/**
 * Created by fanchen on 2017/5/5.
 * 视频播放器管理器.
 */
public class NiceVideoPlayerManager {

    private long preTime = System.currentTimeMillis();

    private NiceVideoPlayer mVideoPlayer;

    private NiceVideoPlayerManager() {
    }

    private static NiceVideoPlayerManager sInstance;

    public static synchronized NiceVideoPlayerManager instance() {
        if (sInstance == null) {
            sInstance = new NiceVideoPlayerManager();
        }
        return sInstance;
    }

    public NiceVideoPlayer getCurrentNiceVideoPlayer() {
        return mVideoPlayer;
    }

    public void setCurrentNiceVideoPlayer(NiceVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releaseNiceVideoPlayer();
            mVideoPlayer = videoPlayer;
        }
    }

    public void pauseNiceVideoPlayer() {
        if (mVideoPlayer != null && (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying())) {
            mVideoPlayer.pause();
        }
    }

    public void resumeNiceVideoPlayer() {
        if (mVideoPlayer != null && (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused())) {
            if(mVideoPlayer.isActivityFullScreen()){
                mVideoPlayer.enterFullScreen();
            }
            mVideoPlayer.restart();
        }
    }

    public void releaseNiceVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    public boolean onBackPressd() {
        if (mVideoPlayer != null) {
            long l = System.currentTimeMillis();
            if (mVideoPlayer.isActivityFullScreen() && l - preTime < 2000) {
                return false;
            } else if (mVideoPlayer.isActivityFullScreen() && l - preTime >= 2000) {
                preTime = l;
                Toast.makeText(mVideoPlayer.getContext(), "再按一次退出", Toast.LENGTH_LONG).show();
                return true;
            } else if (!mVideoPlayer.isActivityFullScreen() && mVideoPlayer.isFullScreen()) {
                return mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                return mVideoPlayer.exitTinyWindow();
            }
        }
        return false;
    }
}
