package com.fanchen.imovie.entity;

import com.fanchen.imovie.R;
import com.fanchen.imovie.entity.face.IViewType;

/**
 * Created by fanchen on 2017/11/9.
 */
public class VideoCategory implements IViewType {
    private String title;
    private int position;
    private int type;

    private static final int DRAWABLE[] = {R.drawable.ic_category_t1, R.drawable.ic_category_t11,
            R.drawable.ic_category_t119, R.drawable.ic_category_t129, R.drawable.ic_category_t13,
            R.drawable.ic_category_t155, R.drawable.ic_category_t160, R.drawable.ic_category_t165,
            R.drawable.ic_category_t3, R.drawable.ic_category_t36, R.drawable.ic_category_t4,
            R.drawable.ic_category_t5, R.drawable.ic_category_live, R.drawable.ic_category_promo,
            R.drawable.ic_bangumi_follow_home_ic_index,R.drawable.ic_live_center_sea_patro};

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

    @Override
    public int getViewType() {
        return TYPE_NORMAL;
    }
}
