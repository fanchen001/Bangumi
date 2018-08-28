package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.BobmaoService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class BobmaoPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","电影", "电视剧", "动漫","综艺","娱乐"};
    private final String[] PATHS = new String[]{"","1", "2", "3","4","5"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true,true};

    private final String REFERER = "https://www.bobmao.com/";

    public BobmaoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,2,LOADS[position],false,false,REFERER,false);
    }

    @Override
    public Object getExtendInfo() {
        return BobmaoService.class.getName();
    }

    @Override
    public int getPageStart() {
        return 2;
    }
}
