package com.fanchen.imovie.view.video_new;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.dlna.DLNADialog;
import com.fanchen.imovie.dlna.DLNAPlayer;
import com.fanchen.imovie.util.DialogUtil;
import com.tencent.smtt.sdk.TbsVideo;
import com.xigua.p2p.P2PManager;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by fanchen on 2017/6/21.
 * 仿腾讯视频热点列表页播放器控制器.
 */
public class TxVideoPlayerController extends NiceVideoPlayerController implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, ChangeClarityDialog.OnClarityChangedListener {

    private BaseActivity mContext;
    private ImageView mImage;
//    private ImageView mCenterStart;

    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;
    private TextView mDLNA;
    private LinearLayout mBatteryTime;
    private ImageView mBattery;
    private TextView mTime;

    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private TextView mClarity;
    private ImageView mFullScreen;

    private TextView mLength;

    private LinearLayout mLoading;
    private TextView mLoadText;

    private LinearLayout mChangePositon;
    private TextView mChangePositionCurrent;
    private ProgressBar mChangePositionProgress;

    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;

    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;

    private LinearLayout mError;
    private TextView mRetry;

    private LinearLayout mCompleted;
    private TextView mReplay;
    //    private TextView mShare;
    private View mChange;
    private TextView mSpeed;

    private boolean topBottomVisible;
    private OnClickListener clickListener;
    private OnClarityListener onClarityListener;
    private CountDownTimer mDismissTopBottomCountDownTimer;

    private List<Clarity> clarities;
    private int defaultClarityIndex;
    private DLNADialog dlnaDialog = null;
    private ChangeClarityDialog mClarityDialog;
    private DLNAPlayer mPlayer = new DLNAPlayer();
    private DLNADialog.OnSelectDLNAListener onSelectDLNAListener = null;

    private boolean hasRegisterBatteryReceiver; // 是否已经注册了电池广播

    public TxVideoPlayerController(BaseActivity context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.tx_video_palyer_controller, this, true);
//        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.image);
        mSpeed = (TextView) findViewById(R.id.speed);
        mDLNA = (TextView) findViewById(R.id.center_dlna);
        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        mChange = findViewById(R.id.change);
        mBatteryTime = (LinearLayout) findViewById(R.id.battery_time);
        mBattery = (ImageView) findViewById(R.id.battery);
        mTime = (TextView) findViewById(R.id.time);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);
        mClarity = (TextView) findViewById(R.id.clarity);
        mLength = (TextView) findViewById(R.id.length);

        mLoading = (LinearLayout) findViewById(R.id.loading);
        mLoadText = (TextView) findViewById(R.id.load_text);

        mChangePositon = (LinearLayout) findViewById(R.id.change_position);
        mChangePositionCurrent = (TextView) findViewById(R.id.change_position_current);
        mChangePositionProgress = (ProgressBar) findViewById(R.id.change_position_progress);

        mChangeBrightness = (LinearLayout) findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = (ProgressBar) findViewById(R.id.change_brightness_progress);

        mChangeVolume = (LinearLayout) findViewById(R.id.change_volume);
        mChangeVolumeProgress = (ProgressBar) findViewById(R.id.change_volume_progress);

        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);

        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);

