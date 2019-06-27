package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
//import com.fanchen.imovie.fragment.DianxiumeiFragment;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.DianxiumeiService;

/**
 * Created by fanchen on 2017/10/14.
 */
public class Xiu169PagerAdapter extends BaseFragmentAdapter{
    
    private final String[] TITLES = new String[]{"电影", "宅舞", "音乐","舞蹈","游戏","小品"};
    private final String[] VALUES = new String[]{"http://honglez.cn/dy/rm.php",
            "http://honglez.cn/huya/so.php?id=zhaiwu", "http://honglez.cn/yinyue/s.php",
            "http://honglez.cn/huya/so.php?id=fuli","http://honglez.cn/baidu/s.php?id=youxi",
            "http://honglez.cn/baidu/s.php?id=xiaopin"};

    public Xiu169PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(VALUES[position], DianxiumeiService.class.getName(), 1);
    }

    @Override
    public Object getExtendInfo() {
        return DianxiumeiService.class.getName();
    }

    @Override
    public int getMultiple() {
        return 10;
    }
}
