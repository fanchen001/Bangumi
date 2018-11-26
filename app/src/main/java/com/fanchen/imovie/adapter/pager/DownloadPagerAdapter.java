package com.fanchen.imovie.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.fragment.DownloadFragment;
import com.fanchen.imovie.fragment.M3u8Fragment;
import com.fanchen.imovie.fragment.XiguaFragment;
import com.fanchen.imovie.fragment.XunleiFragment;

/**
 * DownloadPagerAdapter
 * Created by fanchen on 2017/10/3.
 */
public class DownloadPagerAdapter extends BaseFragmentAdapter {

    private final String[] TITLES = new String[]{"M3u8下载", "西瓜视频","迅雷下载", "视频下载", "应用下载"};
    private final String[] SUFFIX = new String[]{"", "","", ".mp4/.avi/.wmv/.rmvb/.rm", ".apk/.*"};

    public DownloadPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public String[] getTitles() {
        return TITLES;
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return M3u8Fragment.newInstance();
        } else if (position == 1) {
            return XiguaFragment.newInstance();
        }  else if (position == 2) {
            return XunleiFragment.newInstance();
        } else {
            return DownloadFragment.newInstance(SUFFIX[position]);
        }
    }
}
