package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.HaliHaliService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class HaliHaliPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","动漫", "TV剧", "影视"};
    private final String[] PATHS = new String[]{"","dongman","dianshiju", "dianying"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true};
    private final Boolean[] BANGUMI = new Boolean[]{true,false,false,false};

    public HaliHaliPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,2,LOADS[position],false,false,null,BANGUMI[position]);
    }

    @Override
    public Object getExtendInfo() {
        return HaliHaliService.class.getName();
    }
}
