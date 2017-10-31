package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.RankingPagerAdapter;
import com.fanchen.imovie.base.BaseTabActivity;

/**
 * 全站排行页面
 * Created by fanchen on 2017/9/20.
 */
public class RankingTabActivity extends BaseTabActivity{

    public static final int RANK_TYPE_DAY = 0;
    public static final int RANK_TYPE_WEEK = 1;

    private int rankType = RANK_TYPE_WEEK;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RankingTabActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new RankingPagerAdapter(fm,String.valueOf(rankType));
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.ranking);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ranking, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_day:
                if (rankType == RANK_TYPE_WEEK) {
                    rankType = RANK_TYPE_DAY;
                    int currentItem = mViewPager.getCurrentItem();
                    mViewPager.setAdapter(getAdapter(getSupportFragmentManager()));
                    mViewPager.setCurrentItem(currentItem);
                }
                break;
            case R.id.action_week:
                if (rankType == RANK_TYPE_DAY) {
                    rankType = RANK_TYPE_WEEK;
                    int currentItem = mViewPager.getCurrentItem();
                    mViewPager.setAdapter(getAdapter(getSupportFragmentManager()));
                    mViewPager.setCurrentItem(currentItem);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
