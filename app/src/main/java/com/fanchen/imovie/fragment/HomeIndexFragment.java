package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.adapter.HomeIndexAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.face.IBangumiRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.service.TucaoService;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.view.pager.LoopViewPager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 推荐
 * Created by fanchen on 2017/2/25.
 */
public class HomeIndexFragment extends BaseRecyclerFragment implements BaseAdapter.OnItemLongClickListener {
    private static final String[] DIALOG_TITLE = new String[]{"打开详情", "加入收藏"};
    public static final String PATH = "path";
    public static final String LOAD = "load";
    public static final String CATEGORY = "category";
    public static final String INSTANCESTATE = "state";

    private HomeIndexAdapter mRecomAdapter;
    private IBangumiRoot mIBangumiRoot;
    private String path;
    //序列化key
    private String serializeKey;
    private boolean isCategory;
    private boolean isLoad;

    /**
     * @param path
     * @param isCategory
     * @return
     */
    public static Fragment newInstance(String path, boolean isCategory,boolean isLoad) {
        Fragment fragment = new HomeIndexFragment();
        Bundle args = new Bundle();
        args.putString(PATH, path);
        args.putBoolean(LOAD, isLoad);
        args.putBoolean(CATEGORY, isCategory);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(String path) {
        return newInstance(path, false,false);
    }

    @Override
    protected boolean hasLoad() {
        return false;
    }

    @Override
    protected void setListener() {
        super.setListener();
        if (mRecomAdapter != null) {
            mRecomAdapter.setOnItemLongClickListener(this);
        }
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        path = getArguments().getString(PATH);
        isLoad = getArguments().getBoolean(LOAD);
        isCategory = getArguments().getBoolean(CATEGORY);
        serializeKey = getClass().getSimpleName() + "_" + path;
        super.initFragment(savedInstanceState, args);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mRecomAdapter != null) {
            LoopViewPager loopViewPager = mRecomAdapter.getLoopViewPager();
            if (loopViewPager != null && isVisibleToUser && !loopViewPager.isLoop() && loopViewPager.hasLoop()) {
                //用户可见的时候开启滚屏循环
                loopViewPager.startLoop();
            } else if (loopViewPager != null && !isVisibleToUser) {
                //不可见关闭
                loopViewPager.stopLoop();
            }
        }
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.GridLayoutManagerWrapper(activity, 2);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mRecomAdapter = new HomeIndexAdapter(activity, picasso, isCategory,isLoad);
    }

    @Override
    public void loadData(Bundle savedInstanceState,RetrofitManager retrofit, int page) {
        if(savedInstanceState != null && ( mIBangumiRoot = savedInstanceState.getParcelable(INSTANCESTATE)) != null){
            mRecomAdapter.addData(mIBangumiRoot,true);
        }else{
            retrofit.enqueue(TucaoService.class, callback, "home", path);
        }
    }

    @Override
    public void loadLocalData(AsyTaskQueue queue) {
        queue.execute(taskListener);
    }

    @Override
    protected boolean useLocalStorage() {
        return true;
    }

    @Override
    public String getSerializeKey() {
        return serializeKey;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mIBangumiRoot != null){
            outState.putParcelable(INSTANCESTATE,mIBangumiRoot);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideo)) return;
        IVideo video = (IVideo) datas.get(position);
        LogUtil.e("onItemClick","====>" + new Gson().toJson(video));
        VideoDetailsActivity.startActivity(activity,video);
    }

    @Override
    public boolean onItemLongClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof IVideo)) return false;
        IVideo item = (IVideo) datas.get(position);
        DialogUtil.showOperationDialog(this,item,(List<IVideo>)datas,position);
        return true;
    }

    private QueryTaskListener<IBangumiRoot> taskListener = new QueryTaskListener<IBangumiRoot>() {

        @Override
        public void onSuccess(IBangumiRoot date) {
            if (!date.isSuccess() || mRecomAdapter == null) return;
            mRecomAdapter.addData(mIBangumiRoot = date,true);
        }

    };

    private RefreshRecyclerFragmentImpl<IBangumiRoot> callback = new RefreshRecyclerFragmentImpl<IBangumiRoot>() {

        @Override
        public void onSuccess(IBangumiRoot response,boolean refresh) {
            if (!response.isSuccess() || mRecomAdapter == null) return;
            mRecomAdapter.addData(mIBangumiRoot = response,refresh);
        }

    };

}
