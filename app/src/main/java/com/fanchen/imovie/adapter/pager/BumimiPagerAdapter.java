package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.BumimiService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class BumimiPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"电影", "电视剧","番剧", "综艺", "微电影"};
    private final String[] PATHS = new String[]{"movie", "tv","comic", "zongyi","weidianying"};

    public BumimiPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString());
    }

    @Override
    public Object getExtendInfo() {
        return BumimiService.class.getName();
    }
}
