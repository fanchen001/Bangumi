package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.RankingListFragment;

/**
 * Created by fanchen on 2017/9/20.
 */
public class RankingPagerAdapter extends BaseFragmentAdapter {
    private final String[] TITLES = new String[]{"新番", "动画", "音乐", "游戏", "三次元", "影视"};
    private final String[] TIDS = new String[]{"24", "19", "20", "21", "22", "23"};

    private String date = "1";

    public RankingPagerAdapter(FragmentManager fm,String date) {
        super(fm);
        this.date = date;
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return RankingListFragment.newInstance(TIDS[position],date);
    }
}
