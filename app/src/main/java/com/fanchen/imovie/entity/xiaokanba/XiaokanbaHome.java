package com.fanchen.imovie.entity.xiaokanba;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/10/16.
 */
public class XiaokanbaHome implements IHomeRoot, IBangumiMoreRoot {

    private List<XiaokanbaVideo> result;
    private List<XiaokanbaTitle> list;
    private boolean success;
    private String message;


    @Override
    public List<? extends IVideo> getList() {
        return result;
    }

    @Override
    public List<? extends IViewType> getAdapterResult() {
        List<IViewType> lists = new ArrayList<>();
        if(list != null ){
            for (XiaokanbaTitle title : list){
                lists.add(title);
                List<? extends IVideo> list = title.getList();
                if(list != null){
                    lists.addAll(list);
                }
            }
        }else if(result != null){
            lists.addAll(result);
        }
        return lists;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setResult(List<XiaokanbaVideo> result) {
        this.result = result;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setList(List<XiaokanbaTitle> list) {
        this.list = list;
    }
}
