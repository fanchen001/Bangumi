package com.fanchen.imovie.view.pager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class LoopPagerAdapter extends PagerAdapter {
    private final Context context;
    private final ArrayList<? extends IBanner> bannerInfos;//banner data
    private final LoopViewPager.OnBannerItemClickListener onBannerItemClickListener;
    private final LoopViewPager.OnLoadImageViewListener onLoadImageViewListener;

    public LoopPagerAdapter(Context context, List<? extends IBanner> bannerInfos, LoopViewPager.OnBannerItemClickListener onBannerItemClickListener, LoopViewPager.OnLoadImageViewListener onLoadImageViewListener) {
        this.context = context;
        this.bannerInfos = new ArrayList<>(bannerInfos);
        this.onBannerItemClickListener = onBannerItemClickListener;
        this.onLoadImageViewListener = onLoadImageViewListener;
    }


    @Override
    public int getCount() {
        return Short.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int index = position % bannerInfos.size();
        final IBanner bannerInfo = bannerInfos.get(index);
        View child = null;
        if (onLoadImageViewListener != null) {
            child = onLoadImageViewListener.createBannerView(context);
            onLoadImageViewListener.onLoadImageView(child, bannerInfo);
            container.addView(child);

            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBannerItemClickListener != null)
                        onBannerItemClickListener.onBannerClick(index, bannerInfos);
                }
            });
        } else {
            throw new NullPointerException("LoopViewPagerLayout onLoadImageViewListener is not initialize,Be sure to initialize the onLoadImageView");
        }


        return child;
    }
}