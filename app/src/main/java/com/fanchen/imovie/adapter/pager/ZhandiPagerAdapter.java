package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.ZhandiService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class ZhandiPagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"首页", "电影", "电视剧", "动漫", "综艺"};
    //    private final String[] PATHS = new String[]{"","dianying", "dianshiju", "Dm","Zy"};
    private final String[] PATHS = new String[]{"", "Movie", "Tv", "Cartoon", "Variety"};
    private final Boolean[] LOADS = new Boolean[]{false, false, false, true, true};

    //    private final String REFERER = "http://m.zhandi.cc/";
    private final String REFERER = "https://m.ywt.cc/";

    public ZhandiPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position], getExtendInfo().toString(), 1, 2, LOADS[position], false, false, REFERER, false);
    }

    @Override
    public Object getExtendInfo() {
        return ZhandiService.class.getName();
    }
}
