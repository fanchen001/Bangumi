package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.CouldFragment;
import com.fanchen.imovie.fragment.MagnetFragment;

/**
 * Created by fanchen on 2017/8/4.
 */
public class ColudPagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"云搜索","磁力播"};

    public ColudPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return position == 1 ? MagnetFragment.newInstance() : CouldFragment.newInstance();
    }
}
