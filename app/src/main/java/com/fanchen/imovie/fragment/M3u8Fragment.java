package com.fanchen.imovie.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.DownloadTabActivity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.M3u8Adapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.M3u8Warp;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.view.CustomEmptyView;
import com.fanchen.m3u8.M3u8Config;
import com.fanchen.m3u8.M3u8Manager;
import com.fanchen.m3u8.bean.M3u8;
import com.fanchen.m3u8.bean.M3u8File;
import com.fanchen.m3u8.bean.M3u8State;
import com.fanchen.m3u8.bean.M3u8Ts;
import com.fanchen.m3u8.listener.OnM3u8DeleteListener;
import com.fanchen.m3u8.listener.OnM3u8DownloadListenr;
import com.fanchen.m3u8.listener.OnM3u8FileListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * M3u8Fragment
 * Created by fanchen on 2018/9/17.
 */
public class M3u8Fragment extends BaseRecyclerFragment implements OnM3u8DownloadListenr, DownloadTabActivity.OnDeleteListernr,
        OnM3u8FileListener, BaseDownloadAdapter.OnDownloadControlListener<M3u8Warp>, OnM3u8DeleteListener {

    private long timeMillis = System.currentTimeMillis();
    private M3u8Adapter mAdapter;

    public static Fragment newInstance() {
        return new M3u8Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        M3u8Manager.INSTANCE.registerDownloadListeners(this);
        M3u8Manager.INSTANCE.registerM3u8Listeners(this);
        M3u8Manager.INSTANCE.registerDeleteListeners(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        M3u8Manager.INSTANCE.unregisterDownloadListeners(this);
        M3u8Manager.INSTANCE.unregisterM3u8Listeners(this);
        M3u8Manager.INSTANCE.unregisterDeleteListeners(this);
        super.onDestroyView();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(activity);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mAdapter = new M3u8Adapter(activity);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mAdapter.setOnDownloadControlListener(this);
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        if (mTextView == null) return;
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText(String.format("下载路径：%s", M3u8Config.INSTANCE.getM3u8Path()));
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        M3u8Manager.INSTANCE.queryAllASync();
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof M3u8Warp)) return;
        M3u8Warp warp = (M3u8Warp) datas.get(position);
        if (warp.data.getState() != M3u8State.INSTANCE.getSTETE_SUCCESS()) return;
        playM3u8File(warp.data);
    }

    @Override
    public void onStarPre(M3u8File m3u8File) {
        if (mAdapter == null) return;
        mAdapter.updataItem(m3u8File, M3u8State.INSTANCE.getSTETE_NON(), "准备下载");
    }

    @Override
    public void onStart(M3u8 m3u8) {
        if (mAdapter == null) return;
        mAdapter.updataItem(m3u8, M3u8State.INSTANCE.getSTETE_DOWNLOAD(), "开始下载");
    }

    @Override
    public void onStart(LinkedList<M3u8File> linkedList) {
        if (mAdapter == null) return;
        mAdapter.updataItem(linkedList, M3u8State.INSTANCE.getSTETE_NON(), "准备下载");
    }

    @Override
    public void onError(M3u8 m3u8, M3u8Ts m3u8Ts, Throwable throwable) {
        if (mAdapter == null) return;
        throwable.printStackTrace();
        mAdapter.updataItem(m3u8, M3u8State.INSTANCE.getSTETE_ERROR(), String.format("下载失败:%s", throwable.toString()));
    }

    @Override
    public void onStopPre(M3u8File m3u8File) {
        if (mAdapter == null) return;
        mAdapter.updataItem(m3u8File, "正在暂停");
    }

    @Override
    public void onStop(M3u8 m3u8) {
        if (mAdapter == null) return;
        mAdapter.updataItem(m3u8, M3u8State.INSTANCE.getSTETE_STOP(), "暂停成功");
    }

    @Override
    public void onStop(LinkedList<M3u8File> linkedList) {
        if (mAdapter == null) return;
        mAdapter.updataItem(linkedList, M3u8State.INSTANCE.getSTETE_STOP(), "暂停成功");
    }

    @Override
    public void onMerge(M3u8 m3u8) {
        if (mAdapter == null) return;
        mAdapter.updataItem(m3u8, "正在合并文件...");
    }

    @Override
    public void onSuccess(M3u8 m3u8) {
        if (mAdapter == null) return;
        mAdapter.updataItem(m3u8, M3u8State.INSTANCE.getSTETE_SUCCESS(), "下载成功");
    }

    @Override
    public void onProgress(M3u8 m3u8, M3u8Ts m3u8Ts, int totel, int curr) {
        if (mAdapter == null || checkTimeMillis()) return;
        mAdapter.updataItem(m3u8, curr, totel);
    }

    @Override
    public void setDeleteMode(boolean mode) {
        if (mAdapter != null) mAdapter.setDeleteMode(mode);
    }

    @Override
    public void onQueryFile(LinkedList<M3u8File> linkedList) {
        if (mAdapter == null || mAdapter.getList() == null) return;
        if(mSwipeRefreshLayout == null || mCustomEmptyView == null)return ;
        mSwipeRefreshLayout.setEnabled(false);
        mAdapter.setM3u8Files(linkedList);
        if (mAdapter.getList().size() == 0) {
            mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
        }
    }

    @Override
    public void onQueryError(Throwable throwable) {
        if(mSwipeRefreshLayout == null || mCustomEmptyView == null)return ;
        mSwipeRefreshLayout.setEnabled(false);
        mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_ERROR);
        mCustomEmptyView.setEmptyText(throwable.toString());
    }

    @Override
    public void onDelete(M3u8File m3u8File) {
        if (mAdapter == null || mAdapter.getList() == null) return;
        if(mSwipeRefreshLayout == null || mCustomEmptyView == null)return ;
        mAdapter.remove(m3u8File);
        if (mAdapter.getList().size() == 0) {
            mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
        }
    }

    @Override
    public void onDelete(LinkedList<M3u8File> linkedList) {
        if (mAdapter == null || mAdapter.getList() == null) return;
        if(mSwipeRefreshLayout == null || mCustomEmptyView == null)return ;
        mAdapter.clear();
        mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
    }

    @Override
    public void onControl(BaseAdapter adapter, int control, M3u8Warp m3u8File) {
        if (control == STOP) {
            M3u8Manager.INSTANCE.stop(m3u8File.data);
        } else if (control == START) {
            M3u8Manager.INSTANCE.start(m3u8File.data);
            m3u8File.data.setState(M3u8State.INSTANCE.getSTETE_NON());
        } else if (control == PLAY) {
            playM3u8File(m3u8File.data);
        } else if (control == DELETE) {
            DeleteListener listener = new DeleteListener(m3u8File.data);
            DialogUtil.showMaterialDialog(activity, getStringFix(R.string.delete_file), listener);
        }
    }

    private void playM3u8File(M3u8File m3u8File) {
        File file = new File(m3u8File.getM3u8Path(), m3u8File.getM3u8VideoName());
        if (!file.exists() || !file.isFile()) {
            showToast("视频文件已被删除，不能播放");
        } else {
            VideoPlayerActivity.startActivity(activity, file);
        }
    }

    private boolean checkTimeMillis() {
        long l = System.currentTimeMillis();
        if (l - timeMillis > 1000) {
            timeMillis = l;
            return false;
        } else {
            return true;
        }
    }

    private class DeleteListener implements OnButtonClickListener {

        private M3u8File m3u8File;

        public DeleteListener(M3u8File m3u8File) {
            this.m3u8File = m3u8File;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn != OnButtonClickListener.RIGHT) return;
            M3u8Manager.INSTANCE.detele(m3u8File, true);
        }

    }
}
