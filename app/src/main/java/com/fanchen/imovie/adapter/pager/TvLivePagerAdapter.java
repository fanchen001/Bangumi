//package com.fanchen.imovie.adapter.pager;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//
//import com.fanchen.imovie.base.BaseFragmentAdapter;
//import com.fanchen.imovie.fragment.TvLiveListFragment;
//
///**
// * TvLivePagerAdapter
// * Created by fanchen on 2018/9/27.
// */
//public class TvLivePagerAdapter extends BaseFragmentAdapter{
//
//    private final String[] TITLES = new String[]{"央视","卫视", "CIBN", "特色"};
//    private final String[] GROUPID = new String[]{"13558","13556", "13576", "13538"};
//
//    public TvLivePagerAdapter(FragmentManager fm) {
//        super(fm);
//    }
//
//    @Override
//    public String[] getTitles() {
//        return TITLES;
//    }
//
//    @Override
//    public Fragment createFragment(int position) {
//        return TvLiveListFragment.newInstance(GROUPID[position]);
//    }
//}
