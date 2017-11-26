package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.DownloadPagerAdapter;
import com.fanchen.imovie.base.BaseTabActivity;
import com.fanchen.imovie.fragment.DownloadFragment;
import com.fanchen.imovie.util.LogUtil;

/**
 * 下载管理   视频、应用
 * Created by fanchen on 2017/10/3.
 */
public class DownloadTabActivity extends BaseTabActivity implements ViewPager.OnPageChangeListener, IMovieAppliction.OnTaskRuningListener {

    private DownloadFragment mDownloadFragment;
    private boolean deleteMode = false;

    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, DownloadTabActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        mDownloadFragment = (DownloadFragment) getVisibleFragment();
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new DownloadPagerAdapter(fm);
    }

    @Override
    protected void setListener() {
        super.setListener();
        getViewPager().addOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                DownloadFragment downloadFragment = (DownloadFragment) getVisibleFragment();
                if(downloadFragment != null){
                    downloadFragment.setDeleteMode(deleteMode = true);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.download);
    }

    @Override
    public void onBackPressed() {
        if(deleteMode){
            DownloadFragment downloadFragment = (DownloadFragment) getVisibleFragment();
            if(downloadFragment != null){
                downloadFragment.setDeleteMode(deleteMode = false);
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(appliction != null){
            appliction.setRuningListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(appliction != null){
            appliction.setRuningListener(null);
        }
    }

    @Override
    public void onTaskUpdate(DownloadTask task) {
        if (mDownloadFragment != null)
            mDownloadFragment.onTaskUpdate(task);
    }

    @Download.onTaskCancel
    public void onTaskCancel(DownloadTask task) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        deleteMode = false;
        mDownloadFragment = (DownloadFragment) getUnVisibleFragment();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
