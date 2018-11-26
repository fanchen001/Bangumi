package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.SmdyService;

/**
 * SmdyPagerAdapter
 * Created by fanchen on 2017/9/24.
 */
public class SmdyPagerAdapter extends BaseFragmentAdapter{

    private static final String[] TITLES = new String[]{"首页","热门电影", "电视剧","动漫","美剧"};
    public static final String[] PATHS = new String[]{"","1", "2", "3","30"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true};
    private final Boolean[] BANGUMI = new Boolean[]{true,false,false,false,false};

    private final String REFERER = "https://m.ism88.net/";

    public SmdyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
      public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,LOADS[position],false,false,REFERER,BANGUMI[position]);
    }

    @Override
    public Object getExtendInfo() {
        return SmdyService.class.getName();
    }
}
