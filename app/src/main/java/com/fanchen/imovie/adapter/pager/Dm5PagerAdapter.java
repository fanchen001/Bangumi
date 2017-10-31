package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.Dm5Service;

/**
 * Created by fanchen on 2017/9/24.
 */
public class Dm5PagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"番组", "完载番组", "剧场版/OVA"};
    private final String[] PATHS = new String[]{"bgm", "end", "ova"};

    public Dm5PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return VideoListFragment.newInstance(PATHS[position], getExtendInfo().toString(), 1, false,false,false);
        }
        return VideoListFragment.newInstance(PATHS[position], getExtendInfo().toString(), 1);
    }

    @Override
    public Object getExtendInfo() {
        return Dm5Service.class.getName();
    }
}
