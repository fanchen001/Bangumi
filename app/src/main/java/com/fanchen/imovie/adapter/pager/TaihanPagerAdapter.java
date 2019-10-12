package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.TaihanService;

/**
 * TaihanPagerAdapter
 * Created by Administrator on 2018/4/19.
 */
public class TaihanPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","泰剧", "泰影", "韩剧","综艺"};
    private final String[] PATHS = new String[]{"","taiju", "taiying", "hanju","zongyi"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true};

    public TaihanPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position], getExtendInfo().toString(), 1, LOADS[position], false, false, null, true);
    }

    @Override
    public Object getExtendInfo() {
        return TaihanService.class.getName();
    }

}
