package com.fanchen.imovie.entity;

import android.support.v7.widget.RecyclerView;

import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.m3u8.bean.M3u8File;
import com.fanchen.m3u8.bean.M3u8State;

/**
 * M3u8Warp
 * Created by fanchen on 2018/9/17.
 */
public class M3u8Warp implements IViewType {
    public M3u8File m3u8File;
    public RecyclerView.ViewHolder holder;

    public M3u8Warp(M3u8File m3u8File) {
        this.m3u8File = m3u8File;
    }

    public String getState(){
        if(m3u8File.getState() == M3u8State.INSTANCE.getSTETE_STOP()){
            return "停止下载";
        }else if(m3u8File.getState() == M3u8State.INSTANCE.getSTETE_ERROR()){
            return "下载失败";
        }if(m3u8File.getState() == M3u8State.INSTANCE.getSTETE_SUCCESS()){
            return "下载成功";
        }else {
            return "等待中...";
        }
    }

    public int getProgress(){
        if(m3u8File.getState() == M3u8State.INSTANCE.getSTETE_SUCCESS()){
            return 100;
        }else if(m3u8File.getState() == M3u8State.INSTANCE.getSTETE_NON()){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public int getViewType() {
        return 0;
    }
}