//        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mChange.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mClarity.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mDLNA.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
    }

    public void setOnClarityListener(OnClarityListener onClarityListener) {
        this.onClarityListener = onClarityListener;
    }

    public void setOnSelectDLNAListener(DLNADialog.OnSelectDLNAListener onSelectDLNAListener) {
        this.onSelectDLNAListener = onSelectDLNAListener;
    }

    @Override
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) mTitle.setText(title);
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setLoadingVisible(int visible) {
        if (mLoading != null)
            mLoading.setVisibility(View.VISIBLE);
//        if (visible == View.VISIBLE) {
//            mCenterStart.setVisibility(View.GONE);
//        }
    }

    @Override
    public void release() {
        cancelDismissTopBottomTimer();
        if (dlnaDialog != null && dlnaDialog.isShowing()) {
            dlnaDialog.dismiss();
        }
        dlnaDialog = null;
        if (mClarityDialog != null && mClarityDialog.isShowing()) {
            mClarityDialog.dismiss();
        }
        mClarityDialog = null;
        onClarityListener = null;
        onSelectDLNAListener = null;
    }

    @Override
    public void updateSpeed(String speed) {
        if (!TextUtils.isEmpty(speed)) if (mSpeed != null)mSpeed.setText(speed);
    }

    @Override
    public void showSpeed(boolean show) {
        if (mSpeed != null)
            mSpeed.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public TextView getSpeedTextView() {
        return mSpeed;
    }

    @Override
    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    @Override
    public NiceVideoPlayerController setLenght(long length) {
        mLength.setText(NiceUtil.formatTime(length));
        return this;
    }

    @Override
    public void setChangeClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        super.setNiceVideoPlayer(niceVideoPlayer);
        // 给播放器配置视频链接地址
        if (clarities != null && clarities.size() > 1) {
            mNiceVideoPlayer.setUp(clarities.get(defaultClarityIndex).videoUrl, null);
        }
    }

    public void setClarity(List<Clarity> clarities, int defaultClarityIndex) {
        setClarity(clarities, defaultClarityIndex, null);
    }

    /**
     * 设置清晰度
     *
     * @param clarities 清晰度及链接
     */
    public void setClarity(List<Clarity> clarities, int defaultClarityIndex, OnClarityListener listener) {
        if (clarities != null && clarities.size() > 1) {
            this.clarities = clarities;
            this.defaultClarityIndex = defaultClarityIndex;
            List<String> clarityGrades = new ArrayList<>();
            for (Clarity clarity : clarities) {
                clarityGrades.add(clarity.grade);
            }
            mClarity.setText(clarities.get(defaultClarityIndex).grade);
            // 初始化切换清晰度对话框
            mClarityDialog = new ChangeClarityDialog(mContext);
            mClarityDialog.setClarityGrade(clarityGrades, defaultClarityIndex);
            mClarityDialog.setOnClarityCheckedListener(this);
            // 给播放器配置视频链接地址
            if (mNiceVideoPlayer != null && listener == null) {
                mNiceVideoPlayer.setUp(clarities.get(defaultClarityIndex).videoUrl, null);
            } else if (mNiceVideoPlayer != null) {
                listener.onClarityPlay(mNiceVideoPlayer, clarities.get(defaultClarityIndex), 0);
            }
        }
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case NiceVideoPlayer.STATE_IDLE:
                break;
            case NiceVideoPlayer.STATE_PREPARING:
                mImage.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);
                mLoadText.setText("正在准备...");
                mError.setVisibility(View.GONE);
                mCompleted.setVisibility(View.GONE);
                mTop.setVisibility(View.GONE);
                mBottom.setVisibility(View.GONE);
                mDLNA.setVisibility(View.GONE);
//                mCenterStart.setVisibility(View.GONE);
                mLength.setVisibility(View.GONE);
                break;
            case NiceVideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
                break;
            case NiceVideoPlayer.STATE_PLAYING:
                mLoading.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_PAUSED:
                mLoading.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                mLoadText.setText("正在缓冲...");
                startDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                mLoadText.setText("正在缓冲...");
                cancelDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_ERROR:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(View.VISIBLE);
                mError.setVisibility(View.VISIBLE);
                break;
            case NiceVideoPlayer.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mImage.setVisibility(View.VISIBLE);
                mCompleted.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onPlayModeChanged(int playMode) {
        switch (playMode) {
            case NiceVideoPlayer.MODE_NORMAL:
                mBack.setVisibility(View.GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                mFullScreen.setVisibility(View.VISIBLE);
                mClarity.setVisibility(View.GONE);
                mBatteryTime.setVisibility(View.GONE);
                if (hasRegisterBatteryReceiver) {
                    mContext.unregisterReceiver(mBatterReceiver);
                    hasRegisterBatteryReceiver = false;
                }
                break;
            case NiceVideoPlayer.MODE_FULL_SCREEN:
                mBack.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);
                if (clarities != null && clarities.size() > 1) {
                    mClarity.setVisibility(View.VISIBLE);
                }
                mBatteryTime.setVisibility(View.VISIBLE);
                if (!hasRegisterBatteryReceiver) {
                    mContext.registerReceiver(mBatterReceiver,
                            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    hasRegisterBatteryReceiver = true;
                }
                break;
            case NiceVideoPlayer.MODE_TINY_WINDOW:
                mBack.setVisibility(View.VISIBLE);
                mClarity.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 电池状态即电量变化广播接收器
     */
    private BroadcastReceiver mBatterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                // 充电中
                mBattery.setImageResource(R.drawable.battery_charging);
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                // 充电完成
                mBattery.setImageResource(R.drawable.battery_full);
            } else {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int percentage = (int) (((float) level / scale) * 100);
                if (percentage <= 10) {
                    mBattery.setImageResource(R.drawable.battery_10);
                } else if (percentage <= 20) {
                    mBattery.setImageResource(R.drawable.battery_20);
                } else if (percentage <= 50) {
                    mBattery.setImageResource(R.drawable.battery_50);
                } else if (percentage <= 80) {
                    mBattery.setImageResource(R.drawable.battery_80);
                } else if (percentage <= 100) {
                    mBattery.setImageResource(R.drawable.battery_100);
                }
            }
        }
    };

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);

