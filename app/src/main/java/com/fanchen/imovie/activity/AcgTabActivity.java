package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.AcgPagerAdapter;
import com.fanchen.imovie.base.BaseTabActivity;


/**
 * ACG小队
 * Created by fanchen on 2017/8/22.
 */
public class AcgTabActivity extends BaseTabActivity {

    /**
     *
     * @param context
     */
    public static void startActivity(Context context){
        try {
            Intent intent = new Intent(context,AcgTabActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new AcgPagerAdapter(fm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.acg_tree);
    }
}
