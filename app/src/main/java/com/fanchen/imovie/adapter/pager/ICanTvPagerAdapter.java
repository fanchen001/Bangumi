package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.TvLiveFragment;
import com.fanchen.imovie.retrofit.service.ICanTvService;

/**
 * ICanTvPagerAdapter
 * Created by fanchen on 2018/10/12.
 */
public class ICanTvPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"央视", "卫视",  "国外"};
    private final String[] PATHS = new String[]{"cctv.html", "tv.html", "hw.html"};

    public ICanTvPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return TvLiveFragment.newInstance(ICanTvService.class.getName(), PATHS[position]);
    }

}
