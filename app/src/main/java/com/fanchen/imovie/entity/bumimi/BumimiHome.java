package com.fanchen.imovie.entity.bumimi;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/9/24.
 */
public class BumimiHome implements IHomeRoot,IBangumiMoreRoot {

    private boolean success;
    private String message;
    private List<BumimiTitle> list;
    private List<BumimiVideo> result;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public List<? extends IVideo> getList() {
        return result;
    }

    public void setList(List<BumimiTitle> list) {
        this.list = list;
    }

    @Override
    public List<? extends IViewType> getAdapterResult() {
        List<IViewType> viewTypes = new ArrayList<>();
        for (BumimiTitle title : list){
            viewTypes.add(title);
            viewTypes.addAll(title.getList());
        }
        return viewTypes;
    }

    public void setResult(List<BumimiVideo> result) {
        this.result = result;
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
