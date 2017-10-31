package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.DownloadFragment;

/**
 * Created by fanchen on 2017/10/3.
 */
public class DownloadPagerAdapter extends BaseFragmentAdapter{

    private final String[] TITLES = new String[]{"视频下载", "应用下载"};
    private final String[] SUFFIX = new String[]{".mp4/.avi/.wmv/.rmvb", ".apk/.*"};

    public DownloadPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        return DownloadFragment.newInstance(SUFFIX[position]);
    }
}
