package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.AcgCuteFragment;
import com.fanchen.imovie.fragment.AcgMationFragment;
import com.fanchen.imovie.fragment.AcgSiteFragment;

/**
 * Created by fanchen on 2017/7/22.
 */
public class AcgPagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"资讯速递", "P站美图", "萌化应用"};

    public AcgPagerAdapter(FragmentManager fm) {
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
                return AcgMationFragment.newInstance();
            case 1:
                return AcgSiteFragment.newInstance();
            case 2:
                return AcgCuteFragment.newInstance();
            default:
                return null;
        }
    }
}
