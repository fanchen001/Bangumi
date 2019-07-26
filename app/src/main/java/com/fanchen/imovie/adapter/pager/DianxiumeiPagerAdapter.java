package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
//import com.fanchen.imovie.fragment.DianxiumeiFragment;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.DianxiumeiService;

/**
 * Created by fanchen on 2017/10/14.
 */
public class DianxiumeiPagerAdapter extends BaseFragmentAdapter{
    
    private final String[] TITLES = new String[]{"首页", "电影", "电视剧","动漫","综艺","午夜"};
    private final String[] VALUES = new String[]{"","tv","movie","Animation","Arts","wy"};
    private final Boolean[] BANGUMI = new Boolean[]{true,false,false,false,false,false};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true,true};

    public DianxiumeiPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(VALUES[position],DianxiumeiService.class.getName(),1,LOADS[position],false,false,"",BANGUMI[position]);
    }

    @Override
    public Object getExtendInfo() {
        return DianxiumeiService.class.getName();
    }

//    @Override
//    public int getMultiple() {
//        return 10;
//    }

}
