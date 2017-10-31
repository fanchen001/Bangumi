package com.fanchen.zzplayer.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.zzplayer.controller.IControllerImpl;
import com.fanchen.zzplayer.util.OrientationUtil;
import com.fanchen.zzplayer.util.PlayState;
import com.fanchen.zzplayer.util.SeekBarState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by fanchen on 2017/4/28.
 */
public class PlayerController extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private IControllerImpl mControllerImpl;
    private CustomSeekBar mCsb;
    private ImageView mIvPlayPause;
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    public ImageView mIvToggleExpandable;
    private int mDuration = 0;//视频长度(ms)
    private SimpleDateFormat mFormatter = null;
    private static final String ZERO_TIME = "00:00";
    private boolean mUserOperateSeecbar = false;//用户是否正在操作进度条
    private Drawable[] mProgressLayers = new Drawable[3];
    private LayerDrawable mProgressLayerDrawable;
    private static final String TAG = "PlayerController";

    public static final int PLAY_ICON_TYPE_PLAY = 00;// 播放按钮
    public static final int PLAY_ICON_TYPE_PAUSE = 01;// 暂停按钮
    private int curPlayIconType = PLAY_ICON_TYPE_PLAY;
    private int iconPause = R.drawable.zz_player_pause;
    private int iconPlay = R.drawable.zz_player_play;

    int iconShrink = R.drawable.zz_player_shrink;
    int iconExpand = R.drawable.zz_player_expand;
    private int mCurOrientation = OrientationUtil.VERTICAL;


    public PlayerController(Context context) {
        super(context);
        initView(context);
    }

    public PlayerController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PlayerController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayerController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.layout_zz_video_player_controller, this);

        View rlPlayPause = findViewById(R.id.rl_play_pause);
        mIvPlayPause = (ImageView) findViewById(R.id.iv_play_pause);
        mTvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        mTvTotalTime = (TextView) findViewById(R.id.tv_total_time);

        mCsb = (CustomSeekBar) findViewById(R.id.csb);
        setProgressLayerDrawables(R.drawable.zz_player_shape_default_background,
                R.drawable.zz_player_shape_default_second_progress,
                R.drawable.zz_player_shape_default_progress);

        mProgressLayerDrawable = new LayerDrawable(mProgressLayers);
        mCsb.setProgressDrawable(mProgressLayerDrawable);

        View rlToggleExpandable = findViewById(R.id.rl_toggle_expandable);
        mIvToggleExpandable = (ImageView) findViewById(R.id.iv_toggle_expandable);

        rlPlayPause.setOnClickListener(this);
        rlToggleExpandable.setOnClickListener(this);
        mIvPlayPause.setOnClickListener(this);
        mCsb.setOnSeekBarChangeListener(this);
    }

    /**
     * 设置控制条功能回调
     */
    public void setControllerImpl(IControllerImpl controllerImpl) {
        this.mControllerImpl = controllerImpl;
    }

    @Override
    public void onClick(View v) {
        if (mControllerImpl == null) {
            return;
        }

        int id = v.getId();
        if (id == R.id.rl_play_pause || id == R.id.iv_play_pause) {
            mControllerImpl.onPlayTurn();
        } else if (id == R.id.iv_toggle_expandable || id == R.id.rl_toggle_expandable) {
            mControllerImpl.onOrientationChange();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mTvCurrentTime.setText(formatPlayTime(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mControllerImpl.onProgressChange(SeekBarState.START_TRACKING, 0);
        mUserOperateSeecbar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mControllerImpl.onProgressChange(SeekBarState.STOP_TRACKING, seekBar.getProgress());
        mUserOperateSeecbar = false;

    }

    /**
     * 设置播放状态
     *
     * @param curPlayState 参考 {@link PlayState}
     */
    public void setPlayState(int curPlayState) {

        switch (curPlayState) {
            case PlayState.PLAY:
                mIvPlayPause.setImageResource(iconPause);
                curPlayIconType = PLAY_ICON_TYPE_PAUSE;
                break;
            case PlayState.PAUSE:
            case PlayState.STOP:
            case PlayState.COMPLETE:
            case PlayState.ERROR:
                mIvPlayPause.setImageResource(iconPlay);
                curPlayIconType = PLAY_ICON_TYPE_PLAY;
                break;
        }
    }

    /**
     * 屏幕方向改变时,回调该方法
     *
     * @param orientation 新屏幕方向:<br>
     *                    <ol>
     *                    <li>{@link OrientationUtil#HORIZONTAL HORIZONTAL}</li>
     *                    <li>{@link OrientationUtil#VERTICAL VERTICAL}</li>
     *                    </ol>
     */
    public void setOrientation(int orientation) {
        mCurOrientation = orientation;
        //更新全屏图标
        if (orientation == OrientationUtil.HORIZONTAL) {
            mIvToggleExpandable.setImageResource(iconShrink);
        } else {
            mIvToggleExpandable.setImageResource(iconExpand);
        }
    }

    /**
     * 更新播放进度
     * 参考 {@link #updateProgress(int, int, int)}
     */
    public void updateProgress(int progress, int secondProgress) {
        updateProgress(progress, secondProgress, mDuration);
    }

    /**
     * 更新播放进度
     * 参考 {@link #updateProgress(int, int, int, boolean)}
     */
    public void updateProgress(int progress, int secondProgress, int maxValue) {
        updateProgress(progress, secondProgress, maxValue, mUserOperateSeecbar);
    }

    /**
     * 更新播放进度
     *
     * @param progress       当前进度
     * @param secondProgress 缓冲进度
     * @param maxValue       最大值
     * @param isTracking     用户是否正在操作中
     */
    public void updateProgress(int progress, int secondProgress, int maxValue, boolean isTracking) {
        // 更新播放时间信息
        initFormatter(maxValue);

        //更新进度条
        mDuration = maxValue;
        mCsb.setMax(maxValue);
        mCsb.setSecondaryProgress(secondProgress * maxValue / 100);

        if (!isTracking) {
            mCsb.setProgress(progress);
            mTvCurrentTime.setText(formatPlayTime(progress));
        }

        mTvTotalTime.setText(formatPlayTime(maxValue));
    }

    private void initFormatter(int maxValue) {
        if (mFormatter == null) {
            if (maxValue >= (59 * 60 * 1000 + 59 * 1000)) {
                mFormatter = new SimpleDateFormat("HH:mm:ss");
            } else {
                mFormatter = new SimpleDateFormat("mm:ss");
            }
            mFormatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String formatPlayTime(long time) {
        if (time <= 0) {
            return ZERO_TIME;
        }

        if (mFormatter == null) {
            initFormatter(mDuration);
        }
        String timeStr = mFormatter.format(new Date(time));
        if (TextUtils.isEmpty(timeStr)) {
            timeStr = ZERO_TIME;
        }
        return timeStr;
    }

    public void updateNetworkState(boolean isAvailable) {
        mCsb.setSeekable(isAvailable);
    }

    /**
     * 设置暂停按钮图标
     */
    public void setIconPause(@DrawableRes int iconPause) {
        this.iconPause = iconPause;
        if (curPlayIconType == PLAY_ICON_TYPE_PAUSE) {
            mIvPlayPause.setImageResource(iconPause);
        }
    }

    /**
     * 设置播放按钮图标
     */
    public void setIconPlay(@DrawableRes int iconPlay) {
        this.iconPlay = iconPlay;
        if (curPlayIconType == PLAY_ICON_TYPE_PLAY) {
            mIvPlayPause.setImageResource(iconPlay);
        }
    }

    /**
     * 设置退出全屏按钮
     */
    public void setIconShrink(@DrawableRes int iconShrink) {
        this.iconShrink = iconShrink;
        if (mCurOrientation == OrientationUtil.HORIZONTAL) {
            mIvToggleExpandable.setImageResource(iconShrink);
        }
    }

    /**
     * 设置退出全屏按钮
     */
    public void setIconExpand(@DrawableRes int iconExpand) {
        this.iconExpand = iconExpand;
        if (mCurOrientation == OrientationUtil.VERTICAL) {
            mIvToggleExpandable.setImageResource(iconExpand);
        }
    }


    /**
     * 设置进度条样式
     *
     * @param resId 进度条progressDrawable分层资源
     *              数组表示的进度资源分别为 background - secondaryProgress - progress
     *              若对应的数组元素值 <=0,表示该层素材保持不变;
     *              注意:progress和secondaryProgress的shape资源需要做成clip的,否则会直接完全显示
     */
    public void setProgressLayerDrawables(@DrawableRes int... resId) {
        for (int i = 0; i < resId.length; i++) {
            if (resId[i] > 0 && i < mProgressLayers.length) {
                if (Build.VERSION.SDK_INT >= 21) {
                    mProgressLayers[i] = getResources().getDrawable(resId[i], null);
                } else {
                    mProgressLayers[i] = getResources().getDrawable(resId[i]);
                }
            }
        }
        mProgressLayerDrawable = new LayerDrawable(mProgressLayers);
        if (mCsb != null) {
            mCsb.setProgressDrawable(mProgressLayerDrawable);
        }
    }

    public void setProgressLayerDrawables(@DrawableRes int resId) {
        if (mCsb != null) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= 21) {
                drawable = getResources().getDrawable(resId, null);
            } else {
                drawable = getResources().getDrawable(resId);
            }
            mCsb.setProgressDrawable(drawable);
        }
    }

    /**
     * 设置进度条按钮图片
     */
    public void setProgressThumbDrawable(@DrawableRes int thumbId) {
        if (thumbId > 0) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= 21) {
                drawable = getResources().getDrawable(thumbId, null);
            } else {
                drawable = getResources().getDrawable(thumbId);
            }
            if (drawable != null && mCsb != null) {
                mCsb.setThumb(drawable);
            }
        }
    }

    /**
     * 隐藏时间进度和总时间信息
     */
    public void hideTimes() {
        mTvCurrentTime.setVisibility(GONE);
        mTvTotalTime.setVisibility(GONE);
    }

    /**
     * 显示时间进度和总时间信息
     */
    public void showTimes() {
        mTvCurrentTime.setVisibility(VISIBLE);
        mTvTotalTime.setVisibility(VISIBLE);
    }
}
