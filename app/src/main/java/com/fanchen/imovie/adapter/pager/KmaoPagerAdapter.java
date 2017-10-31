package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.JrenService;
import com.fanchen.imovie.retrofit.service.KmaoService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class KmaoPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","电视剧", "电影", "动漫","综艺"};
    private final String[] PATHS = new String[]{"","tv", "movie", "Animation","Arts"};
    private final Boolean[] LOADS = new Boolean[]{false,false,false,true,true};

    public KmaoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,LOADS[position],false,false);
    }

    @Override
    public Object getExtendInfo() {
        return KmaoService.class.getName();
    }
}
