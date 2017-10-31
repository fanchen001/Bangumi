package com.fanchen.imovie.view;

import android.content.Context;
import android.util.TypedValue;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fanchen.imovie.util.DisplayUtil;

/**
 *
 * Created by fanchen on 2017/7/6.
 */
public class ContextMenuTitleView extends ScrollView {
    private static final int MAX_HEIGHT_DP = 70;
    private static final int PADDING_DP = 16;
    private int makeSpec;

    public ContextMenuTitleView(Context context, String title) {
        super(context);
        int padding = DisplayUtil.dip2px(context, PADDING_DP);
        makeSpec = DisplayUtil.dip2px(context, MAX_HEIGHT_DP);
        setPadding(padding, padding, padding, padding);
        TextView titleView = new TextView(context);
        titleView.setMaxLines(2);
        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        titleView.setTextColor(context.getResources().getColor(android.R.color.black));
        addView(titleView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(makeSpec,MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}