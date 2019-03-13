package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.XiaokanbaService;

/**
 * XiaokabaPagerAdapter
 * Created by fanchen on 2017/10/15.
 */
public class XiaokabaPagerAdapter extends BaseFragmentAdapter {
    private final String[] TITLES = new String[]{"首页","电影", "电视剧", "综艺","动漫"};
    //frim/index1.html
    private final String[] PATHS = new String[]{"","1", "2", "3","4"};

    public XiaokabaPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString());
        }
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,true,false,false);
    }

    @Override
    public Object getExtendInfo() {
        return XiaokanbaService.class.getName();
    }
}
