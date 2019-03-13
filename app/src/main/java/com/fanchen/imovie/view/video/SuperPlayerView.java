package com.fanchen.imovie.view.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.WebPlayerActivity;
import com.fanchen.imovie.util.AppUtil;
import com.fanchen.imovie.util.NetworkUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 类描述：视频播放控制类
 *
 * @author Super南仔
 * @time 2016-9-19
 */
public class SuperPlayerView extends RelativeLayout {
    /**
     * fitParent:scale the video uniformly (maintain the video's aspect ratio)
     * so that both dimensions (width and height) of the video will be equal to
     * or **less** than the corresponding dimension of the view. like
     * ImageView's `CENTER_INSIDE`.等比缩放,画面填满view。
     */
    public static final String SCALETYPE_FITPARENT = "fitParent";
    /**
     * fillParent:scale the video uniformly (maintain the video's aspect ratio)
     * so that both dimensions (width and height) of the video will be equal to
     * or **larger** than the corresponding dimension of the view .like
     * ImageView's `CENTER_CROP`.等比缩放,直到画面宽高都等于或小于view的宽高。
     */
    public static final String SCALETYPE_FILLPARENT = "fillParent";
    /**
     * wrapContent:center the video in the view,if the video is less than view
     * perform no scaling,if video is larger than view then scale the video
     * uniformly so that both dimensions (width and height) of the video will be
     * equal to or **less** than the corresponding dimension of the view.
     * 将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中。
     */
    public static final String SCALETYPE_WRAPCONTENT = "wrapContent";
    /**
     * fitXY:scale in X and Y independently, so that video matches view
     * exactly.不剪裁,非等比例拉伸画面填满整个View
     */
    public static final String SCALETYPE_FITXY = "fitXY";
    /**
     * 16:9:scale x and y with aspect ratio 16:9 until both dimensions (width
     * and height) of the video will be equal to or **less** than the
     * corresponding dimension of the view.不剪裁,非等比例拉伸画面到16:9,并完全显示在View中。
     */
    public static final String SCALETYPE_16_9 = "16:9";
    /**
     * 4:3:scale x and y with aspect ratio 4:3 until both dimensions (width and
     * height) of the video will be equal to or **less** than the corresponding
     * dimension of the view.不剪裁,非等比例拉伸画面到4:3,并完全显示在View中。
     */
    public static final String SCALETYPE_4_3 = "4:3";
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    private static final int MESSAGE_FADE_OUT = 2;
    private static final int MESSAGE_SEEK_NEW_POSITION = 3;
    private static final int MESSAGE_HIDE_CENTER_BOX = 4;
    private static final int MESSAGE_RESTART_PLAY = 5;
    private Activity activity;
    private Context context;
    private View contentView;
    private IjkVideoView videoView;
    private SeekBar seekBar;
    private AudioManager audioManager;
    private RelativeLayout mRelativeLayout;
    private TextView mTextView;
    private int mMaxVolume;
    private boolean playerSupport;
    private String url;
    private String errorUrl;
    private Query $;
    public static int STATUS_ERROR = -1;
    public static int STATUS_IDLE = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_PLAYING = 2;
    public static int STATUS_PAUSE = 3;
    public static int STATUS_COMPLETED = 4;
    private long pauseTime;
    private int status = STATUS_IDLE;
    private boolean isLive = false;// 是否为直播
    private boolean isShowCenterControl = false;// 是否显示中心控制器
    private boolean isHideControl = false;//是否隐藏视频控制栏
    private boolean isShowTopControl = true;//是否显示头部显示栏，true：竖屏也显示 false：竖屏不显示，横屏显示
    private boolean isSupportGesture = false;//是否至此手势操作，false ：小屏幕的时候不支持，全屏的支持；true : 小屏幕还是全屏都支持
    //    private boolean isPrepare = false;// 是否已经初始化播放
    private boolean isNetListener = true;// 是否添加网络监听 (默认是监听)
    // 网络监听回调
    private NetChangeReceiver netChangeReceiver;
    private OnNetChangeListener onNetChangeListener;
    private boolean isShowBottomControl = true;

    private OrientationEventListener orientationEventListener;
    private OnPlayStateChangeListener onPlayStateChangeListener;
    private int defaultTimeout = 5000;
    private int screenWidthPixels;

    private int initWidth = 0;
    private int initHeight = 0;
    private boolean isShowing;
    private boolean portrait;
    private float brightness = -1;
    private int volume = -1;
    private long newPosition = -1;
    private long defaultRetryTime = 5000;
    private OnErrorListener onErrorListener;
    private OnInfoListener onInfoListener;
    private OnPreparedListener onPreparedListener;
    private OnClickListener onErrorClickListener;
    private AdapterView.OnItemClickListener onItemClickListener;
    private Handler mmH = new Handler(Looper.getMainLooper());
    private ListPopupWindow mPopupWindow;

