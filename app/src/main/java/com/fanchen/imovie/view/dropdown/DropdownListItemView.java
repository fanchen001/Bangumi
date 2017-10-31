package com.fanchen.imovie.view.dropdown;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 下拉列表条目
 */
public class DropdownListItemView extends TextView {

    private int itemTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 7, getResources().getDisplayMetrics());
    private int itemTextNormalColor = Color.BLACK;
    private int itemTextSelectedColor = Color.BLACK;
    private int itemNormalBg = Color.WHITE;
    private int itemSelectedBg = Color.parseColor("#ffe4e4e4");
    private int itemNormalDrawableResId = -1;
    private int itemSelectedDrawableResId = -1;
    private int itemTextGravity = Gravity.CENTER;

    public DropdownListItemView(Context context) {
        this(context, null);
    }

    public DropdownListItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bind(CharSequence text, boolean checked) {
        setText(text);
        setTextSize(itemTextSize);
        Drawable icon = null;
        if (checked) {
            if (itemSelectedDrawableResId != -1) {
                icon = getResources().getDrawable(itemSelectedDrawableResId);
            }
            setTextColor(itemTextSelectedColor);
            setBackgroundColor(itemSelectedBg);
        } else {
            if (itemNormalDrawableResId != -1) {
                icon = getResources().getDrawable(itemNormalDrawableResId);
            }
            setTextColor(itemTextNormalColor);
            setBackgroundColor(itemNormalBg);
        }

        setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
    }

    public void setItemTextSize(int itemTextSize) {
        this.itemTextSize = itemTextSize;
        setTextSize(itemTextSize);
    }

    public void setItemTextNormalColor(int itemTextNormalColor) {
        this.itemTextNormalColor = itemTextNormalColor;
    }

    public void setItemTextSelectedColor(int itemTextSelectedColor) {
        this.itemTextSelectedColor = itemTextSelectedColor;
    }

    public void setItemNormalBg(int itemNormalBg) {
        this.itemNormalBg = itemNormalBg;
    }

    public void setItemSelectedBg(int itemSelectedBg) {
        this.itemSelectedBg = itemSelectedBg;
    }

    public void setItemNormalDrawableResId(int itemNormalDrawableResId) {
        this.itemNormalDrawableResId = itemNormalDrawableResId;
    }

    public void setItemSelectedDrawableResId(int itemSelectedDrawableResId) {
        this.itemSelectedDrawableResId = itemSelectedDrawableResId;
    }

    public void setItemTextGravity(int itemTextGravity) {
        this.itemTextGravity = itemTextGravity;
        setGravity(itemTextGravity);
    }
}
