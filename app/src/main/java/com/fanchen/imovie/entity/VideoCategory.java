package com.fanchen.imovie.entity;

import com.fanchen.imovie.R;
import com.fanchen.imovie.entity.face.IViewType;

/**
 * VideoCategory
 * Created by fanchen on 2017/11/9.
 */
public class VideoCategory implements IViewType {
    private String title;
    private String url;
    private int position;
    private boolean hot;
    private int type;
    private boolean success = true;

    private static final int DRAWABLE[] = {R.drawable.ic_category_01, R.drawable.ic_category_02,
            R.drawable.ic_category_03, R.drawable.ic_category_04, R.drawable.ic_category_05,
            R.drawable.ic_category_06, R.drawable.ic_category_07, R.drawable.ic_category_08,
            R.drawable.ic_category_09, R.drawable.ic_category_10, R.drawable.ic_category_11,
            R.drawable.ic_category_12, R.drawable.ic_category_13, R.drawable.ic_category_14,
            R.drawable.ic_category_15, R.drawable.ic_category_16, R.drawable.ic_category_17,
            R.drawable.ic_category_18, R.drawable.ic_category_19, R.drawable.ic_category_20,
            R.drawable.ic_category_21,R.drawable.ic_category_22,R.drawable.ic_category_23,
            R.drawable.ic_category_24,R.drawable.ic_category_25,R.drawable.ic_category_26
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDrawable() {
        return DRAWABLE[position % DRAWABLE.length];
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    @Override
    public int getViewType() {
        return TYPE_NORMAL;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
