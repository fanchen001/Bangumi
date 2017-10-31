package com.fanchen.imovie.view.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fanchen.imovie.R;
import com.fanchen.imovie.util.DisplayUtil;

/**
 * Created by fanchen on 2017/7/26.
 */
public class PreferenceLine extends Preference{

    public PreferenceLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferenceLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public PreferenceLine(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        LinearLayout linearLayout = new LinearLayout(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        View view = new View(parent.getContext());
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        view.setBackgroundColor(typedValue.data);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(),0.5f));
        int dp5 = DisplayUtil.dip2px(getContext(), 5);
        int dp10 = DisplayUtil.dip2px(getContext(), 10);
        params.topMargin = dp5;
        params.leftMargin = dp10;
        params.rightMargin = dp10;
        view.setLayoutParams(params);
        linearLayout.addView(view);
        return linearLayout;
    }
}