    public void setDefinition(boolean isDefinition) {
        if ($ == null) return;
        if (isDefinition) {
            $.id(R.id.tv_definition).visibility(View.VISIBLE);
            $.id(R.id.view_jky_play_iv_setting).visibility(View.VISIBLE);
        } else {
            $.id(R.id.tv_definition).visibility(View.GONE);
            $.id(R.id.view_jky_play_iv_setting).visibility(View.GONE);
        }
    }

    private Runnable oncomplete = new Runnable() {
        @Override
        public void run() {
        }
    };

    /**
     * 相应点击事件
     */
    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.view_jky_player_fullscreen) {
                toggleFullScreen();
            } else if (v.getId() == R.id.tv_definition) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
                mPopupWindow = new ListPopupWindow(activity);
                mPopupWindow.setAdapter(new ArrayAdapter<>(activity, R.layout.item_textview, new String[]{"标清", "高清"}));
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#88000000")));
                mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setOnItemClickListener(mItemClickListener);
                mPopupWindow.setAnchorView(v);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点 此处show_btn为按钮
                mPopupWindow.setModal(true);//设置是否是模式
                mPopupWindow.show();
            } else if (v.getId() == R.id.view_jky_player_tv_web) {
                WebPlayerActivity.Companion.startActivity(activity, TextUtils.isEmpty(errorUrl) ? url : errorUrl);
            } else if (v.getId() == R.id.app_video_play) {
                doPauseResume();
                show(defaultTimeout);
            } else if (v.getId() == R.id.view_jky_player_center_play) {
                // videoView.seekTo(0);
                // videoView.start();
                doPauseResume();
                show(defaultTimeout);
            } else if (v.getId() == R.id.app_video_finish) {
                if (!fullScreenOnly && !portrait) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    activity.finish();
                }
            } else if (v.getId() == R.id.view_jky_player_tv_continue) {
                isNetListener = false;// 取消网络的监听
                $.id(R.id.view_jky_player_tip_control).gone();
                play(url, currentPosition, null);
            }
        }
    };

    public SuperPlayerView(Context context) {
        this(context, null);
    }

    public SuperPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        activity = (Activity) this.context;
        //初始化view和其他相关的
        initView();
    }

    /**
     * @param onClickListener
     */
    public void setDefinitionListener(AdapterView.OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }

    public String getUrl() {
        return url;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public void setOnErrorClickListener(OnClickListener onClickListener) {
        this.onErrorClickListener = onClickListener;
    }


    /**
     * try to play when error(only for live video)
     *
     * @param defaultRetryTime millisecond,0 will stop retry,default is 5000 millisecond
     */
    public void setDefaultRetryTime(long defaultRetryTime) {
        this.defaultRetryTime = defaultRetryTime;
    }

    private int currentPosition;
    private boolean fullScreenOnly;
    private CharSequence videoTitle = "";

    public SuperPlayerView setTitle(CharSequence title) {
        videoTitle = title;
        $.id(R.id.app_video_title).text(title);
        return this;
    }

    public CharSequence getVideoTitle() {
        return videoTitle;
    }

    private void doPauseResume() {
        if (status == STATUS_COMPLETED) {
            if (isShowCenterControl) {
                $.id(R.id.view_jky_player_center_control).visible();
            }
            videoView.seekTo(0);
            statusChange(STATUS_PLAYING);
            videoView.start();
        } else if (videoView.isPlaying()) {
            statusChange(STATUS_PAUSE);
            videoView.pause();
        } else {
            statusChange(STATUS_PLAYING);
            videoView.start();
        }
        updatePausePlay();
    }

    /**
     * 更新暂停状态的控件显示
     */
    private void updatePausePlay() {
        $.id(R.id.view_jky_player_center_control).visibility(
                isShowCenterControl ? View.VISIBLE : View.GONE);
        if (videoView.isPlaying()) {
            $.id(R.id.app_video_play).image(R.drawable.ic_pause);
            $.id(R.id.view_jky_player_center_play).image(R.drawable.ic_center_pause);
        } else {
            $.id(R.id.app_video_play).image(R.drawable.ic_play);
            $.id(R.id.view_jky_player_center_play).image(R.drawable.ic_center_play);
        }
    }

    /**
     * @param timeout
     */
    private void show(int timeout) {
        if (isHideControl) {
            showBottomControl(false);
            showCenterControl(false);
            showTopControl(false);
            return;
        }
        if (!isShowing /*&& isPrepare*/) {
            if (!isShowTopControl/* && portrait*/) {
                showTopControl(false);
            } else {
                showTopControl(true);
            }
            if (isShowCenterControl) {
                $.id(R.id.view_jky_player_center_control).visible();
            }
            showBottomControl(true);
            if (!fullScreenOnly) {
                $.id(R.id.view_jky_player_fullscreen).visible();
            }
            isShowing = true;
        }
        updatePausePlay();
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        handler.removeMessages(MESSAGE_FADE_OUT);
        if (timeout != 0) {
            handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_FADE_OUT), timeout);
        }
    }

    public void setIsShowBottomControl(boolean isShowBottomControl) {
        this.isShowBottomControl = isShowBottomControl;
        if (!isShowBottomControl) {
            showBottomControl(isShowBottomControl);
        }
    }

    /**
     * 隐藏显示底部控制栏
     *
     * @param show true ： 显示 false ： 隐藏
     */
    private void showBottomControl(boolean show) {
        if (isShowBottomControl) {
            $.id(R.id.app_video_bottom_box).visibility(show ? View.VISIBLE : View.GONE);
            if (isLive) {// 直播需要隐藏和显示一些底部的一些控件
                $.id(R.id.app_video_bottom_box).visibility(View.GONE);
//                $.id(R.id.app_video_play).gone();
//                $.id(R.id.app_video_currentTime).gone();
//                $.id(R.id.app_video_endTime).gone();
//                $.id(R.id.app_video_seekBar).gone();
//                $.id(R.id.view_jky_player_tv_number).gone();
            }
        } else {
            $.id(R.id.app_video_bottom_box).visibility(View.GONE);
        }


    }

    /**
     * 隐藏和显示头部的一些控件
     *
     * @param show
     */
    private void showTopControl(boolean show) {
        $.id(R.id.app_video_top_box).visibility(show ? View.VISIBLE : View.GONE);
        if (isLive) {// 对直播特定控件隐藏显示

        }
    }

    private long duration;
    private boolean instantSeeking;
    private boolean isDragging;
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (!fromUser)
                return;
            $.id(R.id.view_jky_player_tip_control).gone();// 移动时隐藏掉状态image
            int newPosition = (int) ((duration * progress * 1.0) / 1000);
            String time = generateTime(newPosition);
            if (instantSeeking) {
                videoView.seekTo(newPosition);
            }
            $.id(R.id.app_video_currentTime).text(time);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            show(3600000);
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            if (instantSeeking) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!instantSeeking) {
                videoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
            }
            show(defaultTimeout);
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isDragging = false;
            handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
        }
    };

    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FADE_OUT:
                    hide(false);
                    break;
                case MESSAGE_HIDE_CENTER_BOX:
                    $.id(R.id.app_video_volume_box).gone();
                    $.id(R.id.app_video_brightness_box).gone();
                    $.id(R.id.app_video_fastForward_box).gone();
                    break;
                case MESSAGE_SEEK_NEW_POSITION:
                    if (!isLive && newPosition >= 0) {
                        videoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShowing) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                        updatePausePlay();
                    }
                    break;
                case MESSAGE_RESTART_PLAY:
                    play(url);
                    break;
            }
        }
    };

    /**
     * 初始化视图
     */
    public void initView() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport = true;
        } catch (Throwable e) {
            Log.e("GiraffePlayer", "loadLibraries error", e);
        }
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
        $ = new Query(activity);
        contentView = View.inflate(context, R.layout.view_super_player, this);
        videoView = (IjkVideoView) contentView.findViewById(R.id.video_view);
        mTextView = (TextView) contentView.findViewById(R.id.tv_speed);
        mRelativeLayout = (RelativeLayout) contentView.findViewById(R.id.app_video_loading);
        videoView
                .setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(IMediaPlayer mp) {
                        statusChange(STATUS_COMPLETED);
                        oncomplete.run();
                    }
                });
        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                statusChange(STATUS_ERROR);
                if (onErrorListener != null) {
                    onErrorListener.onError(what, extra);
                }
                return true;
            }
        });
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        statusChange(STATUS_LOADING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        statusChange(STATUS_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        statusChange(STATUS_PLAYING);
                        break;
                }
                if (onInfoListener != null) {
                    onInfoListener.onInfo(what, extra);
                }
                return false;
            }
        });
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(IMediaPlayer mp) {
//                isPrepare = true;

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        hide(false);
                        show(defaultTimeout);
                    }
                }, 500);
                if (onPreparedListener != null) {
                    onPreparedListener.onPrepared();
                }
            }
        });
        seekBar = (SeekBar) contentView.findViewById(R.id.app_video_seekBar);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        $.id(R.id.app_video_play).clicked(onClickListener);
        $.id(R.id.tv_definition).clicked(onClickListener);
        $.id(R.id.view_jky_player_fullscreen).clicked(onClickListener);
        $.id(R.id.app_video_finish).clicked(onClickListener);
        $.id(R.id.view_jky_player_center_play).clicked(onClickListener);
        $.id(R.id.view_jky_player_tv_continue).clicked(onClickListener);
        $.id(R.id.view_jky_player_tv_web).clicked(onClickListener);
        audioManager = (AudioManager) activity
                .getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final GestureDetector gestureDetector = new GestureDetector(activity, new PlayerGestureListener());

        View liveBox = contentView.findViewById(R.id.app_video_box);
        liveBox.setClickable(true);
        liveBox.setFocusable(true);

        liveBox.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent))
                    return true;

                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }

                return false;
            }
        });


        /**
         * 监听手机重力感应的切换屏幕的方向
         */
        orientationEventListener = new OrientationEventListener(activity) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 0 && orientation <= 30 || orientation >= 330
                        || (orientation >= 150 && orientation <= 210)) {
                    // 竖屏
                    if (portrait) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120)
                        || (orientation >= 240 && orientation <= 300)) {
                    if (!portrait) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                }
            }
        };

        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        portrait = getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        hideAll(true);
        if (!playerSupport) {
            showStatus(activity.getResources().getString(R.string.not_support), "重试");
        }
    }

    /**
     * @param show
     */
    public void setAutoSpeend(boolean show) {
        mmH.removeCallbacks(speedRunnable);
        if (show) {
            setShowSpeed(VISIBLE);
            mmH.postDelayed(speedRunnable, 1000);
        } else {
            setShowSpeed(GONE);
        }
    }

    /**
     * @param visible
     */
    public void setShowSpeed(int visible) {
        if (mTextView != null)
            mTextView.setVisibility(visible);
    }

    /**
     * @param speed
     */
    public void setSpeed(String speed) {
        setShowSpeed(VISIBLE);
        if (mTextView != null)
            mTextView.setText(speed);
    }

    private View getViewParent(View v) {
        if (v == null) return null;
        if (v.getParent() != null) {
            return (View) v.getParent();
        }
        return null;
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            handler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            handler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        }
        handler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
        handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);

    }

    private void statusChange(int newStatus, boolean isView) {
        status = newStatus;
        if (isView) {
            if (!isLive && newStatus == STATUS_COMPLETED) {// 当视频播放完成的时候
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                hideAll(false);
                if (isShowCenterControl) {
                    $.id(R.id.view_jky_player_center_control).visible();
                }
            } else if (newStatus == STATUS_ERROR) {
                handler.removeMessages(MESSAGE_SHOW_PROGRESS);
                hideAll(false);
                if (isLive) {
                    showStatus(activity.getResources().getString(
                            R.string.small_problem), "重试");
                    if (defaultRetryTime > 0) {
                        handler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, defaultRetryTime);
                    }
                } else {
                    showStatus(activity.getResources().getString(
                            R.string.small_problem), "重试");
                }
            } else if (newStatus == STATUS_LOADING) {
                hideAll(false);
                $.id(R.id.app_video_loading).visible();
            } else if (newStatus == STATUS_PLAYING) {
                if (isFristPlay) {
                    isFristPlay = false;
                    $.id(R.id.view_jky_player_center_control).gone();
                    $.id(R.id.view_jky_player_fullscreen).invisible();
                    $.id(R.id.view_jky_player_tip_control).gone();
                    return;
                }
                hideAll(false);
            }
        }
