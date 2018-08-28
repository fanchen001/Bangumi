package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.DownloadAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.entity.DownloadEntityWrap;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.util.LogUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fanchen on 2017/10/3.
 */
public class DownloadFragment extends BaseRecyclerFragment {

    public static final String SUFFIX = "";

    private DownloadAdapter mDownloadAdapter;
    private String suffix;

    /**
     * @param suffix
     * @return
     */
    public static Fragment newInstance(String suffix) {
        DownloadFragment downloadFragment = new DownloadFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SUFFIX, suffix);
        downloadFragment.setArguments(bundle);
        return downloadFragment;
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        suffix = getArguments().getString(SUFFIX);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(activity, BaseAdapter.LinearLayoutManagerWrapper.VERTICAL, false);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mDownloadAdapter = new DownloadAdapter(activity);
    }

    @Override
    public void loadData(Bundle savedInstanceState,RetrofitManager retrofit, int page) {
        AsyTaskQueue.newInstance().execute(taskListener);
    }

    public void onTaskUpdate(DownloadTask task) {
        if (mDownloadAdapter != null)
            mDownloadAdapter.update(task);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if(!(datas.get(position) instanceof DownloadEntityWrap))return ;
        DownloadEntityWrap entityWrap = (DownloadEntityWrap) datas.get(position);
        DownloadEntity entity = entityWrap.getEntity();
        if(entity.getState() != IEntity.STATE_COMPLETE)return;
        String fileName = entity.getFileName();
        if (!TextUtils.isEmpty(fileName) && fileName.contains(".apk")) {
            SystemUtil.installApk(activity, entity.getDownloadPath());
        } else if (!TextUtils.isEmpty(fileName) && fileName.contains(".mp4")) {
            VideoPlayerActivity.startActivity(activity, entity);
        }
    }

    public void setDeleteMode(boolean isDeleteMode){
        if(mDownloadAdapter != null)
            mDownloadAdapter.setDeleteMode(isDeleteMode);
    }

    private TaskRecyclerFragmentImpl<List<DownloadEntity>> taskListener = new TaskRecyclerFragmentImpl<List<DownloadEntity>>() {

        @Override
        public List<DownloadEntity> onTaskBackground() {
            if (getDownloadReceiver() == null) return null;
            return getDownloadReceiver().getSimpleTaskList();
        }

        @Override
        public void onTaskSuccess(List<DownloadEntity> data) {
            if (mDownloadAdapter == null) return;
            mDownloadAdapter.addAll(data, suffix);
        }

        @Override
        public void onTaskFinish() {
            if(getSwipeRefreshLayout() == null)return;
            super.onTaskFinish();
            getSwipeRefreshLayout().setEnabled(false);
        }
    };

}
