package com.fanchen.imovie.entity.jren;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * Created by fanchen on 2017/9/24.
 */
public class JrenHome implements IHomeRoot,IBangumiMoreRoot {
    private boolean success;
    private String message;
    private List<JrenVideo> list;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public List<JrenVideo> getList() {
        return list;
    }

    public void setList(List<JrenVideo> list) {
        this.list = list;
    }

    @Override
    public List<? extends IViewType> getAdapterResult() {
        return list;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
