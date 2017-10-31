package com.fanchen.imovie.entity.dm5;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/10/2.
 */
public class Dm5Home implements IHomeRoot,IBangumiMoreRoot {

    private List<Dm5Title> titles;
    private List<Dm5Video> list;
    private String message;
    private boolean success;

    public List<Dm5Title> getTitles() {
        return titles;
    }

    public void setTitles(List<Dm5Title> titles) {
        this.titles = titles;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public List<? extends IViewType> getAdapterResult() {
        List<IViewType> viewTypes = new ArrayList<>();
        if(titles != null){
            for (Dm5Title title : titles){
                viewTypes.add(title);
                viewTypes.addAll(title.getList());
            }
        }else if(list != null){
            viewTypes.addAll(list);
        }
        return viewTypes;
    }

    @Override
    public List<Dm5Video> getList() {
        return list;
    }

    public void setList(List<Dm5Video> list) {
        this.list = list;
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
