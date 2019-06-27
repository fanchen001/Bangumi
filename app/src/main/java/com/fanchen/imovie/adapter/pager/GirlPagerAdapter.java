package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.VideoListFragment;
import com.fanchen.imovie.retrofit.service.DianxiumeiService;
//import com.fanchen.imovie.fragment.DianxiumeiFragment;

/**
 * Created by fanchen on 2017/10/13.
 */
public class GirlPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"女神播", "颜值播","舞蹈播", "热舞播"};
    private final String[] VALUES = new String[]{"http://honglez.cn/zb/huajiao.php?id=2&p=0",
            "http://honglez.cn/zb/huajiao.php?id=800","http://honglez.cn/zb/huajiao.php?id=1000",
            "http://honglez.cn/zb/huajiao.php?id=801"};
    private final int[] MULTIPLE = new int[]{20,20,10,20};

    public GirlPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return VideoListFragment.newInstance(VALUES[position], DianxiumeiService.class.getName(),MULTIPLE[position],true,true,false);
    }

    @Override
    public Object getExtendInfo() {
        return DianxiumeiService.class.getName();
    }
}
