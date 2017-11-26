package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.A4dyPagerAdapter;
import com.fanchen.imovie.adapter.pager.BabayuPagerAdapter;
import com.fanchen.imovie.adapter.pager.BumimiPagerAdapter;
import com.fanchen.imovie.adapter.pager.Dm5PagerAdapter;
import com.fanchen.imovie.adapter.pager.HaliHaliPagerAdapter;
import com.fanchen.imovie.adapter.pager.IKanFanPagerAdapter;
import com.fanchen.imovie.adapter.pager.JrenPagerAdapter;
import com.fanchen.imovie.adapter.pager.K8dyPagerAdapter;
import com.fanchen.imovie.adapter.pager.KankanPagerAdapter;
import com.fanchen.imovie.adapter.pager.KmaoPagerAdapter;
import com.fanchen.imovie.adapter.pager.LL520PagerAdapter;
import com.fanchen.imovie.adapter.pager.MmyyPagerAdapter;
import com.fanchen.imovie.adapter.pager.S80PagerAdapter;
import com.fanchen.imovie.adapter.pager.XiaokabaPagerAdapter;
import com.fanchen.imovie.adapter.pager.Xiu169PagerAdapter;
import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.base.BaseTabActivity;
import com.fanchen.imovie.dialog.fragment.SearchDialogFragment;
import com.fanchen.imovie.entity.face.ISearchWord;

/**
 * [五弹幕][布米米]等其他视频站
 * 的TabActivity
 * Created by fanchen on 2017/9/23.
 */
public class VideoTabActivity extends BaseTabActivity implements SearchDialogFragment.OnSearchClickListener {
    public static final int DM5 = 1;
    public static final int KANKANWU = 2;
    public static final int XIU169 = 3;
    public static final int XIAOKANBA = 4;
    public static final int W4K = 5;
    public static final int JREN = 6;
    public static final int BUMIMI = 7;
    public static final int BABAYU = 8;
    public static final int A4DY = 9;
    public static final int K8DY = 10;
    public static final int LL520 = 11;
    public static final int S80 = 12;
    public static final int HALIHALI = 13;
    public static final int IKANFAN = 14;
    public static final int MMYY = 15;

    public static final String TYPE = "type";
    public static final String TITLE = "title";

    private SearchDialogFragment mSearchFragment = SearchDialogFragment.newInstance();

    private BaseFragmentAdapter mPagerAdapter;
    private int type;
    private String title;

    /**
     * @param context
     * @param title
     * @param type
     */
    public static void startActivity(Context context, String title, int type) {
        try {
            Intent intent = new Intent(context, VideoTabActivity.class);
            intent.putExtra(TYPE, type);
            intent.putExtra(TITLE, title);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param title
     */
    public static void startActivity(Context context, String title) {
        startActivity(context, title, S80);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mSearchFragment.setOnSearchClickListener(this);
    }

    @Override
    protected int getTabMode(PagerAdapter adapter) {
        return type == DM5 || type == HALIHALI || type == IKANFAN ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        type = getIntent().getIntExtra(TYPE, S80);
        title = getIntent().getStringExtra(TITLE);
        super.initActivity(savedState, inflater);
    }

    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        if (type == S80) {
            mPagerAdapter = new S80PagerAdapter(fm);
        } else if (type == BUMIMI) {
            mPagerAdapter = new BumimiPagerAdapter(fm);
        } else if(type == DM5){
            mPagerAdapter = new Dm5PagerAdapter(fm);
        } else if(type == XIU169){
            mPagerAdapter = new Xiu169PagerAdapter(fm);
        }else if(type == XIAOKANBA){
            mPagerAdapter = new XiaokabaPagerAdapter(fm);
        }else if(type == W4K){
            mPagerAdapter = new KmaoPagerAdapter(fm);
        }else if(type == A4DY){
            mPagerAdapter = new A4dyPagerAdapter(fm);
        }else if(type == KANKANWU){
            mPagerAdapter = new KankanPagerAdapter(fm);
        }else if(type == BABAYU){
            mPagerAdapter = new BabayuPagerAdapter(fm);
        }else if(type == LL520){
            mPagerAdapter = new LL520PagerAdapter(fm);
        }else if(type == K8DY){
            mPagerAdapter = new K8dyPagerAdapter(fm);
        }else if(type == IKANFAN){
            mPagerAdapter = new IKanFanPagerAdapter(fm);
        }else if(type == HALIHALI){
            mPagerAdapter = new HaliHaliPagerAdapter(fm);
        }else if(type == MMYY){
            mPagerAdapter = new MmyyPagerAdapter(fm);
        }else {
            mPagerAdapter = new JrenPagerAdapter(fm);
        }
        return mPagerAdapter;
    }

    @Override
    protected String getActivityTitle() {
        return title;
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
    public void onSearchClick(ISearchWord word) {
        String classNmae = mPagerAdapter.getExtendInfo().toString();
        int multiple = mPagerAdapter.getMultiple();
        String wordString = word.getWord();
        SearchVideoActivity.startActivity(this,wordString, classNmae,multiple);
    }

}