//        if(shadeView != null && newStatus == STATUS_PAUSE && videoView != null && videoView.getCurrentPosition() > 0){
//            shadeView.setVisibility(View.GONE);
//        }else if(shadeView != null){
//            shadeView.setVisibility(View.VISIBLE);
//        }
        if (onPlayStateChangeListener != null) {
            onPlayStateChangeListener.onStateChange(newStatus);
        }
    }

    /**
     * 视频播放状态的改变
     *
     * @param newStatus
     */
    private void statusChange(int newStatus) {
        statusChange(newStatus, true);
    }


    public void setOnPlayStateChangeListener(OnPlayStateChangeListener onPlayStateChangeListener) {
        this.onPlayStateChangeListener = onPlayStateChangeListener;
    }

    private boolean isFristPlay = true;

    /**
     * 隐藏全部的控件
     */
    private void hideAll(boolean isFrist) {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        $.id(R.id.view_jky_player_center_control).gone();
        $.id(R.id.app_video_loading).gone();
        $.id(R.id.view_jky_player_fullscreen).invisible();
        $.id(R.id.view_jky_player_tip_control).gone();
        if (!isFrist) {
            showBottomControl(false);
            showTopControl(false);
        }
    }

    /**
     * 暂停
     */
    public void onPause() {
        pauseTime = System.currentTimeMillis();
        show(0);// 把系统状态栏显示出来
        if (status == STATUS_PLAYING) {
            statusChange(STATUS_PAUSE);
            videoView.pause();
            if (!isLive) {
                currentPosition = videoView.getCurrentPosition();
            }
        }
    }

    public void onResume() {
        pauseTime = 0;
        if (status == STATUS_PLAYING) {
            if (isLive) {
                videoView.seekTo(0);
            } else {
                if (currentPosition > 0) {
                    videoView.seekTo(currentPosition);
                }
            }
            statusChange(STATUS_PLAYING);
            videoView.start();
        }
    }

    /**
     * 监听全屏跟非全屏
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);
    }

    public boolean getPortrait() {
        return portrait;
    }

    /**
     * 得到屏幕宽度
     *
     * @return 宽度
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    private void doOnConfigurationChanged(final boolean portrait) {
        if (videoView != null && !fullScreenOnly) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        int screenWidth = getScreenWidth(activity);
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        if (layoutParams == null) {
                            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        }
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        layoutParams.height = initHeight;
                        if (initHeight == 0) {
                            layoutParams.height = screenWidth * 9 / 16;
                        }
                        layoutParams.width = initWidth;
                        if (initWidth == 0) {
                            layoutParams.width = screenWidth;
                        }

                        setLayoutParams(layoutParams);
                        requestLayout();
                        if (videoView != null && videoView.getLayoutParams() != null) {
                            ViewGroup.LayoutParams params = videoView.getLayoutParams();
                            params.width = layoutParams.width;
                            params.height = layoutParams.height;
                            videoView.setLayoutParams(params);
                            videoView.requestLayout();
                        }
                        Log.e("==>", "height." + layoutParams.height + ".width" + layoutParams.width);
                    } else {
                        int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = heightPixels;
                        layoutParams.width = widthPixels;
                        setLayoutParams(layoutParams);
                        requestLayout();
                        if (videoView != null && videoView.getLayoutParams() != null) {
                            ViewGroup.LayoutParams params = videoView.getLayoutParams();
                            params.width = widthPixels;
                            params.height = heightPixels;
                            videoView.setLayoutParams(params);
                            videoView.requestLayout();
                        }
                        Log.e("==>", "height." + layoutParams.height + ".width" + layoutParams.width);
                    }
                    updateFullScreenButton();
                    statusChange(isPlaying() ? STATUS_PLAYING : STATUS_PAUSE);
                }
            });
            orientationEventListener.enable();
        }
    }

    // TODO
    private void tryFullScreen(boolean fullScreen) {
        if (activity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) activity)
                    .getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }
    // TODO 这个是防止项目没有引用v7包
//	private void tryFullScreen(boolean fullScreen) {
//		if (activity instanceof Activity) {
//			android.app.ActionBar supportActionBar = ((Activity) activity)
//					.getActionBar();
//			if (supportActionBar != null) {
//				if (fullScreen) {
//					supportActionBar.hide();
//				} else {
//					supportActionBar.show();
//				}
//			}
//		}
//		setFullScreen(fullScreen);
//	}

    private void setFullScreen(boolean fullScreen) {
        if (activity != null) {
            WindowManager.LayoutParams attrs = activity.getWindow()
                    .getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setAttributes(attrs);
                activity.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    /**
     * 在activity中的onDestroy中需要回调
     */
    public void onDestroy() {
        mmH.removeCallbacks(speedRunnable);
        unregisterNetReceiver();// 取消网络变化的监听
        orientationEventListener.disable();
        handler.removeCallbacksAndMessages(null);
        statusChange(STATUS_PAUSE);
        videoView.stopPlayback();
    }

    /**
     * 显示错误信息
     *
     * @param statusText 错误提示
     * @param btnText    错误按钮提示
     */
    private void showStatus(String statusText, String btnText) {
        $.id(R.id.view_jky_player_tip_control).visible();
        $.id(R.id.view_jky_player_tip_text).text(statusText);
        $.id(R.id.view_jky_player_tv_continue).text(btnText);
//        isPrepare = false;// 设置点击不能出现控制栏
        $.id(R.id.view_jky_player_tv_web).gone();
        if (activity.getResources().getString(R.string.small_problem).equals(statusText)) {
            if (btnText.equals("重试")) {
                if (onErrorClickListener != null)
                    $.id(R.id.view_jky_player_tv_web).clicked(onErrorClickListener);
                $.id(R.id.view_jky_player_tv_web).visible();
            }
        }
    }

    /**
     * 开始播放
     *
     * @param url 播放视频的地址
     */
    public void play(String url) {
        if (TextUtils.isEmpty(url)) return;
        this.url = url;
        $.id(R.id.app_video_loading).visible();
        play(url, 0, null);
    }

    public void play(String url, String referer) {
        if (TextUtils.isEmpty(url)) return;
        this.url = url;
        $.id(R.id.app_video_loading).visible();
        Map<String, String> map = new HashMap<>();
        map.put("Referer", referer);
        play(url, 0, map);
    }

    public void playUrl(String url) {
        this.url = url;
    }

    public void play() {
        play(url, 0, null);
    }

    /**
     * @param url             开始播放(可播放指定位置)
     * @param currentPosition 指定位置的大小(0-1000)
     * @see （一般用于记录上次播放的位置或者切换视频源）
     */
    public void play(String url, int currentPosition, Map<String, String> header) {
        if (TextUtils.isEmpty(url)) return;
        this.url = url;
        if (!isNetListener) {// 如果设置不监听网络的变化，则取消监听网络变化的广播
            unregisterNetReceiver();
        } else {
            // 注册网路变化的监听
            registerNetReceiver();
        }
        if (videoView != null) {
            release();
        }
        if (isNetListener && (NetworkUtil.getNetworkType(activity) == 2 || NetworkUtil
                .getNetworkType(activity) == 4)) {// 手机网络的情况下
            $.id(R.id.view_jky_player_tip_control).visible();
        } else {
            if (playerSupport) {
                $.id(R.id.app_video_loading).visible();
                if (TextUtils.isEmpty(url)) return;
                videoView.setVideoURI(Uri.parse(url), header);
                if (isLive) {
                    videoView.seekTo(0);
                } else {
                    seekTo(currentPosition, true);
                }
                statusChange(STATUS_PLAYING);
                videoView.start();
            }
        }
    }

    /**
     * 播放切换视频源地址
     *
     * @param url
     */
    public void playSwitch(String url) {
        this.url = url;
        if (videoView.isPlaying()) {
            getCurrentPosition();
        }
        play(url, (int) currentPosition, null);
    }

    /**
     * 格式化显示的时间
     *
     * @param time
     * @return
     */
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
                seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    private int getScreenOrientation() {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                && height > width
                || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
                && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        hide(true);

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }
        // 显示
        $.id(R.id.app_video_volume_icon).image(
                i == 0 ? R.drawable.ic_volume_off_white_36dp
                        : R.drawable.ic_volume_up_white_36dp);
        $.id(R.id.app_video_brightness_box).gone();
        $.id(R.id.app_video_volume_box).visible();
        $.id(R.id.app_video_volume_box).visible();
        $.id(R.id.app_video_volume).text(s).visible();
    }

    public void setProgerssVisible(boolean visible) {
        if (!visible) {
            $.id(R.id.app_video_loading).gone();
        } else {
            $.id(R.id.app_video_loading).visible();
        }
    }

    private void onProgressSlide(float percent) {
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);

        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            $.id(R.id.app_video_fastForward_box).visible();
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            $.id(R.id.app_video_fastForward).text(text + "s");
            $.id(R.id.app_video_fastForward_target).text(
                    generateTime(newPosition) + "/");
            $.id(R.id.app_video_fastForward_all).text(generateTime(duration));
        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = activity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
        $.id(R.id.app_video_brightness_box).visible();
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        $.id(R.id.app_video_brightness).text(
                ((int) (lpa.screenBrightness * 100)) + "%");
        activity.getWindow().setAttributes(lpa);

    }

    private long setProgress() {
        if (isDragging) {
            return 0;
        }
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }
        this.duration = duration;
        $.id(R.id.app_video_currentTime).text(generateTime(position));
        $.id(R.id.app_video_endTime).text(generateTime(this.duration));
        return position;
    }

    public void hide(boolean force) {
        if (force || isShowing) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            showBottomControl(false);
            $.id(R.id.view_jky_player_center_control).gone();
            showTopControl(false);
            $.id(R.id.view_jky_player_fullscreen).invisible();
            isShowing = false;
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        mPopupWindow = null;
    }

    public void setSettingVisible(int isVisible) {
        $.id(R.id.view_jky_play_iv_setting).visibility(isVisible);
    }

    /**
     * 更新全屏按钮
     */
    private void updateFullScreenButton() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 全屏幕
            $.id(R.id.view_jky_player_fullscreen).image(R.drawable.ic_not_fullscreen);
