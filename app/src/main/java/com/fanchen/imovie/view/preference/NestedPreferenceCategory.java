package com.fanchen.imovie.view.preference;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.fanchen.imovie.R;

/**
 * NestedPreferenceCategory
 * Created by fanchen on 2017/7/26.
 */
public class NestedPreferenceCategory extends PreferenceCategory {

    public NestedPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NestedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedPreferenceCategory(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if(view instanceof TextView){
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            ((TextView)view).setTextColor(typedValue.data);
        }
    }
}
