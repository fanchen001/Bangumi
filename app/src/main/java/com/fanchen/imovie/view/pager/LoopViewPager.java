package com.fanchen.imovie.view.pager;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.util.DisplayUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * LoopViewPagerLayout
 *
 * @author Edwin.Wu
 * @version 2016/11/14 23:58
 * @see <a href="https://github.com/why168/LoopViewPagerLayout">LoopViewPagerLayout/a>
 * @since JDK1.8
 */
public class LoopViewPager extends RelativeLayout {
    private FrameLayout indicatorFrameLayout;
    private ViewPager loopViewPager;
    private LinearLayout indicatorLayout;
    private LinearLayout animIndicatorLayout;
    private OnBannerItemClickListener onBannerItemClickListener = null;
    private OnLoadImageViewListener onLoadImageViewListener = null;
    private LoopPagerAdapter loopPagerAdapter;
    private int totalDistance;//Little red dot all the distance to move
    private int size = DisplayUtil.dip2px(getContext(), 6);//The size of the set point;
    private ArrayList<? extends IBanner> bannerInfos;//banner data
    private TextView animIndicator;//Little red dot on the move
    private TextView[] indicators;//Initializes the white dots
    private static final int MESSAGE_LOOP = 5;
    private int loop_ms = 5000;//loop speed(ms)
    private int loop_style = -1; //loop style(enum values[-1:empty,1:depth 2:zoom])
    private IndicatorLocation indicatorLocation = IndicatorLocation.Right; //Indicator Location(enum values[1:left,0:depth 2:right])
    private int loop_duration = 2000;//loop rate(ms)
    private boolean isLoop = false;
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == MESSAGE_LOOP) {
                if (loopViewPager.getCurrentItem() < Short.MAX_VALUE - 1) {
                    loopViewPager.setCurrentItem(loopViewPager.getCurrentItem() + 1, true);
                    sendEmptyMessageDelayed(MESSAGE_LOOP, getLoop_ms());
                }
            }
        }
    };

    public LoopViewPager(Context context) {
        super(context);
        initializeData();
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeData();
    }

    public LoopViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeData();
    }

    public boolean isLoop() {
        return isLoop;
    }

    /**
     * onBannerItemClickListener
     *
     * @param onBannerItemClickListener onBannerItemClickListener
     */
    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
    }

    /**
     * 是否需要开启轮训
     *
     * 当banner 大于1时才需要开启
     * @return
     */
    public boolean hasLoop(){
        return loopPagerAdapter == null ? false : loopPagerAdapter.getCount() <= 1 ? false : true;
    }

    /**
     *
     * @param onLoadImageViewListener onLoadImageViewListener
     */
    public void setOnLoadImageViewListener(OnLoadImageViewListener onLoadImageViewListener) {
        this.onLoadImageViewListener = onLoadImageViewListener;
    }

    /**
     * Be sure to initialize the View
     */
    private void initializeView() {
        float density = getResources().getDisplayMetrics().density;

        loopViewPager = new ViewPager(getContext());
        LayoutParams loop_params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addView(loopViewPager, loop_params);

        //TODO FrameLayout
        indicatorFrameLayout = new FrameLayout(getContext());
        LayoutParams f_params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ((int) (20 * density)));
        f_params.addRule(RelativeLayout.CENTER_HORIZONTAL);//android:layout_centerHorizontal="true"
        f_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//android:layout_alignParentBottom="true"

        switch (indicatorLocation) {
            case Left:
                f_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);// android:layout_alignParentLeft="true"
                break;
            case Right:
                f_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//android:layout_alignParentRight="true"
                break;
            default:
                break;
        }

        f_params.setMargins(((int) (10 * density)), 0, ((int) (10 * density)), 0);
        addView(indicatorFrameLayout, f_params);

        //TODO indicatorLayout
        indicatorLayout = new LinearLayout(getContext());
        FrameLayout.LayoutParams ind_params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        indicatorLayout.setGravity(Gravity.CENTER);
        indicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        indicatorFrameLayout.addView(indicatorLayout, ind_params);

        //TODO animIndicatorLayout
        animIndicatorLayout = new LinearLayout(getContext());
        FrameLayout.LayoutParams ind_params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        animIndicatorLayout.setGravity(Gravity.CENTER | Gravity.START);
        animIndicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        indicatorFrameLayout.addView(animIndicatorLayout, ind_params2);
    }

    /**
     * Be sure to initialize the Data
     *
     */
    private void initializeData() {
        initializeView();
        if (loop_duration > loop_ms)
            loop_duration = loop_ms;
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            LoopScroller mScroller = new LoopScroller(getContext());
//            LoopScroller mScroller = new LoopScroller(context, new AccelerateInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new AnticipateInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new PathInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new BounceInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new OvershootInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new AnticipateOvershootInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new LinearInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new AccelerateInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new DecelerateInterpolator());
//            LoopScroller mScroller = new LoopScroller(context, new CycleInterpolator(20));
            //可以用setDuration的方式调整速率
            mScroller.setmDuration(loop_duration);
            mField.set(loopViewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO loop_style
        if (loop_style == 1) {
            loopViewPager.setPageTransformer(true, new DepthPageTransformer());
        } else if (loop_style == 2) {
            loopViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        }

        //TODO Listener
        loopViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stopLoop();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        stopLoop();
                        break;
                    case MotionEvent.ACTION_UP:
                        startLoop();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * initialize the Data
     *
     * @param bannerInfos BannerInfo
     */
    public void setLoopData(List<? extends IBanner> bannerInfos) {
        if (bannerInfos != null && bannerInfos.size() > 0) {
            this.bannerInfos = new ArrayList<>(bannerInfos);
        } else {
            throw new NullPointerException("LoopViewPagerLayout bannerInfos is null or bannerInfos.size() isEmpty");
        }

        //TODO Initialize multiple times, clear images and little red dot
        if (indicatorLayout.getChildCount() > 0) {
            indicatorLayout.removeAllViews();
            removeView(animIndicator);
        }

        InitIndicator();

        InitLittleRed();

        indicatorLayout.getViewTreeObserver().addOnPreDrawListener(new IndicatorPreDrawListener());

        loopPagerAdapter = new LoopPagerAdapter(getContext(), bannerInfos, onBannerItemClickListener, onLoadImageViewListener);
        loopViewPager.setAdapter(loopPagerAdapter);
        loopViewPager.addOnPageChangeListener(new ViewPageChangeListener());

        int index = Short.MAX_VALUE / 2 - (Short.MAX_VALUE / 2) % bannerInfos.size();
        loopViewPager.setCurrentItem(index);
    }

    private void InitIndicator() {
        indicatorLayout.removeAllViews();
        indicators = new TextView[bannerInfos.size()];
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new TextView(getContext());
            indicators[i].setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            if (i != indicators.length - 1) {
                params.setMargins(0, 0, size, 0);
            } else {
                params.setMargins(0, 0, 0, 0);
            }
            indicators[i].setLayoutParams(params);
            indicators[i].setBackgroundResource(R.drawable.indicator_normal_background);//设置默认的背景颜色
            indicatorLayout.addView(indicators[i]);
        }

    }

    private void InitLittleRed() {
        animIndicatorLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        animIndicator = new TextView(getContext());
        animIndicator.setGravity(Gravity.CENTER);
        animIndicator.setBackgroundResource(R.drawable.indicator_selected_background);//设置选中的背景颜色
        animIndicatorLayout.addView(animIndicator, params);
    }

    public int getLoop_ms() {
        if (loop_ms < 1500)
            loop_ms = 1500;
        return loop_ms;
    }

    /**
     * loop speed
     *
     * @param loop_ms (ms)
     */
    public void setLoop_ms(int loop_ms) {
        this.loop_ms = loop_ms;
    }

    /**
     * loop rate
     *
     * @param loop_duration (ms)
     */
    public void setLoop_duration(int loop_duration) {
        this.loop_duration = loop_duration;
    }

    /**
     * loop style
     *
     * @param loop_style (enum values[-1:empty,1:depth 2:zoom])
     */
    public void setLoop_style(LoopStyle loop_style) {
        this.loop_style = loop_style.getValue();
    }

    /**
     * indicator_location
     *
     * @param indicatorLocation (enum values[1:left,0:depth,2:right])
     */
    public void setIndicatorLocation(IndicatorLocation indicatorLocation) {
        this.indicatorLocation = indicatorLocation;
    }

    /**
     * startLoop
     */
    public void startLoop() {
        isLoop = true;
        handler.removeCallbacksAndMessages(MESSAGE_LOOP);
        handler.sendEmptyMessageDelayed(MESSAGE_LOOP, getLoop_ms());
    }

    /**
     * stopLoop
     * Be sure to in onDestory,To prevent a memory leak
     */
    public void stopLoop() {
        isLoop = false;
        handler.removeMessages(MESSAGE_LOOP);
    }

    /**
     * LoopViewPager
     *
     * @return ViewPager
     */
    public ViewPager getLoopViewPager() {
        return loopViewPager;
    }

    /**
     * OnPageChangeListener
     */
    private class ViewPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (loopPagerAdapter.getCount() > 0) {
                float length = ((position % bannerInfos.size()) + positionOffset) / (bannerInfos.size() - 1);
                //TODO To prevent the last picture the little red dot slip out.
                if (length >= 1)
                    length = 1;
                float path = length * totalDistance;
                ViewCompat.setTranslationX(animIndicator, path);
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * OnPreDrawListener
     */
    private class IndicatorPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        @Override
        public boolean onPreDraw() {
            Rect firstRect = new Rect();
            indicatorLayout.getChildAt(0).getGlobalVisibleRect(firstRect);

            Rect lastRect = new Rect();
            indicatorLayout.getChildAt(indicators.length - 1).getGlobalVisibleRect(lastRect);

            totalDistance = lastRect.left - firstRect.left;

            indicatorLayout.getViewTreeObserver().removeOnPreDrawListener(this);

            return false;
        }
    }

    public interface OnBannerItemClickListener {
        /**
         * banner click
         *
         * @param index  subscript
         * @param banner bean
         */
        void onBannerClick(int index, ArrayList<? extends IBanner> banner);
    }

    public interface OnLoadImageViewListener {
        /**
         * create image
         *
         * @param context context
         * @return image
         */
        View createBannerView(Context context);

        /**
         * image load
         *
         * @param imageView ImageView
         * @param parameter String    可以为一个文件路径、uri或者url
         *                  Uri   uri类型
         *                  File  文件
         *                  Integer   资源Id,R.drawable.xxx或者R.mipmap.xxx
         *                  byte[]    类型
         *                  T 自定义类型
         */
        void onLoadImageView(View imageView, IBanner<?> parameter);
    }

    public static abstract class OnDefaultImageViewLoader implements OnLoadImageViewListener {

        @Override
        public View createBannerView(Context context) {
            return new ImageView(context);
        }

    }
    public static class LoopScroller extends Scroller {
        private int mDuration = 1000;//速率必须小于延迟时间loop_ms

        public LoopScroller(Context context) {
            super(context);
        }

        public LoopScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public LoopScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setmDuration(int time) {
            mDuration = time;
        }

        public int getmDuration() {
            return mDuration;
        }

    }
}