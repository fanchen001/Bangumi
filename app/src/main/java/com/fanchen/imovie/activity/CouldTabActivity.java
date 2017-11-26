package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.ColudPagerAdapter;
import com.fanchen.imovie.base.BaseTabActivity;


/**
 * 云播放界面
 * Created by fanchen on 2017/8/4.
 */
public class CouldTabActivity extends BaseTabActivity {

    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, CouldTabActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new ColudPagerAdapter(fm);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.could_player);
    }

}
