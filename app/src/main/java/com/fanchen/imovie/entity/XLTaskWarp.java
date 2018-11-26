package com.fanchen.imovie.entity;

import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.xunlei.downloadlib.parameter.BroadcastInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

/**
 * XLTaskWarp
 * Created by fanchen on 2018/10/17.
 */
public class XLTaskWarp extends BaseDownloadAdapter.DownloadWarp<XLTaskInfo> {

    public XLTaskWarp(XLTaskInfo info) {
        this.data = info;
    }

    @Override
    public int getDownloadState() {
        if (data.mTaskStatus == XLTaskInfo.DOWNLOADING) {
            return DOWNLOADING;
        } else if (data.mTaskStatus == XLTaskInfo.STOP) {
            return STOP;
        } else if (data.mTaskStatus == XLTaskInfo.SUCCESS &&  data.mFileSize == data.mDownloadSize && data.mDownloadSize > 0) {
            return SUCCESS;
        } else if (data.mTaskStatus == XLTaskInfo.FAULT) {
            return ERROR;
        } else if (data.mTaskStatus == XLTaskInfo.CONNECT) {
            return WAIT;
        }
        return 0;
    }

    public String getSpeed() {
        return getSpeed(data.mDownloadSpeed + data.mAdditionalResDCDNSpeed);
    }

    public String getFileSize() {
        return getFileSize(data.mFileSize);
    }


    @Override
    public String getMessage() {
        if ( getDownloadState() == SUCCESS ) {
            return getFileSize();
        } else if ( getDownloadState() == DOWNLOADING) {
            return getSpeed();
        }
        return "";
    }

    @Override
    public String getName() {
        return data.mFileName;
    }

    @Override
    public long getProgress() {
        return getDownloadState() == SUCCESS ? 100 : data.mDownloadSize;
    }

    @Override
    public long getMax() {
        return getDownloadState() == SUCCESS ? 100 : data.mFileSize;
    }

    public long getMax(BroadcastInfo info) {
        return getDownloadState() == SUCCESS ? 100 :  info.mFileSize;
    }

    public long getProgress(BroadcastInfo info) {
        return getDownloadState() == SUCCESS ? 100 :  info.mDownloadSize;
    }

}
