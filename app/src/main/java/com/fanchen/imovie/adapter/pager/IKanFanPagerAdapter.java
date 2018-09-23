package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.IKanFanService;

/**
 * IKanFanPagerAdapter
 * Created by fanchen on 2017/9/24.
 */
public class IKanFanPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","电视剧", "电影", "动漫"};
    private final String[] PATHS = new String[]{"","Tv", "Movie", "Cartoon"};
    private final Boolean[] BANGUMI = new Boolean[]{true,false,false,false,};
    private final String REFERER = "https://m.ysba.cc/";

    public IKanFanPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,false,false,false,REFERER,BANGUMI[position]);
    }

    @Override
    public Object getExtendInfo() {
        return IKanFanService.class.getName();
    }
}
