package com.fanchen.imovie.entity;

import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.inf.IEntity;
import com.fanchen.imovie.base.BaseDownloadAdapter;

/**
 * DownloadEntityWrap
 * Created by fanchen on 2017/10/9.
 */
public class DownloadEntityWrap extends BaseDownloadAdapter.DownloadWarp<DownloadEntity> {

    public DownloadEntityWrap(DownloadEntity entity,int position) {
        this.data = entity;
        this.position = position;
    }

    @Override
    public String getMessage() {
        if(getDownloadState() == SUCCESS){
            return getFileSize(data.getFileSize());
        }else if(getDownloadState() == DOWNLOADING){
            return data.getConvertSpeed();
        }
        return "";
    }

    @Override
    public String getName() {
        return data.getFileName();
    }

    @Override
    public int getDownloadState() {
        if(data.getState() == IEntity.STATE_RUNNING){
            return DOWNLOADING;
        }else if(data.getState() == IEntity.STATE_WAIT){
            return WAIT;
        }else if(data.getState() == IEntity.STATE_FAIL){
            return ERROR;
        }else if(data.getState() == IEntity.STATE_COMPLETE){
            return SUCCESS;
        }else{
            return STOP;
        }
    }

    @Override
    public long getProgress() {
        holder.progressBar.setMax(100);
        int percent = data.getPercent();
        long fileSize = data.getFileSize();
        if (fileSize <= 0) fileSize = Integer.MAX_VALUE;
        return percent <= 0 ?  (data.getCurrentProgress() * 100L / fileSize) : percent;
    }

    @Override
    public long getMax() {
        return 100;
    }
}
