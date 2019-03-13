package com.fanchen.imovie.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by fanchen on 2017/7/24.
 */
public abstract class BaseFragmentAdapter extends FragmentStatePagerAdapter {

    protected Fragment[] fragments = null;

    public BaseFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(fragments == null) fragments = new Fragment[getCount()];
        return fragments[position] == null ? fragments [position] = createFragment(position) : fragments[position] ;
    }

    @Override
    public int getCount() {
        return getTitles().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getTitles()[position];
    }

    /**
     *
     * @return
     */
    public abstract String[] getTitles();

    /**
     *
     * @param position
     * @return
     */
    public abstract Fragment createFragment(int position);

    public Object getExtendInfo(){
        return "";
    }

    public int getMultiple(){
        return 1;
    }

    public int getPageStart(){
        return 1;
    }
}
