package com.fanchen.imovie.entity.dianxiumei;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * Created by fanchen on 2017/10/13.
 */
public class DianxiumeiHome implements IHomeRoot,IBangumiMoreRoot{

    private List<DianxiumeiVideo> result;
    private boolean success;
    private String message;

    @Override
    public List<? extends IViewType> getAdapterResult() {
        return result;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setResult(List<DianxiumeiVideo> result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public List<? extends IVideo> getList() {
        return result;
    }
}
