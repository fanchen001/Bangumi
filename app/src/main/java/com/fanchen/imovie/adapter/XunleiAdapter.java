package com.fanchen.imovie.adapter;

import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.entity.XLTaskWarp;
import com.xunlei.downloadlib.parameter.BroadcastInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * XunleiAdapter
 * Created by fanchen on 2018/10/17.
 */
public class XunleiAdapter extends BaseDownloadAdapter<XLTaskWarp> {

    public XunleiAdapter(BaseActivity context) {
        super(context);
    }

    public void setXLTasks(List<XLTaskInfo> all) {
        List<XLTaskWarp> list = new ArrayList<>();
        for (XLTaskInfo info : all) {
            list.add(new XLTaskWarp(info));
        }
        setList(list,true);
    }

    public void updataXLTaskInfo(List<BroadcastInfo> extra) {
        for (XLTaskWarp warp : getXLTask(extra)) {
            setBtnState(warp.holder, warp);
        }
    }

    private List<XLTaskWarp> getXLTask(List<BroadcastInfo> extra) {
        List<XLTaskWarp> xlTask = new ArrayList<>();
        if (extra == null || extra.isEmpty() || getItemCount() == 0) return xlTask;
        for (BroadcastInfo info : extra) {
            for (XLTaskWarp warp : (List<XLTaskWarp>) getList()) {
                if (warp.holder != null && warp.data.mTaskId == info.mTaskId) {
                    warp.data.mTaskStatus = info.mTaskStatus;
                    warp.data.mDownloadSize = info.mDownloadSize;
                    warp.data.mFileSize = info.mFileSize;
                    warp.data.mDownloadSpeed = info.mDownloadSpeed;
                    xlTask.add(warp);
                }
            }
        }
        return xlTask;
    }
}