//        mCenterStart.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.VISIBLE);

        mBottom.setVisibility(View.GONE);
        mDLNA.setVisibility(View.GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);

        mLength.setVisibility(View.VISIBLE);

        mTop.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.GONE);

        mLoading.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mCompleted.setVisibility(View.GONE);
    }

    /**
     * 尽量不要在onClick中直接处理控件的隐藏、显示及各种UI逻辑。
     * UI相关的逻辑都尽量到{@link #onPlayStateChanged}和{@link #onPlayModeChanged}中处理.
     */
    @Override
    public void onClick(View v) {
        /*if (v == mCenterStart) {
            if (mNiceVideoPlayer.isIdle()) {
                mNiceVideoPlayer.start();
            }
        } else */if (v == mDLNA) {
            if (dlnaDialog == null) {
                mPlayer.addListener(dlnaListener);
                dlnaDialog = new DLNADialog(getContext());
                if(onSelectDLNAListener == null) onSelectDLNAListener = defaultDLNAListener;
                dlnaDialog.setOnSelectDLNAListener(onSelectDLNAListener);
            }
            if (!dlnaDialog.isShowing()) dlnaDialog.show();
        } else if (v == mChange) {
            if (mNiceVideoPlayer != null && !TextUtils.isEmpty(mNiceVideoPlayer.getPlayerUrl())) {
                if (clickListener != null) {
                    clickListener.onClick(mChange);
                } else {//默認用來手動切換播放器
                    String[] titles = new String[]{"TbsVideo", "系统播放器", "IjkPlayer"};
                    DialogUtil.showMaterialListDialog(getContext(), titles, itemClickListener);
                }
            }
        } else if (v == mBack) {
            if (mNiceVideoPlayer.isActivityFullScreen() && mContext != null) {
                mContext.finish();
            } else if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            } else if (mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.exitTinyWindow();
            }
        } else if (v == mRestartPause) {
            if (mNiceVideoPlayer.isPlaying() || mNiceVideoPlayer.isBufferingPlaying()) {
                mNiceVideoPlayer.pause();
            } else if (mNiceVideoPlayer.isPaused() || mNiceVideoPlayer.isBufferingPaused()) {
                mNiceVideoPlayer.restart();
            }
        } else if (v == mFullScreen) {
            if (mNiceVideoPlayer.isNormal() || mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.enterFullScreen();
            } else if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            }
        } else if (v == mClarity) {
            setTopBottomVisible(false); // 隐藏top、bottom
            mClarityDialog.show();     // 显示清晰度对话框
        } else if (v == mRetry) {
            mNiceVideoPlayer.restart();
        } else if (v == mReplay) {
            mRetry.performClick();
        } /*else if (v == mShare) {
            Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show();
        }*/ else if (v == this) {
            if (mNiceVideoPlayer.isPlaying()
                    || mNiceVideoPlayer.isPaused()
                    || mNiceVideoPlayer.isBufferingPlaying()
                    || mNiceVideoPlayer.isBufferingPaused()) {
                setTopBottomVisible(!topBottomVisible);
            }
        }
    }

    @Override
    public void onClarityChanged(int clarityIndex) {
        // 根据切换后的清晰度索引值，设置对应的视频链接地址，并从当前播放位置接着播放
        if (mNiceVideoPlayer == null || clarities == null || clarities.size() <= clarityIndex)
            return;
        Clarity clarity = clarities.get(clarityIndex);
        mClarity.setText(clarity.grade);
        long currentPosition = mNiceVideoPlayer.getCurrentPosition();
        mNiceVideoPlayer.releasePlayer();
        if (onClarityListener == null) {
            mNiceVideoPlayer.setUp(clarity.videoUrl, null);
            mNiceVideoPlayer.start(currentPosition);
        } else {
            onClarityListener.onClarityPlay(mNiceVideoPlayer, clarity, currentPosition);
        }
    }

    @Override
    public void onClarityNotChanged() {
        // 清晰度没有变化，对话框消失后，需要重新显示出top、bottom
        setTopBottomVisible(true);
    }

    /**
     * 设置top、bottom的显示和隐藏
     *
     * @param visible true显示，false隐藏.
     */
    private void setTopBottomVisible(boolean visible) {
        mTop.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBottom.setVisibility(visible ? View.VISIBLE : View.GONE);
        mDLNA.setVisibility(visible ? View.VISIBLE : View.GONE);
        topBottomVisible = visible;
        if (visible) {
            if (!mNiceVideoPlayer.isPaused() && !mNiceVideoPlayer.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    /**
     * 开启top、bottom自动消失的timer
     */
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    /**
     * 取消top、bottom自动消失的timer
     */
    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mNiceVideoPlayer.isBufferingPaused() || mNiceVideoPlayer.isPaused()) {
            mNiceVideoPlayer.restart();
        }
        long position = (long) (mNiceVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
        mNiceVideoPlayer.seekTo(position);
        startDismissTopBottomTimer();
    }

    @Override
    protected void updateProgress() {
        long position = mNiceVideoPlayer.getCurrentPosition();
        long duration = mNiceVideoPlayer.getDuration();
        int bufferPercentage = mNiceVideoPlayer.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(NiceUtil.formatTime(position));
        mDuration.setText(NiceUtil.formatTime(duration));
        // 更新时间
        mTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date()));
    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        mChangePositon.setVisibility(View.VISIBLE);
        long newPosition = (long) (duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(NiceUtil.formatTime(newPosition));
        mChangePositionProgress.setProgress(newPositionProgress);
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(NiceUtil.formatTime(newPosition));
    }

    @Override
    protected void hideChangePosition() {
        mChangePositon.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeVolume(int newVolumeProgress) {
        mChangeVolume.setVisibility(View.VISIBLE);
        mChangeVolumeProgress.setProgress(newVolumeProgress);
    }

    @Override
    protected void hideChangeVolume() {
        mChangeVolume.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeBrightness(int newBrightnessProgress) {
        mChangeBrightness.setVisibility(View.VISIBLE);
        mChangeBrightnessProgress.setProgress(newBrightnessProgress);
    }

    @Override
    protected void hideChangeBrightness() {
        mChangeBrightness.setVisibility(View.GONE);
    }

    @Override
    public VideoState getVideoState() {
        VideoState state = new VideoState();
        state.brightness = getBrightness();
        state.volume = getVolume();
        if (mNiceVideoPlayer == null || mTitle == null) return state;
        state.title = mTitle.getText().toString();
        state.url = mNiceVideoPlayer.getPlayerUrl();
        state.duration = mNiceVideoPlayer.getDuration();
        return state;
    }

    /**
     * 右上角更多操作
     * 默認是選擇播放器
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mNiceVideoPlayer == null || mContext == null) return;
            String playerUrl = mNiceVideoPlayer.getPlayerUrl();
            if (position == 0 && !TextUtils.isEmpty(playerUrl)) {
                TbsVideo.openVideo(mContext, playerUrl);
                mContext.finish();
            } else if (!TextUtils.isEmpty(playerUrl)) {
                mNiceVideoPlayer.release();
                if (position == 1 && !P2PManager.isXiguaUrl(playerUrl)) {
                    mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_NATIVE); // IjkPlayer or MediaPlayer
                } else {//西瓜视频用自带播放器放不了，只能用ijk
                    mNiceVideoPlayer.setPlayerType(NiceVideoPlayer.TYPE_IJK); // IjkPlayer or MediaPlayer
                }
                mNiceVideoPlayer.setActivityFullScreen(true);
                mNiceVideoPlayer.enterFullScreen();
                mNiceVideoPlayer.setUp(playerUrl);
            }
        }

    };

    /**
     * defaultDLNAListener
     * 默認DLNAListener
     */
    private DLNADialog.OnSelectDLNAListener defaultDLNAListener = new DLNADialog.OnSelectDLNAListener() {

        @Override
        public void onSelectDLNA(Device device, ControlPoint controlPoint) {
            DialogUtil.showProgressDialog(getContext(), "正在投屏,请稍后...");
            mPlayer.setUp(device, controlPoint);
            DeviceDetails details = device.getDetails();
            String format = String.format("已连接：%s", details.getFriendlyName());
            NiceVideoPlayerController.VideoState state = TxVideoPlayerController.this.getVideoState();
            TxVideoPlayerController.this.setTitle(format);
            mPlayer.play(state.title, state.url);
        }

    };

    /**
     * dlnaListener
     * 監聽投屏動作
     */
    private DLNAPlayer.EventListener dlnaListener = new DLNAPlayer.EventListener() {

        @Override
        public void onPlay() {
            if(mContext == null)return;
            mContext.showToast("投屏成功，请到DLNA设备上观看视频");
            if (mNiceVideoPlayer != null) mNiceVideoPlayer.pause();
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onGetMediaInfo(MediaInfo mediaInfo) {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onPlayerError() {
            if(mContext == null)return;
            mContext.showToast("投屏失败");
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onTimelineChanged(PositionInfo positionInfo) {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onSeekCompleted() {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onPaused() {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onMuteStatusChanged(boolean isMute) {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onVolumeChanged(int volume) {
            DialogUtil.closeProgressDialog();
        }

        @Override
        public void onStop() {
            DialogUtil.closeProgressDialog();
        }

    };

    /**
     *onClarityPlay
     */
    public interface OnClarityListener {
        /**
         * @param clarity
         */
        void onClarityPlay(INiceVideoPlayer videoPlayer, Clarity clarity, long current);
    }
}
