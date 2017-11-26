package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.LivePagerAdapter;
import com.fanchen.imovie.base.BaseTabActivity;


/**
 * 电视直播
 * Created by fanchen on 2017/8/2.
 */
public class TvLiveTabActivity extends BaseTabActivity {

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, TvLiveTabActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new LivePagerAdapter(fm);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.item_live);
    }

}
