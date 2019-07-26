package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.CcyService;

/**
 * Created by fanchen on 2019/7/14.
 */
public class CcyPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页", "电影", "电视剧","动漫","综艺"};
    private final String[] VALUES = new String[]{"","dy","dsj","dm","zy"};
    private final Boolean[] BANGUMI = new Boolean[]{true,false,false,false,false};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true};

    public CcyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(VALUES[position],CcyService.class.getName(),1,2,LOADS[position],false,false,"",BANGUMI[position]);
    }

    @Override
    public Object getExtendInfo() {
        return CcyService.class.getName();
    }


}
