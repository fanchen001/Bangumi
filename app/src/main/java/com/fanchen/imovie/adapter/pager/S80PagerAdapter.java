package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.S80Service;

/**
 * Created by fanchen on 2017/9/23.
 */
public class S80PagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"电影", "电视剧",  "动漫", "音乐","短片"};
    private final String[] PATHS = new String[]{"1", "2","14","5","6"};

    public S80PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),24,true,false,true);
    }

    @Override
    public Object getExtendInfo() {
        return S80Service.class.getName();
    }
}
