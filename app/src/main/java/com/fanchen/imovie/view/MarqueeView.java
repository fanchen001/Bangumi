package com.fanchen.imovie.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

import com.fanchen.imovie.R;
import com.fanchen.imovie.util.DisplayUtil;

import java.util.List;

/**
 * MarqueeView
 * Created by fanchen on 2018/11/16.
 */
public class MarqueeView extends ViewFlipper {
    /**
     * 是否显示淡入淡出动画
     */
    private boolean isSetAlphaAnim = true;
    /**
     * 切换间隔时间
     */
    private int interval = 5000;
    /**
     * 动画时间
     */
    private int animDuration = 2000;

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MarqueeViewUp, defStyleAttr, 0);
        isSetAlphaAnim = ta.getBoolean(R.styleable.MarqueeViewUp_isSetAlphaAnim, isSetAlphaAnim);
        interval = ta.getInteger(R.styleable.MarqueeViewUp_interval, interval);
        animDuration = ta.getInteger(R.styleable.MarqueeViewUp_animDuration, animDuration);
        setFlipInterval(interval);
        //淡入淡出动画
        AlphaAnimation animationIn = new AlphaAnimation(0, 1);//淡入 从透明到不透明
        animationIn.setDuration(animDuration);//设置动画持续时间
        AlphaAnimation animationOut = new AlphaAnimation(1, 0);//淡出 从不透明到透明
        animationOut.setDuration(animDuration);//设置动画持续时间
        //平移动画
        TranslateAnimation translateAnimationIn = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);//向上移动出现
        TranslateAnimation translateAnimationOut = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f); //向上移动消失
        translateAnimationIn.setDuration(animDuration);
        translateAnimationOut.setDuration(animDuration);
        // 动画集-- 进入动画
        AnimationSet animationInSet = new AnimationSet(false);
        animationInSet.addAnimation(translateAnimationIn);
        // 动画集-- 退出动画
        AnimationSet animationOutSet = new AnimationSet(false);
        animationOutSet.addAnimation(translateAnimationOut);
        if (isSetAlphaAnim) {
            //设置淡入淡出动画
            animationInSet.addAnimation(animationIn);
            animationOutSet.addAnimation(animationOut);
        }
        setInAnimation(animationInSet);
        setOutAnimation(animationOutSet);
    }


    /**
     * 设置循环滚动的View数组
     *
     * @param views
     */
    public void setViews(final List<View> views) {
        if (views == null || views.size() == 0) return;
        removeAllViews();
        for (int i = 0; i < views.size(); i++) {
            final int position = i;
            //设置监听回调
            views.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, views.get(position));
                    }
                }
            });
            MarginLayoutParams layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.leftMargin = DisplayUtil.dip2px(getContext(),10);
            layoutParams.rightMargin = DisplayUtil.dip2px(getContext(),10);
            addView(views.get(i),layoutParams);
        }
        startFlipping();
    }

    /**
     * 点击回调
     */
    private OnItemClickListener onItemClickListener;

    /**
     * 设置监听接口
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * item_view的接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }
}
