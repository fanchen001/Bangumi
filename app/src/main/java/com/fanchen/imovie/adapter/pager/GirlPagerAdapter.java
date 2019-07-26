package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.V6Service;

/**
 * Created by fanchen on 2017/10/13.
 */
public class GirlPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"女神播", "颜值播","舞蹈播", "热舞播"};
    private final String[] VALUES = new String[]{"","u0","u1","u2"};

    public GirlPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(VALUES[position], V6Service.class.getName(),1,false,true,false);
    }

    @Override
    public Object getExtendInfo() {
        return V6Service.class.getName();
    }

}
