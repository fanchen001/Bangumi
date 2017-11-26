package com.fanchen.imovie.view;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.fanchen.imovie.R;


/**
 *
 *  @author fanchen
 */
public class StatusBarLayout extends RelativeLayout {

	private int barColor = 0;
	private Rect mStatusBarRect;
	private Paint mStatusBarColorPaint;
	private int mStatusBarHeight;

	public StatusBarLayout(Context context) {
		super(context);
		init(context);
	}

	public StatusBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,R.styleable.StatusBarColorLayout);
		barColor = mTypedArray.getColor(R.styleable.StatusBarColorLayout_barColor, 0);
		mTypedArray.recycle();
	}

	public StatusBarLayout(Context context, AttributeSet attrs,int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,R.styleable.StatusBarColorLayout);
		barColor = mTypedArray.getColor(R.styleable.StatusBarColorLayout_barColor, 0);
		mTypedArray.recycle();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public StatusBarLayout(Context context, AttributeSet attrs,int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,R.styleable.StatusBarColorLayout);
		barColor = mTypedArray.getColor(R.styleable.StatusBarColorLayout_barColor, 0);
		mTypedArray.recycle();
	}

	private void init(Context context) {
		// 让ToolBar处于系统状态栏下方
		setFitsSystemWindows(true);
		// 设置画笔
		mStatusBarColorPaint = new Paint();
		if (barColor == 0) {
			mStatusBarColorPaint.setColor(getThemeColor(context,R.attr.colorPrimary));
		} else {
			mStatusBarColorPaint.setColor(getResources().getColor(R.color.colorPrimary));
		}
		mStatusBarColorPaint.setAntiAlias(true);
		mStatusBarColorPaint.setStyle(Paint.Style.FILL);
		mStatusBarRect = new Rect();
		mStatusBarHeight = getStatusBarHeight(context);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		try {
			// 要绘制的区域
			mStatusBarRect.set(getLeft(), getTop(), getRight(), mStatusBarHeight);
			// 绘制系统状态栏颜色
			canvas.drawRect(mStatusBarRect, mStatusBarColorPaint);
			super.dispatchDraw(canvas);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取系统状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * 获取当前主题里的颜色
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public int getThemeColor(Context context, int resId) {
		TypedValue value = new TypedValue();
		context.getTheme().resolveAttribute(resId, value, true);
		return value.data;
	}

	public void setStatusBarColorPaint(int colorWithAlpha) {
		mStatusBarColorPaint.setColor(colorWithAlpha);
	}
}