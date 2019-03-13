package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.pager.A4dyPagerAdapter;
import com.fanchen.imovie.adapter.pager.AismPagerAdapter;
import com.fanchen.imovie.adapter.pager.BabayuPagerAdapter;
import com.fanchen.imovie.adapter.pager.BobmaoPagerAdapter;
import com.fanchen.imovie.adapter.pager.BumimiPagerAdapter;
import com.fanchen.imovie.adapter.pager.Dm5PagerAdapter;
import com.fanchen.imovie.adapter.pager.HaliHaliPagerAdapter;
import com.fanchen.imovie.adapter.pager.HaoQuPagerAdapter;
import com.fanchen.imovie.adapter.pager.ICanTvPagerAdapter;
import com.fanchen.imovie.adapter.pager.IKanFanPagerAdapter;
import com.fanchen.imovie.adapter.pager.JrenPagerAdapter;
import com.fanchen.imovie.adapter.pager.JugouPagerAdapter;
import com.fanchen.imovie.adapter.pager.K8dyPagerAdapter;
import com.fanchen.imovie.adapter.pager.KankanPagerAdapter;
import com.fanchen.imovie.adapter.pager.KmaoPagerAdapter;
import com.fanchen.imovie.adapter.pager.KupianPagerAdapter;
import com.fanchen.imovie.adapter.pager.LL520PagerAdapter;
import com.fanchen.imovie.adapter.pager.LaosijiPagerAdapter;
import com.fanchen.imovie.adapter.pager.MmyyPagerAdapter;
import com.fanchen.imovie.adapter.pager.S80PagerAdapter;
import com.fanchen.imovie.adapter.pager.SmdyPagerAdapter;
import com.fanchen.imovie.adapter.pager.TaihanPagerAdapter;
import com.fanchen.imovie.adapter.pager.TepianPagerAdapter;
import com.fanchen.imovie.adapter.pager.TvLivePagerAdapter;
import com.fanchen.imovie.adapter.pager.VipysPagerAdapter;
import com.fanchen.imovie.adapter.pager.WandouPagerAdapter;
import com.fanchen.imovie.adapter.pager.WeilaiPagerAdapter;
import com.fanchen.imovie.adapter.pager.XiaokabaPagerAdapter;
import com.fanchen.imovie.adapter.pager.Xiu169PagerAdapter;
import com.fanchen.imovie.adapter.pager.ZhandiPagerAdapter;
import com.fanchen.imovie.adapter.pager.ZzyoPagerAdapter;
import com.fanchen.imovie.adapter.pager.ZzzvzPagerAdapter;
import com.fanchen.imovie.base.BaseFragmentAdapter;
import com.fanchen.imovie.base.BaseTabActivity;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.dialog.fragment.SearchDialogFragment;
import com.fanchen.imovie.entity.face.ISearchWord;
import com.fanchen.imovie.util.DialogUtil;

/**
 * [五弹幕][布米米]等其他视频站
 * 的TabActivity
 * Created by fanchen on 2017/9/23.
 */
public class VideoTabActivity extends BaseTabActivity implements SearchDialogFragment.OnSearchClickListener {
    public static final int ICAN_LIVE = -3;
    public static final int HQ_LIVE = -2;
    public static final int DYTT_LIVE = -1;
    public static final int DM5 = 1;
    public static final int WANDOU = 2;
    public static final int SMDY = 3;
    public static final int AISM = 4;
    public static final int ZHANDI = 5;
    public static final int XIU169 = 6;
    public static final int BUMIMI = 7;
    public static final int BABAYU = 8;
    public static final int A4DY = 9;
    public static final int K8DY = 10;
    public static final int LL520 = 11;
    public static final int S80 = 12;
    public static final int HALIHALI = 13;
    public static final int IKANFAN = 14;
    public static final int MMYY = 15;
    public static final int KANKANWU = 16;
    public static final int XIAOKANBA = 17;
    public static final int W4K = 18;
    public static final int JREN = 19;
    public static final int SANMAO = 20;
    public static final int TAIHAN = 21;
    public static final int KUPIAN = 22;
    public static final int TEPIAN = 23;
    public static final int JUGOU = 24;
    public static final int LAOSIJI = 25;
    public static final int ZZZVZ = 26;
    public static final int VIPYS = 27;
    public static final int FEIFAN = 28;
    public static final int ZZYO = 29;

    public static final String TYPE = "type";
    public static final String TITLE = "title";

    private SearchDialogFragment mSearchFragment = SearchDialogFragment.newInstance();

