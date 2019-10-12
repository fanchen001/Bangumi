package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.YdwService;

public class YdwPagerAdapter  extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"首页","电影", "电视剧", "动漫","综艺"};
    private final String[] PATHS = new String[]{"","1", "2", "4","3"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true};
    private final Boolean[] BANGUMI = new Boolean[]{true,false,false,false,false};

    public YdwPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position], getExtendInfo().toString(), 1, LOADS[position], false, false,null,BANGUMI[position]);
    }

    @Override
    public Object getExtendInfo() {
        return YdwService.class.getName();
    }
}