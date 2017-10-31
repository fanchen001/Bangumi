package com.fanchen.imovie.entity.tucao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiTimeRoot;
import com.fanchen.imovie.entity.face.IBangumiTimeTitle;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/9/20.
 */
public class TucaoTimeLine implements IBangumiTimeRoot,Parcelable{
    private List<IViewType> viewTypes = new ArrayList<>();
    private List<TucaoTimeLineTitle> list;
    private boolean success;
    private String message;

    public TucaoTimeLine(){

    }

    protected TucaoTimeLine(Parcel in) {
        list = in.createTypedArrayList(TucaoTimeLineTitle.CREATOR);
        success = in.readByte() != 0;
        message = in.readString();
    }

    public static final Creator<TucaoTimeLine> CREATOR = new Creator<TucaoTimeLine>() {
        @Override
        public TucaoTimeLine createFromParcel(Parcel in) {
            return new TucaoTimeLine(in);
        }

        @Override
        public TucaoTimeLine[] newArray(int size) {
            return new TucaoTimeLine[size];
        }
    };

    @Override
    public List<? extends IBangumiTimeTitle> getList() {
        return list;
    }

    @Override
    public List<? extends IViewType> getAdapterList() {
        viewTypes.clear();
        for (TucaoTimeLineTitle title : list){
            viewTypes.add(title);
            viewTypes.addAll(title.getList());
        }
        return viewTypes;
    }

    @Override
    public int getPosition() {
        int i = 0;
        for (IViewType viewType : viewTypes){
            if(viewType instanceof IBangumiTimeTitle && ((IBangumiTimeTitle)viewType).isNow()){
                return i;
            }
            i ++;
        }
        return 0;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setList(List<TucaoTimeLineTitle> list) {
        this.list = list;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(list);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
    }
}
