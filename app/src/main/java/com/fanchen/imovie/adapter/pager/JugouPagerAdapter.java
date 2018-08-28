package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.JugouService;

/**
 * JugouPagerAdapter
 * Created by fanchen on 2018/6/13.
 */
public class JugouPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"首页","电视剧", "电影", "动漫","综艺"};
    private final String[] PATHS = new String[]{"http://video.jfenxiang.com/","http://video.jfenxiang.com/movie.php?m=/dianying/list.php?cat=all",
            "http://video.jfenxiang.com/tv.php?u=/dianshi/list.php?cat=all", "http://video.jfenxiang.com/dongman.php?m=/dongman/list.php?cat=all",
            "http://video.jfenxiang.com/zongyi.php?m=/zongyi/list.php?cat=all"};
    private final Boolean[] LOADS = new Boolean[]{false,true,true,true,true};

    public JugouPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(PATHS[position], getExtendInfo().toString(), 1, LOADS[position], false, false);
    }

    @Override
    public Object getExtendInfo() {
        return JugouService.class.getName();
    }
}
