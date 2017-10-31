package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.activity.CollectTabActivity;
import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.fragment.CollectFragment;

/**
 * Created by fanchen on 2017/7/24.
 */
public class CollectPagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"视频收藏", "直播收藏"};

    public CollectPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CollectFragment.newInstance(VideoCollect.TYPE_VIDEO);
            case 1:
                return CollectFragment.newInstance(VideoCollect.TYPE_LIVE);
            default:
                return null;
        }
    }

    /**
     *
     * @param position
     * @return
     */
    public CollectTabActivity.OnClearListener getItemAtPosition(int position){
        if(fragments == null) return null;
        return (CollectTabActivity.OnClearListener) fragments[position];
    }
}
