package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.HackerPagerAdapter;
import com.fanchen.imovie.base.BaseTabActivity;

/**
 * Created by fanchen on 2017/10/10.
 */
public class HackerToolActivity extends BaseTabActivity{

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, HackerToolActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new HackerPagerAdapter(fm);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.hack);
    }
}
