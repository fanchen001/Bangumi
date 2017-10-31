package com.fanchen.imovie.entity;

import com.arialyy.aria.core.download.DownloadEntity;
import com.fanchen.imovie.entity.face.IViewType;

/**
 * Created by fanchen on 2017/10/9.
 */
public class DownloadEntityWrap implements IViewType {

    private DownloadEntity entity;
    private int position;

    public DownloadEntityWrap(DownloadEntity entity,int position) {
        this.entity = entity;
        this.position = position;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }

    public DownloadEntity getEntity() {
        return entity;
    }

    public int getPosition() {
        return position;
    }
}
