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
import com.fanchen.imovie.IMovieAppliction;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.DownloadTabActivity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.DownloadAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.DownloadEntityWrap;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.thread.AsyTaskQueue;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.SystemUtil;
import com.fanchen.m3u8.M3u8Config;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * DownloadFragment
 * Created by fanchen on 2017/10/3.
 */
public class DownloadFragment extends BaseRecyclerFragment implements DownloadTabActivity.OnDeleteListernr,
        IMovieAppliction.OnTaskRuningListener, BaseDownloadAdapter.OnDownloadControlListener<DownloadEntityWrap> {

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
        getMTextView().setVisibility(View.VISIBLE);
        getMTextView().setText(String.format("下载路径：%s", M3u8Config.INSTANCE.getM3u8Path()));
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
    protected void setListener() {
        super.setListener();
        mDownloadAdapter.setOnDownloadControlListener(this);
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        AsyTaskQueue.newInstance().execute(taskListener);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && appliction != null) {
            appliction.addRuningListener(this);
        } else if (appliction != null) {
            appliction.removeRuningListener(this);
        }
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof DownloadEntityWrap)) return;
        DownloadEntityWrap entityWrap = (DownloadEntityWrap) datas.get(position);
        if (entityWrap.data.getState() != IEntity.STATE_COMPLETE) return;
        if (!new File(entityWrap.data.getDownloadPath()).exists()) {
            showToast("文件已删除");
        } else {
            String fileName = entityWrap.data.getFileName();
            if (!TextUtils.isEmpty(fileName) && fileName.contains(".apk")) {
                SystemUtil.installApk(activity, entityWrap.data.getDownloadPath());
            } else if (!TextUtils.isEmpty(fileName) && fileName.contains(".mp4")) {
                VideoPlayerActivity.Companion.startActivity(activity, entityWrap.data);
            }
        }
    }

    @Override
    public void setDeleteMode(boolean isDeleteMode) {
        if (mDownloadAdapter != null) mDownloadAdapter.setDeleteMode(isDeleteMode);
    }

    @Override
    public void onTaskUpdate(DownloadTask task) {
        if (mDownloadAdapter != null) mDownloadAdapter.update(task);
    }

    @Override
    public void onTaskCancel(DownloadTask task) {

    }

    @Override
    public void onControl(BaseAdapter adapter, int control, DownloadEntityWrap data) {
        if (control == START) {
            getDownloadReceiver().load(data.data.getUrl()).start();
            data.data.setState(IEntity.STATE_RUNNING);
            adapter.notifyDataSetChanged();
        } else if (control == STOP) {
            getDownloadReceiver().load(data.data.getUrl()).stop();
            data.data.setState(IEntity.STATE_STOP);
            adapter.notifyDataSetChanged();
        } else if (control == DELETE) {
            DeleteClickListener listener = new DeleteClickListener(data.data);
            DialogUtil.showMaterialDialog(activity, getStringFix(R.string.delete_file), listener);
        } else if (control == PLAY) {
            String fileName = data.data.getFileName();
            if (!new File(data.data.getDownloadPath()).exists()) {
                showToast("文件已删除");
            } else if (!TextUtils.isEmpty(fileName) && fileName.contains(".apk")) {
                SystemUtil.installApk(activity, data.data.getDownloadPath());
            } else if (!TextUtils.isEmpty(fileName) && fileName.contains(".mp4")) {
                VideoPlayerActivity.Companion.startActivity(activity, data.data);
            }
        }
    }

    private TaskRecyclerFragmentImpl<List<DownloadEntity>> taskListener = new TaskRecyclerFragmentImpl<List<DownloadEntity>>() {

        @Override
        public List<DownloadEntity> onTaskBackground() {
            if (getDownloadReceiver() == null) return null;
            return getDownloadReceiver().getTaskList();
        }

        @Override
        public void onTaskSuccess(List<DownloadEntity> data) {
            if (mDownloadAdapter == null) return;
            mDownloadAdapter.addAll(data, suffix);
        }

        @Override
        public void onTaskFinish() {
            if (getMSwipeRefreshLayout() == null) return;
            super.onTaskFinish();
            getMSwipeRefreshLayout().setEnabled(false);
        }

        @Override
        public void onTaskProgress(Integer... values) {

        }
    };

    private class DeleteClickListener implements OnButtonClickListener {
        private DownloadEntity entity;

        public DeleteClickListener(DownloadEntity entity) {
            this.entity = entity;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            if (btn == OnButtonClickListener.RIGHT && !TextUtils.isEmpty(entity.getUrl())) {
                activity.getDownloadReceiver().load(entity.getUrl()).cancel(true);
                mDownloadAdapter.remove(entity);
            }
            dialog.dismiss();
        }
    }
}
