package com.fanchen.imovie.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.arialyy.aria.core.download.DownloadTask;
import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.DownloadTabActivity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.pager.XiguaAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.XiguaDownload;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.view.CustomEmptyView;
import com.squareup.picasso.Picasso;
import com.xigua.p2p.P2PManager;
import com.xigua.p2p.P2PMessageWhat;
import com.xigua.p2p.StorageUtils;
import com.xigua.p2p.TaskVideoInfo;

import java.util.List;

/**
 * XiguaFragment
 * Created by fanchen on 2018/9/21.
 */
public class XiguaFragment extends BaseRecyclerFragment implements DownloadTabActivity.OnDownloadListernr,
        XiguaAdapter.OnXiguaControlListener {

    private boolean isRegister = false;
    private XiguaAdapter mAdapter;

    public static Fragment newInstance() {
        return new XiguaFragment();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(activity);
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText(String.format("下载路径：%s", StorageUtils.getCachePath()));
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mAdapter = new XiguaAdapter(activity);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mAdapter.setOnXiguaControlListener(this);
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (activity == null) return;
        if (isVisibleToUser && !isRegister) {
            activity.registerReceiver(mReceiver, new IntentFilter(P2PMessageWhat.p2p_callback));
            isRegister = true;
        } else if (!isVisibleToUser && isRegister) {
            activity.unregisterReceiver(mReceiver);
            isRegister = false;
        }
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof XiguaDownload)) return;
        XiguaDownload warp = (XiguaDownload) datas.get(position);
        if (warp.taskVideoInfo.getLocalSize() <= 0) return;
        VideoPlayerActivity.startActivity(activity, warp.getXiguaUrl());
    }

    @Override
    public void onControl(BaseAdapter adapter, int control, TaskVideoInfo info) {
        if (control == START) {
            P2PManager.getInstance().play(info.getUrl());
        } else if (control == STOP) {
            P2PManager.getInstance().pause(info.getUrl());
        } else if (control == DELETE) {
            DeleteListener listener = new DeleteListener(info);
            DialogUtil.showMaterialDialog(activity, getString(R.string.delete_file), listener);
        } else if (control == PLAY) {
            String replace = info.getUrl().replace("ftp://", "xg://");
            VideoPlayerActivity.startActivity(activity, replace);
        }
    }

    @Override
    public void setDeleteMode(boolean mode) {
        if (mAdapter != null) mAdapter.setDeleteMode(mode);
    }

    @Override
    public void onTaskUpdate(DownloadTask task) {

    }

    private class DeleteListener implements OnButtonClickListener {

        private TaskVideoInfo info;

        public DeleteListener(TaskVideoInfo info) {
            this.info = info;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn != OnButtonClickListener.RIGHT) return;
            P2PManager.getInstance().remove(info.getUrl());
        }

    }

    /**
     * 西瓜播放器播放广播接受者
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAdapter == null || intent == null) return;
            if (intent.getIntExtra("what", 0) != 2 || !intent.hasExtra("data")) return;
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(false);
            List<TaskVideoInfo> infos = intent.getParcelableArrayListExtra("data");
            if (infos == null || infos.isEmpty()) {
                mCustomEmptyView.setEmptyType(CustomEmptyView.TYPE_EMPTY);
            } else {
                mAdapter.setTaskVideoInfos(infos);
            }
        }

    };

}
