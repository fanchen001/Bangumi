package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.A4dyService;
import com.fanchen.imovie.retrofit.service.KmaoService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class A4dyPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","电视剧", "电影", "动漫","综艺"};
    private final String[] PATHS = new String[]{"index.html","2", "1", "4","3"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true};

    private final String REFERER = "http://c.aaccy.com/";

    public A4dyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,LOADS[position],false,false,REFERER,false);
    }

    @Override
    public Object getExtendInfo() {
        return A4dyService.class.getName();
    }
}
