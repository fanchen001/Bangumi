package com.fanchen.imovie.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.db.LiteOrmManager;
import com.fanchen.imovie.entity.JsonSerialize;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.DianxiumeiService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListener;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.NetworkUtil;
import com.fanchen.imovie.view.CustomEmptyView;
import com.google.gson.Gson;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.InjectView;

/**
 *
 * Created by fanchen on 2017/7/22.
 */
public abstract class BaseRecyclerFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, BaseAdapter.OnItemClickListener, BaseAdapter.OnLoadListener {

    @InjectView(R.id.recycle)
    protected RecyclerView mRecyclerView;
    @InjectView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.empty_layout)
    protected CustomEmptyView mCustomEmptyView;
    @InjectView(R.id.tv_recycler_bottom)
    protected TextView mTextView;

    private int page = 1;
    private int pageStart = 1;
    private BaseAdapter mAdapter;
    private Bundle savedInstanceState;

    @Override
    protected int getLayout() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        this.savedInstanceState = savedInstanceState;
        setHasOptionsMenu(true);
        TypedValue typedValue = new TypedValue();
        if(activity != null) activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        if (!checkFieldNull()) {
            mSwipeRefreshLayout.setColorSchemeColors(typedValue.data);
            mSwipeRefreshLayout.setEnabled(hasRefresh());
            mRecyclerView.setLayoutManager(getLayoutManager());
            mRecyclerView.setAdapter(mAdapter = getAdapter(getPicasso()));
        }
        if (useLocalStorage() && savedInstanceState == null) {
            LogUtil.e(BaseRecyclerFragment.class, "尝试加载本地数据");
            loadLocalData(AsyTaskQueue.newInstance());
        } else {
            LogUtil.e(BaseRecyclerFragment.class, "加载网络数据");
            loadData(savedInstanceState, getRetrofitManager(), page = pageStart);
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        if (!checkFieldNull()) {
            if (hasRefresh())
                mSwipeRefreshLayout.setOnRefreshListener(this);
            mCustomEmptyView.setOnClickListener(this);
            mRecyclerView.addOnScrollListener(scrollListener);
        }
        if (mAdapter != null) {
            if (hasLoad())
                mAdapter.setOnLoadListener(this);
            mAdapter.setOnItemClickListener(this);
        }
    }

    public boolean checkFieldNull() {
        return mSwipeRefreshLayout == null || mCustomEmptyView == null || mRecyclerView == null;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    /**
     * 能否刷新
     *
     * @return
     */
    protected boolean hasRefresh() {
        return true;
    }

    /**
     * RecyclerView  LayoutManager
     *
     * @return
     */
    public abstract RecyclerView.LayoutManager getLayoutManager();

    /**
     * RecyclerView Adapter
     *
     * @return
     */
    public abstract BaseAdapter getAdapter(Picasso picasso);

    /**
     * 加载网络数据
     *
     * @param page
     */
    public abstract void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page);

    /**
     * 加载本地缓存的数据
     */
    public void loadLocalData(AsyTaskQueue queue) {

    }

    /**
     * 能否加载更多
     *
     * @return
     */
    protected boolean hasLoad() {
        return true;
    }

    /**
     * 当前页数
     *
     * @return
     */
    public int getPage() {
        return page;
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    /**
     * @return
     */
    public boolean isRefresh() {
        return page == pageStart;
    }

    @Override
    public void onRefresh() {
        loadData(null, getRetrofitManager(), page = pageStart);
    }

    @Override
    public void onLoad() {
        loadData(null, getRetrofitManager(), ++page);
    }

    /**
     * 是否使用本地存储
     *
     * @return
     */
    protected boolean useLocalStorage() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_layout:
                onRefresh();
                break;
        }
    }

    /**
     * @return
     */
    public Type getSerializeClass() {
        return null;
    }

    public int getStaleTime() {
        return 12;
    }

    /**
     * @return
     */
    public String getSerializeKey() {
        return getClass().getSimpleName();
    }

    /**
     *
     */
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            Picasso picasso = getPicasso();
            if (picasso != null) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    picasso.resumeTag(BaseRecyclerFragment.class);
                } else {
                    picasso.pauseTag(BaseRecyclerFragment.class);
                }
            }
        }

    };


    /**
     *
     */
    private class SaveTaskListener extends AsyTaskListenerImpl<Void> {

        private Object response;

        public SaveTaskListener(Object response) {
            this.response = response;
        }

        @Override
        public Void onTaskBackground() {
            if (getLiteOrm() == null) return null;
            //保存key
            LogUtil.e("SaveTaskListener","onTaskBackground key -> " + getSerializeKey());
            getLiteOrm().delete(new WhereBuilder(JsonSerialize.class, "key = ?", new Object[]{getSerializeKey()}));
            getLiteOrm().insert(new JsonSerialize(response, getSerializeKey()));
            return null;
        }

    }

    /**
     * @param <T>
     */
    public abstract class QueryTaskListener<T> extends AsyTaskListenerImpl<T> {

        @Override
        public void onTaskSart() {
            if (mCustomEmptyView == null || mSwipeRefreshLayout == null || mAdapter == null) return;
            mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_NON);
            if (!mSwipeRefreshLayout.isRefreshing() && !mAdapter.isLoading()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public T onTaskBackground() {
            if (getLiteOrm() == null) return null;
            List<JsonSerialize> query = getLiteOrm().query(new QueryBuilder<>(JsonSerialize.class).where("key = ?", getSerializeKey()));
            if (query != null && query.size() > 0) {
                JsonSerialize jsonSerialize = query.get(0);
                //数据未过期 或者当前无网络情况下，返回缓存的数据
                if (!jsonSerialize.isStale(getStaleTime()) || !NetworkUtil.isNetWorkAvailable(activity)) {
                    try {
                        Type serializeClass = getSerializeClass();
                        if (serializeClass != null) {
                            return (T) new Gson().fromJson(jsonSerialize.getJson(), serializeClass);
                        } else if (jsonSerialize.isRawType()) {
                            Class<?> forName = Class.forName(jsonSerialize.getClazz());
                            return (T) new Gson().fromJson(jsonSerialize.getJson(), forName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        public void onTaskSuccess(T data) {
            if (mSwipeRefreshLayout == null) return;
            if (data != null) {
                onSuccess(data);
                mSwipeRefreshLayout.setRefreshing(false);
                LogUtil.e(BaseRecyclerFragment.class, "加载本地数据");
            } else {
                //加载网络数据
                LogUtil.e(BaseRecyclerFragment.class, "加载网络数据");
                loadData(savedInstanceState, getRetrofitManager(), page = pageStart);
            }
        }

        /**
         * @param date
         */
        public abstract void onSuccess(T date);
    }


    /**
     * @param <T>
     */
    protected abstract class RefreshRecyclerFragmentImpl<T> implements RefreshCallback<T> {

        @Override
        public void onStart(int enqueueKey) {
            if (mCustomEmptyView == null || mSwipeRefreshLayout == null) return;
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
            if (mCustomEmptyView == null || mAdapter == null) return;
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.setLoading(false);
            if (mAdapter.isEmpty()) {
                mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
            }
        }

        @Override
        public void onSuccess(int enqueueKey, T response) {
            if (response == null || mAdapter == null) return;
            if (useLocalStorage()) {
                //将数据序列化到本地
                LogUtil.e(BaseRecyclerFragment.class, "序列化数据到本地" + new Gson().toJson(response));
                AsyTaskQueue.newInstance().execute(new SaveTaskListener(response));
            }
            onSuccess(response,isRefresh());
        }

        /**
         * @param response
         */
        public abstract void onSuccess(T response,boolean refresh);
    }

    protected abstract class TaskRecyclerFragmentImpl<T> implements AsyTaskListener<T> {

        @Override
        public void onTaskFinish() {
            if (isDetached() || !isAdded()) return;
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.setLoading(false);
            if (mAdapter.isEmpty()) {
                mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
            }
        }

        @Override
        public void onTaskSart() {
            if (isDetached() || !isAdded()) return;
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
