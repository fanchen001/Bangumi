package com.fanchen.imovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.ApkListAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerActivity;
import com.fanchen.imovie.dialog.fragment.SearchDialogFragment;
import com.fanchen.imovie.entity.face.ISearchWord;
import com.fanchen.imovie.entity.apk.ApkData;
import com.fanchen.imovie.entity.apk.ApkItem;
import com.fanchen.imovie.entity.apk.ApkParamData;
import com.fanchen.imovie.entity.apk.ApkParamUser;
import com.fanchen.imovie.entity.apk.ApkRoot;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.MoeapkService;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 应用、游戏列表
 * Created by fanchen on 2017/8/22.
 */
public class ApkListActivity extends BaseRecyclerActivity implements SearchDialogFragment.OnSearchClickListener {

    public static final String APKLIST_TYPE = "type";
    public static final String SEARCH_WORD = "word";
    public static final String HAS_LOAD = "load";
    public static final int TYPE_APK = 0;
    public static final int TYPE_GAME = 1;
    public static final int TYPE_SEARCH = 2;

    private Gson gson = new Gson();
    private int activityType = TYPE_APK;
    private String word;
    private ApkListAdapter mApkListAdapter;
    private SearchDialogFragment mDialogFragment = SearchDialogFragment.newInstance();

    /**
     * @param context
     * @param type
     */
    public static void startActivity(Context context, int type) {
        try {
            Intent intent = new Intent(context, ApkListActivity.class);
            intent.putExtra(ApkListActivity.APKLIST_TYPE, type);
            intent.putExtra(ApkListActivity.HAS_LOAD, true);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param word
     */
    public static void startActivity(Context context, String word) {
        try {
            Intent intent = new Intent(context, ApkListActivity.class);
            intent.putExtra(ApkListActivity.APKLIST_TYPE, ApkListActivity.TYPE_SEARCH);
            intent.putExtra(ApkListActivity.SEARCH_WORD, word);
            intent.putExtra(ApkListActivity.HAS_LOAD, false);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mDialogFragment.setOnSearchClickListener(this);
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        activityType = getIntent().getIntExtra(APKLIST_TYPE, TYPE_APK);
        word = getIntent().getStringExtra(SEARCH_WORD);
        super.initActivity(savedState, inflater);
    }

    @Override
    protected boolean hasLoad() {
        return getIntent().getBooleanExtra(HAS_LOAD, true);
    }

    @Override
    protected String getActivityTitle() {
        if (activityType == TYPE_APK) {
            return getString(R.string.apk);
        } else if (activityType == TYPE_GAME) {
            return getString(R.string.game);
        } else if (activityType == TYPE_SEARCH) {
            return getString(R.string.search_mart, word);
        }
        return null;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(this);
    }

    @Override
    protected BaseAdapter getAdapter(Picasso picasso) {
        return mApkListAdapter = new ApkListAdapter(this, picasso, getIntent().getBooleanExtra(HAS_LOAD, true));
    }

    @Override
    protected void loadData(RetrofitManager retrofit, int page) {
        String mothed = "";
        String data = "";
        String user = gson.toJson(new ApkParamUser());
        if (activityType == TYPE_APK) {
            mothed = "appList";
            data = gson.toJson(new ApkParamData(page));
        } else if (activityType == TYPE_GAME) {
            mothed = "gameList";
            data = gson.toJson(new ApkParamData(page));
        } else if (activityType == TYPE_SEARCH) {
            mothed = "search";
            String word = getIntent().getStringExtra(SEARCH_WORD);
            data = gson.toJson(new ApkParamData(page, word));
        }
        String format = String.format("data=%s&user=%s", URLEncoder.encode(data), URLEncoder.encode(user));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), format);
        retrofit.enqueue(MoeapkService.class, callback, mothed, requestBody);
    }

    @Override
    public void onSearchClick(ISearchWord word) {
        ApkListActivity.startActivity(this, word.getWord());
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof ApkItem)) return;
        ApkItem item = (ApkItem) datas.get(position);
        ApkDetailsActivity.startActivity(this, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (activityType != TYPE_SEARCH)
            getMenuInflater().inflate(R.menu.menu_search_down, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (activityType != TYPE_SEARCH && item.getItemId() == R.id.action_search && mDialogFragment != null) {
            mDialogFragment.show(this);
            return true;
        } else if (R.id.action_download == item.getItemId()) {
            DownloadTabActivity.startActivity(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private RefreshRecyclerActivityImpl<ApkRoot<ApkData<ApkItem>>> callback = new RefreshRecyclerActivityImpl<ApkRoot<ApkData<ApkItem>>>() {

        @Override
        public void onSuccess(int enqueueKey, ApkRoot<ApkData<ApkItem>> response) {
            if (response == null || response.getData() == null || mApkListAdapter == null)return;
            //第一次加载或者是刷新
            mApkListAdapter.setList(response.getData().getList(),isRefresh());
        }

    };
}
