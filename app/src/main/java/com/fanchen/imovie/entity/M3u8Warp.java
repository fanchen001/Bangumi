package com.fanchen.imovie.entity;

import android.text.TextUtils;

import com.fanchen.imovie.base.BaseDownloadAdapter;
import com.fanchen.m3u8.bean.M3u8File;
import com.fanchen.m3u8.bean.M3u8State;

/**
 * M3u8Warp
 * Created by fanchen on 2018/9/17.
 */
public class M3u8Warp extends BaseDownloadAdapter.DownloadWarp<M3u8File> {

    public int max = -1;
    public int progress = -1;
    public String msg = "";

    public M3u8Warp(M3u8File m3u8File) {
        this.data = m3u8File;
    }

    @Override
    public String getMessage() {
        if (TextUtils.isEmpty(msg)) {
            int downloadState = getDownloadState();
            if (downloadState == STOP) {
                msg = "停止";
            } else if (downloadState == SUCCESS) {
                msg = "完成";
            } else if (downloadState == ERROR) {
                msg = "下载失败";
            } else {
                long max = getMax();
                long progress = getProgress();
                return String.format("%d/%d", progress, max);
            }
        }
        return msg;
    }

    @Override
    public String getName() {
        return data.getM3u8VideoName();
    }

    @Override
    public int getDownloadState() {
        if (data.getState() == M3u8State.INSTANCE.getSTETE_STOP()) {
            return STOP;
        } else if (data.getState() == M3u8State.INSTANCE.getSTETE_ERROR()) {
            return ERROR;
        } else if (data.getState() == M3u8State.INSTANCE.getSTETE_SUCCESS()) {
            return SUCCESS;
        } else if (data.getState() == M3u8State.INSTANCE.getSTETE_DOWNLOAD()) {
            return DOWNLOADING;
        } else {
            return WAIT;
        }
    }

    @Override
    public long getProgress() {
        if (progress != -1) return progress;
        if (getDownloadState() == SUCCESS) {
            return 100;
        } else if (getDownloadState() == WAIT) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public long getMax() {
        if (max != -1) return max;
        return 100;
    }

}
