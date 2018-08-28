package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.WandouService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class WandouPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","电影", "电视剧", "动漫","综艺"};
    private final String[] PATHS = new String[]{"","dianying", "dianshiju", "dongman","zongyi"};

    private final String REFERER = "http://www.wandouys.com/";

    public WandouPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,false,false,false,REFERER,true);
    }

    @Override
    public Object getExtendInfo() {
        return WandouService.class.getName();
    }
}
