package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.ShortVideoFragment;

/**
 * Created by fanchen on 2017/9/22.
 */
public class ShortVideoPagerAdapter extends BaseFragmentAdapter{
    private final String[] TITLES = new String[]{"推荐", "搞笑", "娱乐", "游戏", "音乐", "影视","军事","社会","汽车","生活","科技","体育"};
    private final String[] TIDS = new String[]{"0", "1", "2", "3", "4", "5","6","7","8","9","10","11"};

    public ShortVideoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return ShortVideoFragment.newInstance(TIDS[position]);
    }

}
