package com.fanchen.imovie.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;

/**
 * 自定义EmptyView
 *
 * @author fanchen
 */
public class CustomEmptyView extends FrameLayout {
    public static final int TYPE_NON = 0;
    public static final int TYPE_EMPTY = 1;
    public static final int TYPE_ERROR = 2;

    private ImageView mEmptyImg;

    private TextView mEmptyText;

    private int type = TYPE_EMPTY;

    public CustomEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomEmptyView(Context context) {
        this(context, null);
    }

    public CustomEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_empty, this);
        mEmptyImg = (ImageView) view.findViewById(R.id.empty_img);
        mEmptyText = (TextView) view.findViewById(R.id.empty_text);
        setVisibility(GONE);
    }

    public void setEmptyImage(int imgRes) {
        mEmptyImg.setImageResource(imgRes);
    }

    public void setEmptyText(String text) {
        mEmptyText.setText(text);
    }

    public void setEmptyType(int type) {
        if (this.type == TYPE_ERROR && type == TYPE_EMPTY)
            return;
        this.type = type;
        if (TYPE_EMPTY == this.type) {
            mEmptyImg.setImageResource(R.drawable.ic_load_empty);
            mEmptyText.setText(R.string.load_hit_empty);
            setVisibility(VISIBLE);
        } else if (TYPE_ERROR == this.type) {
            mEmptyImg.setImageResource(R.drawable.ic_load_error);
            mEmptyText.setText(R.string.load_hit_error);
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {

        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == TYPE_ERROR && l != null) {
                    l.onClick(v);
                }
            }
        });

    }

}
