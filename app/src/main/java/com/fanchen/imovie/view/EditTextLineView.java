package com.fanchen.imovie.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.fanchen.imovie.R;

/**
 *
 * Created by fanchen on 2017/7/25.
 */
public class EditTextLineView extends TextView {

    private final Paint c = new MPaint(this, 5);

    public EditTextLineView(Context context) {
        super(context);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        this.c.setColor(typedValue.data);
    }

    public EditTextLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        this.c.setColor(typedValue.data);
    }

    public EditTextLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        this.c.setColor(typedValue.data);
    }

    protected void onDraw(Canvas paramCanvas) {
        this.c.setStrokeWidth(2.0F);
        paramCanvas.drawLine(0.0F, getMeasuredHeight() / 2, getMeasuredWidth(), getMeasuredHeight() / 2, this.c);
        this.c.setStrokeWidth(1.0F);
    }

    public static class MPaint extends Paint {
        public MPaint(EditTextLineView paramEditTextLineView, int paramInt) {
            super(paramInt);
            setStyle(Paint.Style.FILL);
        }
    }
}