    private SharedPreferences mSharedPreferences;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startLiveActivity(Context context, String type) {
        String string = context.getString(R.string.tv_live);
        if ("dytt".equalsIgnoreCase(type)) {
            VideoTabActivity.startActivity(context, string, VideoTabActivity.DYTT_LIVE);
        } else if ("ican".equalsIgnoreCase(string)) {
            VideoTabActivity.startActivity(context, string, VideoTabActivity.ICAN_LIVE);
        } else {
            VideoTabActivity.startActivity(context, string, VideoTabActivity.HQ_LIVE);
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
        return type == DYTT_LIVE || type == DM5 || type == JREN || type == HALIHALI || type == IKANFAN || type == TEPIAN || type == KUPIAN ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        type = getIntent().getIntExtra(TYPE, S80);
        title = getIntent().getStringExtra(TITLE);
        super.initActivity(savedState, inflater);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.getBoolean("video_thit", true)) {
            String string = getString(R.string.video_thit);
            DialogUtil.showCancelableDialog(this, string, "继续提醒", "不要再说了", buttonClickListener);
        }
    }


    @Override
    protected PagerAdapter getAdapter(FragmentManager fm) {
        if (type == S80) {
            mPagerAdapter = new S80PagerAdapter(fm);
        } else if (type == BUMIMI) {
            mPagerAdapter = new BumimiPagerAdapter(fm);
        } else if (type == DM5) {
            mPagerAdapter = new Dm5PagerAdapter(fm);
        } else if (type == XIU169) {
            mPagerAdapter = new Xiu169PagerAdapter(fm);
        } else if (type == XIAOKANBA) {
            mPagerAdapter = new XiaokabaPagerAdapter(fm);
        } else if (type == W4K) {
            mPagerAdapter = new KmaoPagerAdapter(fm);
        } else if (type == A4DY) {
            mPagerAdapter = new A4dyPagerAdapter(fm);
        } else if (type == KANKANWU) {
            mPagerAdapter = new KankanPagerAdapter(fm);
        } else if (type == BABAYU) {
            mPagerAdapter = new BabayuPagerAdapter(fm);
        } else if (type == LL520) {
            mPagerAdapter = new LL520PagerAdapter(fm);
        } else if (type == K8DY) {
            mPagerAdapter = new K8dyPagerAdapter(fm);
        } else if (type == IKANFAN) {
            mPagerAdapter = new IKanFanPagerAdapter(fm);
        } else if (type == HALIHALI) {
            mPagerAdapter = new HaliHaliPagerAdapter(fm);
        } else if (type == MMYY) {
            mPagerAdapter = new MmyyPagerAdapter(fm);
        } else if (type == SMDY) {
            mPagerAdapter = new SmdyPagerAdapter(fm);
        } else if (type == AISM) {
            mPagerAdapter = new AismPagerAdapter(fm);
        } else if (type == WANDOU) {
            mPagerAdapter = new WandouPagerAdapter(fm);
        } else if (type == ZHANDI) {
            mPagerAdapter = new ZhandiPagerAdapter(fm);
        } else if (type == SANMAO) {
            mPagerAdapter = new BobmaoPagerAdapter(fm);
        } else if (type == JREN) {
            mPagerAdapter = new JrenPagerAdapter(fm);
        } else if (type == KUPIAN) {
            mPagerAdapter = new KupianPagerAdapter(fm);
        } else if (type == TAIHAN) {
            mPagerAdapter = new TaihanPagerAdapter(fm);
        } else if (type == TEPIAN) {
            mPagerAdapter = new TepianPagerAdapter(fm);
        } else if (type == JUGOU) {
            mPagerAdapter = new JugouPagerAdapter(fm);
        } else if (type == LAOSIJI) {
            mPagerAdapter = new LaosijiPagerAdapter(fm);
        } else if (type == ZZZVZ) {
            mPagerAdapter = new ZzzvzPagerAdapter(fm);
        } else if (type == VIPYS) {
            mPagerAdapter = new VipysPagerAdapter(fm);
        } else if (type == FEIFAN) {
            mPagerAdapter = new WeilaiPagerAdapter(fm);
        } else if (type == DYTT_LIVE) {
            mPagerAdapter = new TvLivePagerAdapter(fm);
        } else if (type == HQ_LIVE) {
            mPagerAdapter = new HaoQuPagerAdapter(fm);
        } else if (type == ICAN_LIVE) {
            mPagerAdapter = new ICanTvPagerAdapter(fm);
        }else if(type == ZZYO){
            mPagerAdapter = new ZzyoPagerAdapter(fm);
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
        Object info = mPagerAdapter.getExtendInfo();
        if(info == null) return;
        String classNmae = info.toString();
        int multiple = mPagerAdapter.getMultiple();
        int pageStart = mPagerAdapter.getPageStart();
        String wordString = word.getWord();
        SearchVideoActivity.startActivity(this, wordString, classNmae, pageStart, multiple);
    }

    private OnButtonClickListener buttonClickListener = new OnButtonClickListener() {

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (mSharedPreferences == null) return;
            if (btn == OnButtonClickListener.RIGHT) {
                mSharedPreferences.edit().putBoolean("video_thit", false).commit();
            }
        }

    };
}