//            $.id(R.id.view_jky_player_iv_share).gone();
//            if(!isLive)
//                $.id(R.id.view_jky_play_iv_setting).visible();
//            else
//                $.id(R.id.view_jky_play_iv_setting).gone();
        } else {
            $.id(R.id.view_jky_player_fullscreen).image(R.drawable.ic_enlarge);
//            $.id(R.id.view_jky_player_iv_share).visible();
//            $.id(R.id.view_jky_play_iv_setting).gone();
        }
    }

    /**
     * 设置只能全屏
     *
     * @param fullScreenOnly true ： 只能全屏 false ： 小屏幕显示
     */
    public void setFullScreenOnly(boolean fullScreenOnly) {
        this.fullScreenOnly = fullScreenOnly;
        tryFullScreen(fullScreenOnly);
        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else if(!isLive){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        updateFullScreenButton();
    }

    /**
     * using constants in GiraffePlayer,eg: GiraffePlayer.SCALETYPE_FITPARENT
     *
     * @param scaleType
     */
    public void setScaleType(String scaleType) {
        if (SCALETYPE_FITPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        } else if (SCALETYPE_FILLPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        } else if (SCALETYPE_WRAPCONTENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
        } else if (SCALETYPE_FITXY.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
        } else if (SCALETYPE_16_9.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
        } else if (SCALETYPE_4_3.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
        }
    }

    /**
     * 是否显示左上导航图标(一般有actionbar or appToolbar时需要隐藏)
     *
     * @param show
     */
    public void setShowNavIcon(boolean show) {
        $.id(R.id.app_video_finish).visibility(show ? View.VISIBLE : View.GONE);
    }

    public void start() {
        statusChange(STATUS_PLAYING);
        videoView.start();
    }

    public void pause() {
        statusChange(STATUS_PAUSE);
        videoView.pause();
    }

    public boolean onBackPressed() {
        if (!fullScreenOnly
                && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    class Query {
        private final Activity activity;
        private View view;

        public Query(Activity activity) {
            this.activity = activity;
        }

        public Query id(int id) {
            view = contentView.findViewById(id);
            return this;
        }

        public Query image(int resId) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
            return this;
        }

        public Query visible() {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public Query gone() {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return this;
        }

        public Query invisible() {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            return this;
        }

        public Query clicked(OnClickListener handler) {
            if (view != null) {
                view.setOnClickListener(handler);
            }
            return this;
        }

        public Query text(CharSequence text) {
            if (view != null && view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public Query visibility(int visible) {
            if (view != null) {
                view.setVisibility(visible);
            }
            return this;
        }
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if ($ == null) return;
            $.id(R.id.tv_definition).text(parent.getItemAtPosition(position).toString());
            if (mPopupWindow != null && mPopupWindow.isShowing())
                mPopupWindow.dismiss();
            mPopupWindow = null;
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(parent, view, position, id);
            }
        }
    };

    public class PlayerGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            if (!isPrepare) {// 视频没有初始化点击屏幕不起作用
//                return false;
//            }
            if (!isLive && videoView != null) {
                if (videoView.isPlaying()) {
                    statusChange(STATUS_PAUSE);
                    videoView.pause();
                } else {
                    statusChange(STATUS_PLAYING);
                    videoView.start();
                }
            }
            handler.removeCallbacks(runnable);
            //videoView.toggleAspectRatio();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            return super.onDown(e);
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e("onScroll", "player");
            if (!isSupportGesture && portrait) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > screenWidthPixels * 0.5f;
                firstTouch = false;
            }

            if (toSeek) {
                if (!isLive) {
                    onProgressSlide(-deltaX / videoView.getWidth());
                }
            } else {
                float percent = deltaY / videoView.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            if (!isPrepare) {// 视频没有初始化点击屏幕不起作用
//                return false;
//            }
            handler.postDelayed(runnable, 300);
            return true;
        }
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (isShowing) {
                hide(false);
            } else {
                show(defaultTimeout);
            }
        }

    };

    /**
     * is player support this device
     *
     * @return
     */
    public boolean isPlayerSupport() {
        return playerSupport;
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return videoView != null ? videoView.isPlaying() : false;
    }

    /**
     * 是否是暂停状态
     *
     * @return
     */
    public boolean isStop() {
        if (videoView == null) {
            return false;
        } else {
            return !videoView.isPlaying() && videoView.getCurrentPosition() != 0;
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (videoView.isPlaying()) {
            statusChange(STATUS_PAUSE);
            videoView.stopPlayback();
        }
    }

    /**
     * 停止播放，（仅仅对列表播放）
     */
    public void stopPlayVideo() {
        if (this != null) {
            statusChange(STATUS_PAUSE);
            videoView.stopPlayback();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        videoView.release(true);
        videoView.seekTo(0);
        isFristPlay = true;
    }

    /**
     * 显示列表中的视图(仅仅对列表播放的一个方法)
     *
     * @param viewId
     */
    public void showView(int viewId) {
        ViewGroup last = (ViewGroup) this.getParent();//找到videoitemview的父类，然后remove
        if (last != null) {
            last.removeAllViews();
            View itemView = (View) last.getParent();
            if (itemView != null) {
                View viewById = itemView.findViewById(viewId);
                if (viewById != null)
                    viewById.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * seekTo position
     *
     * @param msec millisecond
     */
    public SuperPlayerView seekTo(int msec, boolean showControlPanle) {
        videoView.seekTo(msec);
        if (showControlPanle) {
            show(defaultTimeout);
        }
        return this;
    }

    /**
     * 快退快退（取决于传进来的percent）
     *
     * @param percent
     * @return
     */
    public SuperPlayerView forward(float percent) {
        if (isLive || percent > 1 || percent < -1) {
            return this;
        }
        onProgressSlide(percent);
        showBottomControl(true);
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        endGesture();
        return this;
    }

    /**
     * 获取当前播放的currentPosition
     *
     * @return
     */
    public int getCurrentPosition() {
        if (!isLive) {
            currentPosition = videoView.getCurrentPosition();
        } else {// 直播
            currentPosition = -1;
        }
        return currentPosition;
    }

    /**
     * 获取视频的总长度
     *
     * @return
     */
    public int getDuration() {
        return videoView.getDuration();
    }

    public SuperPlayerView playInFullScreen(boolean fullScreen) {
        if (fullScreen) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            updateFullScreenButton();
        }
        return this;
    }

    /**
     * 设置播放视频的是否是全屏
     */
    public void toggleFullScreen() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 转小屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (isShowTopControl) {
                showTopControl(false);
            }
        } else {// 转全屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            showTopControl(true);
        }
        updateFullScreenButton();
    }

    public interface OnErrorListener {
        void onError(int what, int extra);
    }

    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }

    public interface OnPreparedListener {
        void onPrepared();
    }

    public SuperPlayerView onError(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        return this;
    }

    public SuperPlayerView onComplete(Runnable complete) {
        this.oncomplete = complete;
        return this;
    }

    public SuperPlayerView onInfo(OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
        return this;
    }

    public SuperPlayerView onPrepared(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
        return this;
    }

    // 网络监听的回调
    public SuperPlayerView setOnNetChangeListener(
            OnNetChangeListener onNetChangeListener) {
        this.onNetChangeListener = onNetChangeListener;
        return this;
    }

    /**
     * set is live (can't seek forward)
     *
     * @param isLive
     * @return
     */
    public SuperPlayerView setLive(boolean isLive) {
        this.isLive = isLive;
        return this;
    }

    public SuperPlayerView toggleAspectRatio() {
        if (videoView != null) {
            videoView.toggleAspectRatio();
        }
        return this;
    }

    /**
     * 注册网络监听器
     */
    private void registerNetReceiver() {
        if (netChangeReceiver == null) {
            IntentFilter filter = new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION);
            netChangeReceiver = new NetChangeReceiver();
            activity.registerReceiver(netChangeReceiver, filter);
        }
    }

    /**
     * 销毁网络监听器
     */
    private void unregisterNetReceiver() {
        if (netChangeReceiver != null) {
            activity.unregisterReceiver(netChangeReceiver);
            netChangeReceiver = null;
        }
    }

    public interface OnNetChangeListener {
        // wifi
        void onWifi();

        // 手机
        void onMobile();

        // 网络断开
        void onDisConnect();

        // 网路不可用
        void onNoAvailable();
    }

    /*********************************
     * 网络变化监听
     ************************************/
    public class NetChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (onNetChangeListener == null) {
                return;
            }
            if (NetworkUtil.getNetworkType(activity) == 3) {// 网络是WIFI
                onNetChangeListener.onWifi();
            } else if (NetworkUtil.getNetworkType(activity) == 2 || NetworkUtil.getNetworkType(activity) == 4) {// 网络不是手机网络或者是以太网
                // TODO 更新状态是暂停状态
                statusChange(STATUS_PAUSE);
                videoView.pause();
                updatePausePlay();
                $.id(R.id.app_video_loading).gone();
                onNetChangeListener.onMobile();
                showStatus(activity.getResources().getString(R.string.player_not_wifi), "继续");
            } else if (NetworkUtil.getNetworkType(activity) == 1) {// 网络链接断开
                onPause();
                onNetChangeListener.onDisConnect();
            } else {
                onNetChangeListener.onNoAvailable();
            }
        }
    }

    /*************************************** 对外调用的方法 ********************/

    /**
     * 是否显示中心控制器
     *
     * @param isShow true ： 显示 false ： 不显示
     */
    public SuperPlayerView showCenterControl(boolean isShow) {
        this.isShowCenterControl = isShow;
        return this;
    }

    public SuperPlayerView setShowTopControl(boolean isShowTopControl) {
        this.isShowTopControl = isShowTopControl;
        return this;
    }

    /**
     * 点击的时候是否显示控制栏
     *
     * @param isHideControl
     * @return
     */
    public SuperPlayerView setHideControl(boolean isHideControl) {
        this.isHideControl = isHideControl;
        return this;
    }

    /**
     * 设置播放视频是否有网络变化的监听
     *
     * @param isNetListener true ： 监听 false ： 不监听
     * @return
     */
    public SuperPlayerView setNetChangeListener(boolean isNetListener) {
        this.isNetListener = isNetListener;
        return this;
    }

    /**
     * 设置小屏幕是否支持手势操作（默认false）
     *
     * @param isSupportGesture true : 支持（小屏幕支持，大屏幕支持）
     *                         false ：不支持（小屏幕不支持,大屏幕支持）
     * @return
     */
    public SuperPlayerView setSupportGesture(boolean isSupportGesture) {
        this.isSupportGesture = isSupportGesture;
        return this;
    }

    /**
     * 设置了竖屏的时候播放器的宽高
     *
     * @param width  0：默认是屏幕的宽度
     * @param height 0：默认是宽度的16:9
     * @return
     */
    public SuperPlayerView setPlayerWH(int width, int height) {
        this.initWidth = width;
        this.initHeight = height;
        return this;
    }

    /**
     * 获取到当前播放的状态
     *
     * @return
     */
    public int getVideoStatus() {
        return videoView.getCurrentState();
    }

    public IjkVideoView getVideoView() {
        return videoView;
    }

    /**
     * 获得某个控件
     *
     * @param ViewId
     * @return
     */
    public View getView(int ViewId) {
        return activity.findViewById(ViewId);
    }

    public interface OnPlayStateChangeListener {

        void onStateChange(int state);

    }

    private Runnable speedRunnable = new Runnable() {

        @Override
        public void run() {
            if (videoView != null && videoView.getMediaPlayer() != null && mTextView != null) {
                IMediaPlayer mediaPlayer = videoView.getMediaPlayer();
                if (mediaPlayer instanceof IjkMediaPlayer) {
                    long tcpSpeed = ((IjkMediaPlayer) mediaPlayer).getTcpSpeed();
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText(AppUtil.getSize(tcpSpeed));
                }
            }
            mmH.removeCallbacks(this);
            mmH.postDelayed(this, 1000);
        }
    };
}
