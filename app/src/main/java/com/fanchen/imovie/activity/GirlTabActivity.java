package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.GirlPagerAdapter;
import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.base.BaseTabActivity;
import com.fanchen.imovie.dialog.fragment.SearchDialogFragment;
import com.fanchen.imovie.entity.face.ISearchWord;

/**
 * Created by fanchen on 2017/10/13.
 */
public class GirlTabActivity extends BaseTabActivity implements SearchDialogFragment.OnSearchClickListener {

    private SearchDialogFragment mSearchFragment = SearchDialogFragment.newInstance();

    public static void startActivity(Context context) {
        try {
            Intent intent = new Intent(context, GirlTabActivity.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mSearchFragment.setOnSearchClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if (mSearchFragment != null) {
                    mSearchFragment.show(getSupportFragmentManager(), getClass().getSimpleName());
                }
                break;
            case R.id.action_download:
                DownloadTabActivity.startActivity(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        return new GirlPagerAdapter(fm);
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.gril_live);
    }

    @Override
    public void onSearchClick(ISearchWord word) {
        String wordString = word.getWord();
        String clazz = ((BaseFragmentAdapter) getMViewPager().getAdapter()).getExtendInfo().toString();
        int multiple = ((BaseFragmentAdapter) getMViewPager().getAdapter()).getMultiple();
        int pageStart = ((BaseFragmentAdapter) getMViewPager().getAdapter()).getPageStart();
        SearchVideoActivity.startActivity(this,wordString, clazz,pageStart,multiple);
    }
}
