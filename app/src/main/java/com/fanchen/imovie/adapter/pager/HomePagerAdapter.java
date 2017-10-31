package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.HomeIndexFragment;
import com.fanchen.imovie.fragment.HomeMoreFragment;


/**
 * 主界面Fragment模块Adapter
 * Created by fanchen on 2017/7/25.
 */
public class HomePagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"推荐", "番剧","影視", "音乐", "更多"};
    private final String[] PATHS = new String[]{"index.html", "list/24/","list/23/", "list/20/"};

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return HomeIndexFragment.newInstance(PATHS[position],false,false);
            case 1:
                return HomeIndexFragment.newInstance(PATHS[position],true,true);
            case 2:
                return HomeIndexFragment.newInstance(PATHS[position],false,true);
            case 3:
                return HomeIndexFragment.newInstance(PATHS[position],false,true);
            case 4:
                return HomeMoreFragment.newInstance();
            default:
                return null;
        }
    }
}
