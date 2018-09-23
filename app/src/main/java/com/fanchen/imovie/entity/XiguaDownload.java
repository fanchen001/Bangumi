package com.fanchen.imovie.entity;


import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IViewType;
import com.xigua.p2p.TaskVideoInfo;

/**
 * XiguaDownload
 * Created by fanchen on 2018/9/21.
 */
public class XiguaDownload implements IViewType {

    public TaskVideoInfo taskVideoInfo;

    public XiguaDownload(TaskVideoInfo taskVideoInfo) {
        this.taskVideoInfo = taskVideoInfo;
    }

    public String getFileName() {
        if (taskVideoInfo == null) return "";
        String url = taskVideoInfo.getUrl();
        if (TextUtils.isEmpty(url)) return "";
        String[] split = url.split("/");
        return split[split.length - 1];
    }

    public String getXiguaUrl(){
        if (taskVideoInfo == null) return "";
        return taskVideoInfo.getUrl().replace("ftp://", "xg://");
    }

    @Override
    public int getViewType() {
        return 0;
    }

}
