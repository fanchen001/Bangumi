package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.JugouService;

/**
 * JugouPagerAdapter
 * Created by fanchen on 2018/6/13.
 */
public class JugouPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","电视剧", "电影", "动漫"};
    private final String[] PATHS = new String[]{"","2","1","4",""};
    private final Boolean[] BANNERS = new Boolean[]{true,false,false,false,false};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true};

    public JugouPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position], getExtendInfo().toString(), 1, LOADS[position], false, false,"",BANNERS[position]);
    }

    @Override
    public Object getExtendInfo() {
        return JugouService.class.getName();
    }
}
