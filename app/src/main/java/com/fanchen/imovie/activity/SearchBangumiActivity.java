package com.fanchen.imovie.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.adapter.BangumiListAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseToolbarActivity;
import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.service.TucaoService;
import com.fanchen.imovie.view.CustomEmptyView;
import com.fanchen.imovie.view.dropdown.DropdownLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.InjectView;

/**
 * 吐槽C 视频搜索页面
 * Created by fanchen on 2017/9/21.
 */
public class SearchBangumiActivity extends BaseToolbarActivity implements DropdownLayout.OnDropdownListListener,
        SwipeRefreshLayout.OnRefreshListener, BaseAdapter.OnLoadListener, BaseAdapter.OnItemClickListener {

    private String[] TIDKEYS = {"全部", "新番", "动画", "音乐", "游戏", "三次元", "影视"};
    private String[] TIDVALUES = {"", "24", "19", "20", "21", "22", "23"};
    private String[] ORDERKEYS = {"发布日期", "弹幕数量", "播放数量"};
    private String[] ORDERVALUES = {"date", "mukio", "views"};

    public static final String WORD = "word";

    @InjectView(R.id.dl_category)
    protected DropdownLayout mDropdownLayout;
    @InjectView(R.id.recycle_list)
    protected RecyclerView mRecyclerView;
    @InjectView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.cev_empty)
    protected CustomEmptyView mCustomEmptyView;

    private int page = 1;
    private String keyWord;
    private String tid = TIDVALUES[0];
    private String order = ORDERVALUES[0];
    private BangumiListAdapter mVideoListAdapter;

    /**
     * @param activity
     * @param word
     */
    public static void startActivity(Activity activity, String word) {
        try {
            Intent intent = new Intent(activity, SearchBangumiActivity.class);
            intent.putExtra(WORD, word);
            activity.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        keyWord = getIntent().getStringExtra(WORD);
        super.initActivity(savedState, inflater);
        mDropdownLayout.setCols(2);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        mSwipeRefreshLayout.setColorSchemeColors(typedValue.data);
        mVideoListAdapter = new BangumiListAdapter(this, getPicasso());
        mRecyclerView.setLayoutManager(new BaseAdapter.LinearLayoutManagerWrapper(this, BaseAdapter.LinearLayoutManagerWrapper.VERTICAL, false));
        mRecyclerView.setAdapter(mVideoListAdapter);
        mDropdownLayout.setDropdownList(new String[][]{TIDKEYS, ORDERKEYS}, new String[][]{TIDVALUES, ORDERVALUES});
        //加载数据
        loadNet(page = 1);
    }

    @Override
    protected String getActivityTitle() {
        return String.format(getString(R.string.search_mart), keyWord);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_search_list;
    }

    @Override
    protected void setListener() {
        super.setListener();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mDropdownLayout.setOnDropdownListListener(this);
        mVideoListAdapter.setOnLoadListener(this);
        mVideoListAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideo)) return;
        IVideo video = (IVideo) datas.get(position);
        VideoDetailsActivity.startActivity(this, video);
    }

    @Override
    public void onRefresh() {
        loadNet(page = 1);
    }

    @Override
    public void onLoad() {
        loadNet(++page);
    }

    /**
     * @param pager
     */
    private void loadNet(int pager) {
        Integer integer = Integer.valueOf(pager);
        if (TextUtils.isEmpty(tid)) {
            //全部
            getRetrofitManager().enqueue(TucaoService.class, callback, "search", keyWord, integer, order);
        } else {
            //分区搜索
            getRetrofitManager().enqueue(TucaoService.class, callback, "search", keyWord, integer, tid, order);
        }
    }

    @Override
    public void OnDropdownListSelected(int indexOfButton, int indexOfList, String textOfList, String valueOfList) {
        switch (indexOfButton) {
            case 0:
                tid = valueOfList;
                break;
            case 1:
                order = valueOfList;
                break;
        }
        loadNet(page = 1);
    }

    @Override
    public void onDropdownListOpen() {
    }

    @Override
    public void onDropdownListClosed() {
    }

    private RefreshCallback<IBangumiMoreRoot> callback = new RefreshCallback<IBangumiMoreRoot>() {

        @Override
        public void onSuccess(int enqueueKey, IBangumiMoreRoot response) {
            if (response == null || !response.isSuccess() || mVideoListAdapter == null) return;
            List<? extends IVideo> list = response.getList();
            if (list == null || list.size() == 0) {
                showSnackbar(getString(R.string.not_more));
                mVideoListAdapter.setLoad(false);
            } else {
                mVideoListAdapter.setList(list,page == 1);
                mVideoListAdapter.setLoad(list.size() >= 10);
            }
        }

        @Override
        public void onStart(int enqueueKey) {
            if (mCustomEmptyView == null || mSwipeRefreshLayout == null || mVideoListAdapter == null)return;
            mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_NON);
            if (!mSwipeRefreshLayout.isRefreshing() && !mVideoListAdapter.isLoading()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (mCustomEmptyView == null || mVideoListAdapter == null) return;
            if (mVideoListAdapter.isEmpty())
                mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_ERROR);
            showSnackbar(throwable);
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (mCustomEmptyView == null || mSwipeRefreshLayout == null || mVideoListAdapter == null) return;
            mSwipeRefreshLayout.setRefreshing(false);
            mVideoListAdapter.setLoading(false);
            if (mVideoListAdapter.getList().size() == 0) {
                mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
            }
        }

    };

}
