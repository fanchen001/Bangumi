package com.fanchen.imovie.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.DownloadTabActivity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.XiguaAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.XiguaDownload;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.util.LogUtil;
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
public class XiguaFragment extends BaseRecyclerFragment implements DownloadTabActivity.OnDeleteListernr,
        BaseDownloadAdapter.OnDownloadControlListener<XiguaDownload> {

    private boolean isRegister = false;
    private XiguaAdapter mAdapter;

    public static Fragment newInstance() {
        return new XiguaFragment();
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(activity);
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
        mAdapter.setOnDownloadControlListener(this);
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
            activity.registerReceiver(mReceiver, new IntentFilter(P2PMessageWhat.P2P_CALLBACK));
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
        if (warp.data == null || warp.data.getLocalSize() <= 0) return;
        VideoPlayerActivity.startActivity(activity, warp.getXiguaUrl());
    }

    @Override
    public void onControl(BaseAdapter adapter, int control, XiguaDownload info) {
        if (control == START) {
            P2PManager.getInstance().play(info.data.getUrl());
        } else if (control == STOP) {
            P2PManager.getInstance().pause(info.data.getUrl());
        } else if (control == DELETE) {
            DeleteListener listener = new DeleteListener(info.data);
            DialogUtil.showMaterialDialog(activity, getStringFix(R.string.delete_file), listener);
        } else if (control == PLAY) {
            VideoPlayerActivity.startActivity(activity, info.data.getXiguaUrl());
        }
    }

    @Override
    public void setDeleteMode(boolean mode) {
        if (mAdapter != null) mAdapter.setDeleteMode(mode);
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
            try {
                if (mSwipeRefreshLayout == null || mAdapter == null) return;
                if (intent == null || !P2PMessageWhat.P2P_CALLBACK.equals(intent.getAction()))
                    return;
                if (intent.getIntExtra(P2PMessageWhat.WHAT, 0) != P2PMessageWhat.MESSAGE_TASK_LIST)
                    return;
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
                if (intent.hasExtra(P2PMessageWhat.DATA)) {
                    List<TaskVideoInfo> infos = intent.getParcelableArrayListExtra(P2PMessageWhat.DATA);
                    mAdapter.setTaskVideoInfos(infos);
                    mCustomEmptyView.setEmptyType(mAdapter.getList().isEmpty() ? CustomEmptyView.TYPE_EMPTY : CustomEmptyView.TYPE_NON);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

}
