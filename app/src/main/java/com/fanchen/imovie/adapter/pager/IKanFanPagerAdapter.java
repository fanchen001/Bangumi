package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.IKanFanService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class IKanFanPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"动漫", "剧场版", "特别篇","TV版"};
    private final String[] PATHS = new String[]{"dongman", "juchang", "tebie","tv"};

    private final String REFERER = "http://m.ikanfan.com";

    public IKanFanPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position],getExtendInfo().toString(),1,true,false,false,REFERER,false);
    }

    @Override
    public Object getExtendInfo() {
        return IKanFanService.class.getName();
    }
}
