package com.fanchen.imovie.view.dropdown;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;

/**
 * 下拉按钮
 */
public class DropdownButton extends RelativeLayout {

    //按钮文字大小
    private int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    //按钮未选时中文字颜色
    private int textNormalColor = Color.BLACK;
    //按钮选中时文字颜色
    private int textSelectedColor = Color.BLACK;
    //按钮选中时的图标
    private int selectedDrawableResId = -1;
    //按钮未选中时的图标
    private int normalDrawableResId = -1;

    //默认的下划线宽度
    private int bottomLineWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    //默认的下划线高度
    private int bottomLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());
    //默认的下划线颜色
    private int bottomLineColor = Color.TRANSPARENT;


    TextView textView;
    View bottomLine;

    public DropdownButton(Context context) {
        this(context, null);
    }

    public DropdownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_dropdown_button, this, true);
        textView = (TextView) view.findViewById(R.id.textView);
        bottomLine = view.findViewById(R.id.bottomLine);
    }


    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setChecked(boolean checked) {
        Drawable icon = null;
        if (checked) {
            if (selectedDrawableResId != -1) {
                icon = getResources().getDrawable(selectedDrawableResId);
            }
            textView.setTextColor(textSelectedColor);
            bottomLine.setVisibility(VISIBLE);
        } else {
            if (normalDrawableResId != -1) {
                icon = getResources().getDrawable(normalDrawableResId);
            }
            textView.setTextColor(textNormalColor);
            bottomLine.setVisibility(GONE);
        }

        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        textView.setTextSize(textSize);
    }

    public void setTextNormalColor(int textNormalColor) {
        this.textNormalColor = textNormalColor;
    }

    public void setTextSelectedColor(int textSelectedColor) {
        this.textSelectedColor = textSelectedColor;
    }

    public void setNormalDrawableResId(int normalDrawableResId) {
        this.normalDrawableResId = normalDrawableResId;
    }

    public void setSelectedDrawableResId(int selectedDrawableResId) {
        this.selectedDrawableResId = selectedDrawableResId;
    }

    public void setBottomLineWidth(int bottomLineWidth) {
        this.bottomLineWidth = bottomLineWidth;
        ViewGroup.LayoutParams params = bottomLine.getLayoutParams();
        params.width = bottomLineWidth;
        bottomLine.setLayoutParams(params);
    }

    public void setBottomLineHeight(int bottomLineHeight) {
        this.bottomLineHeight = bottomLineHeight;
        ViewGroup.LayoutParams params = bottomLine.getLayoutParams();
        params.height = bottomLineHeight;
        bottomLine.setLayoutParams(params);
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
        bottomLine.setBackgroundColor(bottomLineColor);
    }
}
