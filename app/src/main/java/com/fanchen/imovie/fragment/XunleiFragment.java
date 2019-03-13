package com.fanchen.imovie.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.DownloadTabActivity;
import com.fanchen.imovie.activity.VideoPlayerActivity;
import com.fanchen.imovie.adapter.XunleiAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.base.BaseRecyclerFragment;
import com.fanchen.imovie.dialog.BaseAlertDialog;
import com.fanchen.imovie.dialog.OnButtonClickListener;
import com.fanchen.imovie.entity.XLTaskWarp;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.util.DialogUtil;
import com.fanchen.imovie.view.CustomEmptyView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xunlei.XLAppliction;
import com.xunlei.XLManager;
import com.xunlei.downloadlib.XLService;
import com.xunlei.downloadlib.parameter.BroadcastInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import java.io.File;
import java.util.List;

/**
 * XunleiFragment
 * Created by fanchen on 2018/10/17.
 */
public class XunleiFragment extends BaseRecyclerFragment implements DownloadTabActivity.OnDeleteListernr,
        BaseDownloadAdapter.OnDownloadControlListener<XLTaskWarp> {

    private XunleiAdapter mXunleiAdapter;

    public static Fragment newInstance() {
        return new XunleiFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(XLService.REFRESH_LIST);
        filter.addAction(XLService.REFRESH_SPEED);
        activity.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.unregisterReceiver(mReceiver);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new BaseAdapter.LinearLayoutManagerWrapper(activity);
    }

    @Override
    public BaseAdapter getAdapter(Picasso picasso) {
        return mXunleiAdapter = new XunleiAdapter(activity);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mXunleiAdapter.setOnDownloadControlListener(this);
    }

    @Override
    protected void initFragment(@Nullable Bundle savedInstanceState, Bundle args) {
        super.initFragment(savedInstanceState, args);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText(String.format("下载路径：%s", XLAppliction.XL_PATH));
    }

    @Override
    public void loadData(Bundle savedInstanceState, RetrofitManager retrofit, int page) {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onItemClick(List<?> datas, View v, int position) {
        if (!(datas.get(position) instanceof XLTaskWarp)) return;
        XLTaskWarp warp = (XLTaskWarp) datas.get(position);
        if (warp.data.mTaskStatus != 2 || TextUtils.isEmpty(warp.data.mFileName)) return;
        File file = new File(XLAppliction.XL_PATH, warp.data.mFileName);
        if (!file.exists()) {
            showToast("文件已删除");
        } if(file.getName().toLowerCase().contains(".torrent")){
            XLManager.get(activity).addTask(file.getAbsolutePath());
            showToast("添加BT任务成功");
        }else {
            String absolutePath = file.getAbsolutePath();
            VideoPlayerActivity.startActivity(activity, absolutePath);
        }
    }

    @Override
    public void setDeleteMode(boolean mode) {
        if (mXunleiAdapter == null) return;
        mXunleiAdapter.setDeleteMode(mode);
    }

    private void setXLTasks(List<XLTaskInfo> infos) {
        if (mXunleiAdapter == null || mCustomEmptyView == null) return;
        mXunleiAdapter.setXLTasks(infos);
        mCustomEmptyView.setEmptyType(mXunleiAdapter.getList().size() == 0 ? CustomEmptyView.TYPE_EMPTY : CustomEmptyView.TYPE_NON);
    }

    @Override
    public void onControl(BaseAdapter adapter, int control, XLTaskWarp data) {
        if (control == STOP) {
            XLManager.get(activity).stop(data.data.mURI);
        } else if (control == START) {
            XLManager.get(activity).start(data.data.mURI);
        } else if (control == PLAY) {
            File file = new File(XLAppliction.XL_PATH, data.data.mFileName);
            if (!file.exists()) {
                showToast("文件已删除");
            } else {
                String absolutePath = file.getAbsolutePath();
                VideoPlayerActivity.startActivity(activity, absolutePath);
            }
        } else if (control == DELETE) {
            DeleteListener listener = new DeleteListener(data.data);
            DialogUtil.showMaterialDialog(activity, getStringFix(R.string.delete_file), listener);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mXunleiAdapter == null || intent == null) return;
            String action = intent.getAction();
            if (XLService.REFRESH_LIST.equals(action)) {
                mSwipeRefreshLayout.setEnabled(false);
                mSwipeRefreshLayout.setRefreshing(false);
                List<XLTaskInfo> infos = intent.getParcelableArrayListExtra(XLService.DATA);
                setXLTasks(infos);
                Log.e("onReceive", "info -> " + new Gson().toJson(infos));
            } else if (XLService.REFRESH_SPEED.equals(action) && getUserVisibleHint()) {
                List<BroadcastInfo> extra = intent.getParcelableArrayListExtra(XLService.DATA);
                Log.e("onReceive","speed -> " + new Gson().toJson(extra));
                mXunleiAdapter.updataXLTaskInfo(extra);
            }
        }

    };

    private class DeleteListener implements OnButtonClickListener {

        private XLTaskInfo info;

        public DeleteListener(XLTaskInfo info) {
            this.info = info;
        }

        @Override
        public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
            dialog.dismiss();
            if (btn != OnButtonClickListener.RIGHT) return;
            XLManager.get(activity).deleteTask(info.mURI);
        }

    }
}
