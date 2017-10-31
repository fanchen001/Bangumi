package com.fanchen.imovie.entity.xiaoma;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/7/15.
 */
public class XiaomaWordIndex implements Parcelable{
    private XiaomaWordResult result;
    private int status;
    private long ts;

    public XiaomaWordIndex() {
    }

    protected XiaomaWordIndex(Parcel in) {
        result = in.readParcelable(XiaomaWordResult.class.getClassLoader());
        status = in.readInt();
        ts = in.readLong();
    }

    public static final Creator<XiaomaWordIndex> CREATOR = new Creator<XiaomaWordIndex>() {
        @Override
        public XiaomaWordIndex createFromParcel(Parcel in) {
            return new XiaomaWordIndex(in);
        }

        @Override
        public XiaomaWordIndex[] newArray(int size) {
            return new XiaomaWordIndex[size];
        }
    };

    public XiaomaWordResult getResult() {
        return result;
    }

    public void setResult(XiaomaWordResult result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<String> getListData(){
        List<String> all = new ArrayList<>();
        for (XiaomaWord w : result.getHotKeyword()) {
            if(w.getKeywords() != null)
                all.addAll(w.getKeywords());
        }
        return all;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(result, flags);
        dest.writeInt(status);
        dest.writeLong(ts);
    }
}
