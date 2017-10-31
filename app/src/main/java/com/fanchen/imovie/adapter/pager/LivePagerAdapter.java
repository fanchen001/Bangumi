package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.TvLiveListFragment;

/**
 *
 * Created by fanchen on 2017/8/2.
 */
public class LivePagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"卫视", "央视", "CIBN", "特色","其他"};

    public LivePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return TvLiveListFragment.newInstance(TITLES[position]);
    }

}
