package com.fanchen.imovie.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.thread.task.AsyTaskListener;
import com.fanchen.imovie.view.CustomEmptyView;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;

/**
 * layout为R.layout.activity_recyclerview
 * 以RecyclerView为显示根布局的activity继承该类
 * Created by fanchen on 2017/8/22.
 */
public abstract class BaseRecyclerActivity extends BaseToolbarActivity implements
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, BaseAdapter.OnItemClickListener, BaseAdapter.OnLoadListener {

    @InjectView(R.id.recycle_list)
    protected RecyclerView mRecyclerView;
    @InjectView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.cev_empty)
    protected CustomEmptyView mCustomEmptyView;

    private int page = 1;
    private int pageStart = 1;
    private BaseAdapter mAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_recyclerview;
    }

    @Override
    protected void initActivity(Bundle savedState, LayoutInflater inflater) {
        super.initActivity(savedState, inflater);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        if (!checkFieldNull()) {
            mSwipeRefreshLayout.setColorSchemeColors(typedValue.data);
            mRecyclerView.setLayoutManager(getLayoutManager());
            mRecyclerView.setAdapter(mAdapter = getAdapter(getPicasso()));
        }
        loadData(getRetrofitManager(), page = pageStart);
    }

    @Override
    protected void setListener() {
        super.setListener();
        if (!checkFieldNull()) {
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mCustomEmptyView.setOnClickListener(this);
            mRecyclerView.addOnScrollListener(scrollListener);
        }
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(this);
            if (hasLoad()) mAdapter.setOnLoadListener(this);
        }
    }

    public boolean checkFieldNull() {
        return mSwipeRefreshLayout == null || mCustomEmptyView == null || mRecyclerView == null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_top_back:
                finish();
                break;
            case R.id.cev_empty:
                onRefresh();
                break;
        }
    }

    @Override
    public void onRefresh() {
        loadData(getRetrofitManager(), page = pageStart);
    }

    @Override
    public void onLoad() {
        loadData(getRetrofitManager(), ++page);
    }

    public int getPage() {
        return page;
    }

    public void setPageStart(int page) {
        this.pageStart = page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return
     */
    protected boolean isRefresh() {
        return page <= pageStart;
    }

    /**
     * @return
     */
    protected boolean hasLoad() {
        return false;
    }

    /**
     * @return
     */
    protected abstract RecyclerView.LayoutManager getLayoutManager();

    /**
     * @return
     */
    protected abstract BaseAdapter getAdapter(Picasso picasso);

    /**
     * @param page
     */
    protected abstract void loadData(RetrofitManager retrofit, int page);

    /**
     *
     */
    protected RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Picasso picasso = getPicasso();
            if (picasso != null) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    picasso.resumeTag(BaseRecyclerActivity.class);
                } else {
                    picasso.pauseTag(BaseRecyclerActivity.class);
                }
            }
        }

    };

    /**
     * @param <T>
     */
    protected abstract class RefreshRecyclerActivityImpl<T> implements RefreshCallback<T> {

        @Override
        public void onStart(int enqueueKey) {
            if (mCustomEmptyView == null || mSwipeRefreshLayout == null || mAdapter == null) return;
            mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_NON);
            if (!mSwipeRefreshLayout.isRefreshing() && !mAdapter.isLoading()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (mCustomEmptyView == null || mAdapter == null) return;
            if (mAdapter.isEmpty()) mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_ERROR);
            showSnackbar(throwable);
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (mCustomEmptyView == null || mSwipeRefreshLayout == null || mAdapter == null) return;
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.setLoading(false);
            if (mAdapter.isEmpty()) {
                mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
            }
        }
    }

    protected abstract class TaskRecyclerActivityImpl<T> implements AsyTaskListener<T> {

        @Override
        public void onTaskFinish() {
            if (mCustomEmptyView == null || mSwipeRefreshLayout == null || mAdapter == null) return;
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.setLoading(false);
            if (mAdapter.isEmpty()) {
                mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
            }
        }

        @Override
        public void onTaskSart() {
            if (mCustomEmptyView == null || mSwipeRefreshLayout == null) return;
            mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_NON);
            if (!mSwipeRefreshLayout.isRefreshing() && !mAdapter.isLoading()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public void onTaskProgress(Integer... values) {

        }

    }
}
