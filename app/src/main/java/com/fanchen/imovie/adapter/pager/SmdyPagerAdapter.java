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

    private static final String[] TITLES = new String[]{"首页","热门电影", "电视剧","动漫","综艺"};
    public static final String[] PATHS = new String[]{"","dianying", "dianshiju", "dongman","zongyi"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true};
    private final Boolean[] BANGUMI = new Boolean[]{true,false,false,false,false};

    private final String REFERER = "http://m.xigua15.com/";

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
