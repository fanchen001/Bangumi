package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.CollectPagerAdapter;
import com.fanchen.imovie.base.BaseTabActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.util.DialogUtil;

/**
 * 我的收藏
 * Created by fanchen on 2017/8/15.
 */
public class CollectTabActivity extends BaseTabActivity {

    private CollectPagerAdapter mPagerAdapter;

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CollectTabActivity.class);
        context.startActivity(intent);
    }

    @Override
    public String getActivityTitle() {
        return getString(R.string.item_favourite);
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return mPagerAdapter = new CollectPagerAdapter(fm);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            DialogUtil.showMaterialDialog(this, getString(R.string.collect_clear), buttonClickListener);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clear, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     */
    private OnButtonClickListener buttonClickListener = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            OnClearListener itemAtPosition = mPagerAdapter.getItemAtPosition(getViewPager().getCurrentItem());
            if (btn == OnButtonClickListener.RIGHT && itemAtPosition != null) {
                itemAtPosition.onClear();
            }
            dialog.dismiss();
        }

    };

    /**
     *
     */
    public interface OnClearListener {

        /**
         *
         */
        void onClear();

    }
}
