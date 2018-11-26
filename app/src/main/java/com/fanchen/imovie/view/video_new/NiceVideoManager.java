package com.fanchen.imovie.view.video_new;

import android.widget.Toast;

/**
 * Created by fanchen on 2017/5/5.
 * 视频播放器管理器.
 */
public class NiceVideoManager {

    private long preTime = System.currentTimeMillis();

    private boolean isUserisPause = false;
    private NiceVideoPlayer mVideoPlayer;

    private NiceVideoManager() {
    }

    private static NiceVideoManager sInstance;

    public static synchronized NiceVideoManager instance() {
        if (sInstance == null) {
            sInstance = new NiceVideoManager();
        }
        return sInstance;
    }

    public NiceVideoPlayer getCurrent() {
        return mVideoPlayer;
    }

    public void setCurrent(NiceVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            release();
            mVideoPlayer = videoPlayer;
        }
    }

    public void pause() {
        if (mVideoPlayer == null) return;
        isUserisPause = mVideoPlayer.isPaused() && !mVideoPlayer.isBufferingPaused();
        if (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying()) {
            mVideoPlayer.pause();
        }
    }

    public void resume() {
        if (mVideoPlayer == null) return;
        if (mVideoPlayer.isActivityFullScreen()) {
            mVideoPlayer.enterFullScreen();
        }
        if (isUserisPause) {
            isUserisPause = false;
        } else if (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused()) {
            mVideoPlayer.restart();
        }
    }

    public void release() {
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
