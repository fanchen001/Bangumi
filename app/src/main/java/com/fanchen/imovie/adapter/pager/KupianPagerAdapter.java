package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.KupianService;

/**
 * KupianPagerAdapter
 * Created by Administrator on 2018/4/19.
 */
public class KupianPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","电视剧", "电影", "动漫"};
    private final String[] PATHS = new String[]{"","dianshiju", "dianying", "dongman"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true};

    public KupianPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position], getExtendInfo().toString(), 1, LOADS[position], false, false);
    }

    @Override
    public Object getExtendInfo() {
        return KupianService.class.getName();
    }

}
