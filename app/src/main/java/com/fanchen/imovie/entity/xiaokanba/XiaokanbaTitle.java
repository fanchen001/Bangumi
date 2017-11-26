package com.fanchen.imovie.entity.xiaokanba;

import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.XiaokanbaService;

import java.util.List;

/**
 * Created by fanchen on 2017/10/16.
 */
public class XiaokanbaTitle implements IBangumiTitle{
    /*** 标题**/
    private String title;
    /*** id**/
    private String id;
    /*** url**/
    private String url;
    private int drawable;
    private List<XiaokanbaVideo> result;

    @Override
    public boolean hasMore() {
        return !TextUtils.isEmpty(url);
    }

    @Override
    public String getFormatUrl() {
        return url;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getDrawable() {
        return drawable;
    }

    @Override
    public List<? extends IVideo> getList() {
        return result;
    }

    @Override
    public String getServiceClassName() {
        return XiaokanbaService.class.getName();
    }

    @Override
    public String getId() {
        String[] split = id.split("_");
        if(split.length >= 3){
            return split[2];
        }
        return id;
    }

    @Override
    public int getStartPage() {
        return 1;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_TITLE;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public void setResult(List<XiaokanbaVideo> result) {
        this.result = result;
    }
}
