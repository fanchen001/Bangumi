package com.fanchen.imovie.entity;


import android.text.TextUtils;

import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.imovie.entity.face.IViewType;
import com.xigua.p2p.TaskVideoInfo;

/**
 * XiguaDownload
 * Created by fanchen on 2018/9/21.
 */
public class XiguaDownload extends BaseDownloadAdapter.DownloadWarp<TaskVideoInfo> {

    public XiguaDownload(TaskVideoInfo data) {
        this.data = data;
    }

    public String getFileName() {
        if (data == null) return "";
        String url = data.getUrl();
        if (TextUtils.isEmpty(url)) return "";
        String[] split = url.split("/");
        return split[split.length - 1];
    }

    public String getXiguaUrl() {
        if (data == null) return "";
        return data.getUrl() == null ? "" : data.getUrl().replace("ftp://", "xg://");
    }

    @Override
    public String getMessage() {
        if (getDownloadState() == SUCCESS) {
            return getFileSize(data.getLocalSize());
        } else if (getDownloadState() == DOWNLOADING) {
            return getSpeed(data.getSpeed());
        }
        return "";
    }

    @Override
    public String getName() {
        return getFileName();
    }

    @Override
    public int getDownloadState() {
        if (data.getLocalSize() > 0) {
            return SUCCESS;
        } else if (data.getState() == TaskVideoInfo.START) {
            return DOWNLOADING;
        } else if (data.getState() == TaskVideoInfo.PAUSE) {
            return STOP;
        }
        return ERROR;
    }

    @Override
    public long getProgress() {
        if(getDownloadState() == SUCCESS){
            return 100;
        }
        return  data.getDownSize();
    }

    @Override
    public long getMax() {
        if(getDownloadState() == SUCCESS){
            return 100;
        }
        return  data.getTotalSize();
    }

}
