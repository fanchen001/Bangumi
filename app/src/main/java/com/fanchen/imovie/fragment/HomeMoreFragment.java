package com.fanchen.imovie.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.AcgTabActivity;
import com.fanchen.imovie.activity.ApkEvaluatActivity;
import com.fanchen.imovie.activity.ApkListActivity;
import com.fanchen.imovie.activity.CaptureActivity;
import com.fanchen.imovie.activity.HackerToolActivity;
import com.fanchen.imovie.base.BaseFragment;
import com.fanchen.imovie.entity.face.ISearchWord;
import com.fanchen.imovie.entity.JsonSerialize;
import com.fanchen.imovie.entity.bili.BilibiliIndex;
import com.fanchen.imovie.entity.bili.BilibiliWord;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RefreshCallback;
import com.fanchen.imovie.retrofit.service.BilibiliService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.thread.task.AsyTaskListenerImpl;
import com.fanchen.imovie.util.DisplayUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.view.FlowLayout;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * 更多
 * Created by fanchen on 2017/2/25.
 */
public class HomeMoreFragment extends BaseFragment implements View.OnClickListener,
        FlowLayout.OnFlowItemClick, SwipeRefreshLayout.OnRefreshListener {
    public static final int DIP_96 = 96;
    public static final int DIP_192 = 192;
    public static final String INDEX = "index";

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.search_bar)
    protected TextView mSearchView;
    @InjectView(R.id.qr_scan)
    protected ImageButton mScanView;
    @InjectView(R.id.flowlayout_work)
    protected FlowLayout mHotFlowLayout;
    @InjectView(R.id.nsv_hotword)
    protected NestedScrollView mNestedScrollView;
    @InjectView(R.id.ll_more_hotword)
    protected LinearLayout mMoreHotView;
    @InjectView(R.id.ll_game_pc)
    protected LinearLayout mPcView;
    @InjectView(R.id.ll_acg_tree)
    protected LinearLayout mAcgView;
    @InjectView(R.id.ll_more_apk)
    protected LinearLayout mApkView;
    @InjectView(R.id.ll_more_game)
    protected LinearLayout mGameView;
    @InjectView(R.id.ll_hack)
    protected LinearLayout mHackView;
    @InjectView(R.id.tv_word_error)
    protected TextView mErrorTextView;

    private BilibiliIndex mSaveWordIndex;
    private String serializeKey;

    public static HomeMoreFragment newInstance() {
        return new HomeMoreFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_more;
    }

    @Override
    protected void findView(View v) {
        super.findView(v);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        serializeKey = getClass().getSimpleName();
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        mSwipeRefreshLayout.setColorSchemeColors(typedValue.data);
        if (savedInstanceState != null && (mSaveWordIndex = savedInstanceState.getParcelable(INDEX)) != null) {
            if (mSaveWordIndex.getData() != null && mSaveWordIndex.getData().getList() != null) {
                mHotFlowLayout.removeAllViews();
                mHotFlowLayout.addDataList2TextView(getListData(mSaveWordIndex));
            }
        } else {
            AsyTaskQueue.newInstance().execute(new QueryTaskListener(getRetrofitManager()));
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        mHackView.setOnClickListener(this);
        mGameView.setOnClickListener(this);
        mApkView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
        mMoreHotView.setOnClickListener(this);
        mScanView.setOnClickListener(this);
        mHotFlowLayout.setOnFlowItemClick(this);
        mPcView.setOnClickListener(this);
        mAcgView.setOnClickListener(this);
        mErrorTextView.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_word_error:
                onRefresh();
                break;
            case R.id.ll_more_apk:
                ApkListActivity.startActivity(activity, ApkListActivity.TYPE_APK);
                break;
            case R.id.ll_more_game:
                ApkListActivity.startActivity(activity, ApkListActivity.TYPE_GAME);
                break;
            case R.id.ll_acg_tree:
                AcgTabActivity.startActivity(activity);
                break;
            case R.id.ll_game_pc:
                ApkEvaluatActivity.startActivity(activity);
                break;
            case R.id.search_bar:
                Fragment parentFragment = getParentFragment();
                if (parentFragment != null && parentFragment instanceof HomePagerFragment) {
                    ((HomePagerFragment) parentFragment).openSearchDialog();
                }
                break;
            case R.id.ll_hack:
                HackerToolActivity.startActivity(activity);
                break;
            case R.id.qr_scan:
                CaptureActivity.startActivity(activity);
                break;
            case R.id.ll_more_hotword:
                ViewGroup.LayoutParams layoutParams = mNestedScrollView.getLayoutParams();
                if (layoutParams.height == DisplayUtil.dip2px(activity, DIP_192)) {
                    layoutParams.height = DisplayUtil.dip2px(activity, DIP_96);
                    ((TextView) mMoreHotView.getChildAt(1)).setText("  查看更多");
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_down_gray_round);
                    ((TextView) mMoreHotView.getChildAt(1)).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                } else {
                    layoutParams.height = DisplayUtil.dip2px(activity, DIP_192);
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_up_gray_round);
                    ((TextView) mMoreHotView.getChildAt(1)).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    ((TextView) mMoreHotView.getChildAt(1)).setText("  收起");
                }
                mNestedScrollView.setLayoutParams(layoutParams);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSaveWordIndex != null) {
            outState.putParcelable(INDEX, mSaveWordIndex);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        loadDate(getRetrofitManager());
    }

    @Override
    public <T> void OnItemClick(View v, final T data, int position) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof HomePagerFragment) {
            ((HomePagerFragment) parentFragment).onSearchClick(new ISearchWord() {

                @Override
                public int getViewType() {
                    return TYPE_NORMAL;
                }

                @Override
                public int getType() {
                    return ISearchWord.TYPE_WORD;
                }

                @Override
                public String getWord() {
                    return data.toString();
                }

            });
        }
    }

    public List<String> getListData(BilibiliIndex result) {
        List<String> all = new ArrayList<>();
        if (result == null || result.getData() == null || result.getData().getList() == null)
            return all;
        for (BilibiliWord s : result.getData().getList()) {
            all.add(s.getKeyword());
        }
        return all;
    }

    /**
     *
     */
    private void loadDate(RetrofitManager retrofitManager) {
        retrofitManager.enqueue(BilibiliService.class, callback, "loadHotword", String.valueOf(System.currentTimeMillis()));
    }

    private RefreshCallback<BilibiliIndex> callback = new RefreshCallback<BilibiliIndex>() {

        @Override
        public void onStart(int enqueueKey) {
            if (mSwipeRefreshLayout == null || mErrorTextView == null) return;
            mErrorTextView.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onFinish(int enqueueKey) {
            if (mSwipeRefreshLayout == null) return;
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(int enqueueKey, String throwable) {
            if (mErrorTextView == null) return;
            mErrorTextView.setVisibility(View.VISIBLE);
            showSnackbar(throwable);
        }

        @Override
        public void onSuccess(int enqueueKey, BilibiliIndex response) {
            if (mHotFlowLayout == null || response == null || isDetached()) return;
            mSaveWordIndex = response;
            mHotFlowLayout.removeAllViews();
            mHotFlowLayout.addDataList2TextView(getListData(response));
            AsyTaskQueue.newInstance().execute(new SaveTaskListener(response));
        }

    };

    private class QueryTaskListener extends AsyTaskListenerImpl<BilibiliIndex> {

        private RetrofitManager retrofit;

        public QueryTaskListener(RetrofitManager retrofit) {
            this.retrofit = retrofit;
        }

        @Override
        public void onTaskSart() {
            if (mSwipeRefreshLayout == null) return;
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public BilibiliIndex onTaskBackground() {
            if (getLiteOrm() == null) return null;
            List<JsonSerialize> query = getLiteOrm().query(new QueryBuilder<>(JsonSerialize.class).where("key = ?", serializeKey));
            if (query != null && query.size() > 0) {
                JsonSerialize jsonSerialize = query.get(0);
                if (!jsonSerialize.isStale()) {
                    //数据未过期
                    return new Gson().fromJson(jsonSerialize.getJson(), BilibiliIndex.class);
                }
            }
            return null;
        }

        @Override
        public void onTaskSuccess(BilibiliIndex data) {
            if (mHotFlowLayout == null || mSwipeRefreshLayout == null) return;
            mSaveWordIndex = data;
            if (data != null) {
                //加载本地数据
                LogUtil.d(HomeMoreFragment.class, "加载本地数据");
                mHotFlowLayout.addDataList2TextView(getListData(data));
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                //没有缓存数据或者数据已经过期，加载网络数据
                LogUtil.d(HomeMoreFragment.class, "加载网络数据");
                loadDate(retrofit);
            }
        }


    }

    ;

    private class SaveTaskListener extends AsyTaskListenerImpl<Void> {

        private BilibiliIndex response;

        public SaveTaskListener(BilibiliIndex response) {
            this.response = response;
        }

        @Override
        public Void onTaskBackground() {
            if (getLiteOrm() == null || response == null) return null;
            //保存key
            getLiteOrm().delete(new WhereBuilder(JsonSerialize.class, "key = ?", new Object[]{serializeKey}));
            getLiteOrm().insert(new JsonSerialize(response, serializeKey));
            return null;
        }

    }

}
