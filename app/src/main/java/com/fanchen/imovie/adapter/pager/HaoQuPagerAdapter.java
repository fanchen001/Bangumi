package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.TvLiveFragment;
import com.fanchen.imovie.retrofit.service.HaoQuService;

/**
 * HaoQuPagerAdapter
 * Created by fanchen on 2018/10/10.
 */
public class HaoQuPagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"CCTV", "卫视", "省级", "港澳台", "国外"};
    private final String[] PATHS = new String[]{"1", "2", "3", "4", "5"};

    public HaoQuPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return TvLiveFragment.newInstance(HaoQuService.class.getName(), PATHS[position]);
    }

}
