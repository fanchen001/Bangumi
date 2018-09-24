package com.fanchen.imovie.view.video_new;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fanchen.imovie.util.AppUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by fanchen on 2017/4/28.
 * 播放器
 */
public class NiceVideoPlayer extends FrameLayout
        implements INiceVideoPlayer,
        TextureView.SurfaceTextureListener {
    /**
     * 播放错误
     **/
    public static final int STATE_ERROR = -1;
    /**
     * 播放未开始
     **/
    public static final int STATE_IDLE = 0;
    /**
     * 播放准备中
     **/
    public static final int STATE_PREPARING = 1;
    /**
     * 播放准备就绪
     **/
    public static final int STATE_PREPARED = 2;
    /**
     * 正在播放
     **/
    public static final int STATE_PLAYING = 3;
    /**
     * 暂停播放
     **/
    public static final int STATE_PAUSED = 4;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    /**
     * 播放完成
     **/
    public static final int STATE_COMPLETED = 7;

    /**
     * 普通模式
     **/
    public static final int MODE_NORMAL = 10;
    /**
     * 全屏模式
     **/
    public static final int MODE_FULL_SCREEN = 11;
    /**
     * 小窗口模式
     **/
    public static final int MODE_TINY_WINDOW = 12;

    /**
     * IjkPlayer
     **/
    public static final int TYPE_IJK = 111;
    /**
     * MediaPlayer
     **/
    public static final int TYPE_NATIVE = 222;

    private int mPlayerType = TYPE_IJK;
    private int mCurrentState = STATE_IDLE;
    private int mCurrentMode = MODE_NORMAL;

    private Context mContext;
    private boolean usingMediaCodec = false;
    private boolean usingMediaCodecAutoRotate = false;
    private boolean usingOpenSLES = false;
    private String pixelFormat = "";// Auto Select=,RGB 565=fcc-rv16,RGB
    // 888X=fcc-rv32,YV12=fcc-yv12,默认为RGB 888X
    private boolean isActivityFullScreen = false;
    private AudioManager mAudioManager;
    private IMediaPlayer mMediaPlayer;
    private FrameLayout mContainer;
    private NiceTextureView mTextureView;
    private NiceVideoPlayerController mController;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private String mUrl;
    private Map<String, String> mHeaders;
    private int mBufferPercentage;
    private boolean continueFromLastPosition = true;
    private long skipToPosition;
    private IMediaPlayer.OnErrorListener onErrorListener;
    private boolean showPlaySpeed = true;
    private Handler mmH = new Handler(Looper.getMainLooper());

    public NiceVideoPlayer(Context context) {
        this(context, null);
    }

    public NiceVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    public void setUp(String url, String ref) {
        Map<String, String> map = new HashMap<>();
        map.put("Referer", ref);
        map.put("Connection", "keep-alive");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("Accept-Language", "zh-CN,zh;q=0.9");
        map.put("Accept", "i*/*");
        map.put("Origin", ref);
        map.put("User-Agent", getUserAgent(getContext()));
        setUp(url, map);
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    private String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public void setUp(String url) {
        setUp(url, (Map<String, String>) null);
    }

    public void setUp(String url, Map<String, String> headers) {
        mUrl = url;
        mHeaders = headers;
        if (isActivityFullScreen) {
            start();
        }
    }

    public void setController(NiceVideoPlayerController controller) {
        mContainer.removeView(mController);
        mController = controller;
        mController.reset();
        mController.setNiceVideoPlayer(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);
    }

    /**
     * 设置播放器类型
     *
     * @param playerType IjkPlayer or MediaPlayer.
     */
    public void setPlayerType(int playerType) {
        mPlayerType = playerType;
    }

    /**
     * 是否从上一次的位置继续播放
     *
     * @param continueFromLastPosition true从上一次的位置继续播放
     */
    @Override
    public void continueFromLastPosition(boolean continueFromLastPosition) {
        this.continueFromLastPosition = continueFromLastPosition;
    }

    @Override
    public void setSpeed(float speed) {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            ((IjkMediaPlayer) mMediaPlayer).setSpeed(speed);
        } else {
            LogUtil.d("只有IjkPlayer才能设置播放速度");
        }
    }

    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE) {
            mmH.removeCallbacks(speedRunnable);
            if (showPlaySpeed) mmH.postDelayed(speedRunnable, 1000);
            NiceVideoPlayerManager.instance().setCurrentNiceVideoPlayer(this);
            initAudioManager();
            initMediaPlayer();
            initTextureView();
            addTextureView();
        } else {
            LogUtil.d("NiceVideoPlayer只有在mCurrentState == STATE_IDLE时才能调用start方法.");
        }
    }

    public void setPlaySpeed(boolean showPlaySpeed) {
        this.showPlaySpeed = showPlaySpeed;
    }

    @Override
    public void setActivityFullScreen(boolean full) {
        this.isActivityFullScreen = full;
    }

    @Override
    public boolean isActivityFullScreen() {
        return isActivityFullScreen;
    }

    @Override
    public void start(long position) {
        skipToPosition = position;
        start();
    }

    @Override
    public void restart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_PLAYING");
        } else if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_BUFFERING_PLAYING");
        } else if (mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR) {
            mMediaPlayer.reset();
            try {
                openMediaPlayer();
            } catch (Throwable e) {
                LogUtil.d("STATE_ERROR");
                mController.onPlayStateChanged(mCurrentState = STATE_ERROR);
            }
        } else {
            LogUtil.d("NiceVideoPlayer在mCurrentState == " + mCurrentState + "时不能调用restart()方法.");
        }
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_PAUSED");
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_BUFFERING_PAUSED");
        }
    }

    @Override
    public void seekTo(long pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void setVolume(int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return mCurrentMode == MODE_FULL_SCREEN;
    }


    @Override
    public boolean isTinyWindow() {
        return mCurrentMode == MODE_TINY_WINDOW;
    }

    @Override
    public boolean isNormal() {
        return mCurrentMode == MODE_NORMAL;
    }

    @Override
    public int getMaxVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public int getVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public long getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    @Override
    public float getSpeed(float speed) {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getSpeed(speed);
        }
        return 0;
    }

    @Override
    public long getTcpSpeed() {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getTcpSpeed();
        }
        return 0;
    }

    private void initAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            switch (mPlayerType) {
                case TYPE_NATIVE:
                    mMediaPlayer = new AndroidMediaPlayer();
                    break;
                default:
                    IjkMediaPlayer mediaPlayer = new IjkMediaPlayer();
//                    ((IjkMediaPlayer) mMediaPlayer).setOption(1, "analyzemaxduration", 100L);
//                    ((IjkMediaPlayer) mMediaPlayer).setOption(1, "flush_packets", 1L);
//                    ((IjkMediaPlayer) mMediaPlayer).setOption(4, "packet-buffering", 0L);
//                    ((IjkMediaPlayer) mMediaPlayer).setOption(4, "framedrop", 1L);
                    if (usingMediaCodec) {
                        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
                        if (usingMediaCodecAutoRotate) {
                            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                        } else {
                            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
                        }
                    } else {
                        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                    }
                    if (usingOpenSLES) {
                        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
                    } else {
                        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                    }
                    if (TextUtils.isEmpty(pixelFormat)) {
                        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                    } else {
                        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
                    }
                    mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);//支持h265硬解;
                    mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                    mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
                    mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                    mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000);
                    mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
                    mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
                    mMediaPlayer = mediaPlayer;
                    break;
            }
        }
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new NiceTextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mContainer.addView(mTextureView, 0, params);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surfaceTexture;
            try {
                openMediaPlayer();
            } catch (Throwable e) {
                LogUtil.d("STATE_ERROR");
                mController.onPlayStateChanged(mCurrentState = STATE_ERROR);
            }
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    private void openMediaPlayer() throws Throwable {
        // 屏幕常亮
        mContainer.setKeepScreenOn(true);
        // 设置监听
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnInfoListener(mOnInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mHeaders == null) {
                mHeaders = new HashMap<>();
                mHeaders.put("Accept-Encoding", "gzip, deflate");
                mHeaders.put("Accept-Language", "zh-CN,zh;q=0.9");
                mHeaders.put("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 5_1_1 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3 XiaoMi/MiuiBrowser/10.1.1");
            }
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl), mHeaders);
        } else {
            mMediaPlayer.setDataSource(mUrl);
        }
        if (mSurface == null) mSurface = new Surface(mSurfaceTexture);
        mMediaPlayer.setSurface(mSurface);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setScreenOnWhilePlaying(true);
        mMediaPlayer.prepareAsync();
        mController.onPlayStateChanged(mCurrentState = STATE_PREPARING);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(IMediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("onPrepared ——> STATE_PREPARED");
            mp.start();
            // 从上次的保存位置播放
            if (continueFromLastPosition && skipToPosition <= 0) {
                long savedPlayPosition = NiceUtil.getSavedPlayPosition(mContext, mUrl);
                mp.seekTo(savedPlayPosition);
            }
            // 跳到指定位置播放
            if (skipToPosition != 0) {
                mp.seekTo(skipToPosition);
            }
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            mTextureView.adaptVideoSize(width, height);
            LogUtil.d("onVideoSizeChanged ——> width：" + width + "， height：" + height);
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            mCurrentState = STATE_COMPLETED;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("onCompletion ——> STATE_COMPLETED");
            // 清除屏幕常亮
            mContainer.setKeepScreenOn(false);
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            // 直播流播放时去调用mediaPlayer.getDuration会导致-38和-2147483648错误，忽略该错误
            if (what != -38 && what != -2147483648 && extra != -38 && extra != -2147483648) {
                mCurrentState = STATE_ERROR;
                mController.onPlayStateChanged(mCurrentState);
                LogUtil.d("onError ——> STATE_ERROR ———— what：" + what + ", extra: " + extra);
            }
            if (onErrorListener != null) {
                onErrorListener.onError(mp, what, extra);
            }
            return true;
        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器开始渲染
                mCurrentState = STATE_PLAYING;
                mController.onPlayStateChanged(mCurrentState);
                LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
                mController.onPlayStateChanged(mCurrentState);
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    mController.onPlayStateChanged(mCurrentState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    mController.onPlayStateChanged(mCurrentState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                // 视频旋转了extra度，需要恢复
                if (mTextureView != null) {
                    mTextureView.setRotation(extra);
                    LogUtil.d("视频旋转角度：" + extra);
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                LogUtil.d("视频不能seekTo，为直播视频");
            } else {
                LogUtil.d("onInfo ——> what：" + what);
            }
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener
            = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            mBufferPercentage = percent;
        }
    };

    /**
     * 全屏，将mContainer(内部包含mTextureView和mController)从当前容器中移除，并添加到android.R.content中.
     * 切换横屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期
     */
    @Override
    public void enterFullScreen() {
        if (mCurrentMode == MODE_FULL_SCREEN) return;
        // 隐藏ActionBar、状态栏，并横屏
        NiceUtil.hideActionBar(mContext);
        NiceUtil.scanForActivity(mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(mContext).findViewById(android.R.id.content);
        if (mCurrentMode == MODE_TINY_WINDOW) {
            contentView.removeView(mContainer);
        } else {
            this.removeView(mContainer);
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);
        mCurrentMode = MODE_FULL_SCREEN;
        if (mController != null) mController.onPlayModeChanged(mCurrentMode);
        LogUtil.d("MODE_FULL_SCREEN");
    }

    /**
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     * 切换竖屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期.
     *
     * @return true退出全屏.
     */
    @Override
    public boolean exitFullScreen() {
        if (mCurrentMode == MODE_FULL_SCREEN) {
            NiceUtil.showActionBar(mContext);
            NiceUtil.scanForActivity(mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(mContext).findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);
            mCurrentMode = MODE_NORMAL;
            mController.onPlayModeChanged(mCurrentMode);
            LogUtil.d("MODE_NORMAL");
            return true;
        }
        return false;
    }

    /**
     * 进入小窗口播放，小窗口播放的实现原理与全屏播放类似。
     */
    @Override
    public void enterTinyWindow() {
        if (mCurrentMode == MODE_TINY_WINDOW) return;
        this.removeView(mContainer);
        ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(mContext).findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        LayoutParams params = new LayoutParams((int) (NiceUtil.getScreenWidth(mContext) * 0.6f), (int) (NiceUtil.getScreenWidth(mContext) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = NiceUtil.dp2px(mContext, 8f);
        params.bottomMargin = NiceUtil.dp2px(mContext, 8f);
        if (mContainer.getParent() != null) {
            ViewParent parent = mContainer.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mContainer);
            }
        }
        contentView.addView(mContainer, params);

        mCurrentMode = MODE_TINY_WINDOW;
        mController.onPlayModeChanged(mCurrentMode);
        LogUtil.d("MODE_TINY_WINDOW");
    }

    /**
     * 退出小窗口播放
     */
    @Override
    public boolean exitTinyWindow() {
        if (mCurrentMode == MODE_TINY_WINDOW) {
            ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(mContext).findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);
            mCurrentMode = MODE_NORMAL;
            mController.onPlayModeChanged(mCurrentMode);
            LogUtil.d("MODE_NORMAL");
            return true;
        }
        return false;
    }

    @Override
    public void releasePlayer() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(null);
            mAudioManager = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mContainer.removeView(mTextureView);
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCurrentState = STATE_IDLE;
    }

    public String getPlayerUrl() {
        return mUrl;
    }

    @Override
    public void release() {
        // 保存播放位置
        if (isPlaying() || isBufferingPlaying() || isBufferingPaused() || isPaused()) {
            NiceUtil.savePlayPosition(mContext, mUrl, getCurrentPosition());
        } else if (isCompleted()) {
            NiceUtil.savePlayPosition(mContext, mUrl, 0);
        }
        // 退出全屏或小窗口
        if (isFullScreen()) {
            exitFullScreen();
        }
        if (isTinyWindow()) {
            exitTinyWindow();
        }
        mCurrentMode = MODE_NORMAL;

        // 释放播放器
        releasePlayer();

        // 恢复控制器
        if (mController != null) {
            mController.reset();
        }
        mmH.removeCallbacks(speedRunnable);
        Runtime.getRuntime().gc();
    }

    private Runnable speedRunnable = new Runnable() {

        @Override
        public void run() {
            if (mController == null || mMediaPlayer == null) {
            } else if (mController.getSpeedTextView() == null || mController.getSpeedTextView().getParent() == null) {
                mController.showSpeed(false);
            } else if (((View) mController.getSpeedTextView().getParent()).getVisibility() == View.VISIBLE && mPlayerType == TYPE_IJK) {
                long tcpSpeed = ((IjkMediaPlayer) mMediaPlayer).getTcpSpeed();
                mController.showSpeed(true);
                mController.updateSpeed( AppUtil.getSize(tcpSpeed));
            } else {
                mController.showSpeed(false);
            }
            mmH.removeCallbacks(this);
            mmH.postDelayed(this, 1000);
        }

    };

}
