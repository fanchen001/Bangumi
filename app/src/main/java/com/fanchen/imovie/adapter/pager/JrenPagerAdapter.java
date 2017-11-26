package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.JrenService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class JrenPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"新番连载", "动漫完载", "剧场版/OVA", "肉番无修正"};
    private final String[] PATHS = new String[]{"dmlz", "xfwz", "ova","rfwx"};
    private final Boolean[] BANGUMI = new Boolean[]{true,false,false,false,false,false};

    public JrenPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1);
    }

    @Override
    public Object getExtendInfo() {
        return JrenService.class.getName();
    }
}
