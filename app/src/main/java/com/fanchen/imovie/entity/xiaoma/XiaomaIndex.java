package com.fanchen.imovie.entity.xiaoma;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/11/4.
 */
public class XiaomaIndex<T extends Parcelable> implements Parcelable{
    private int status;
    private long ts;
    private T result;

    public XiaomaIndex(){
    }

    protected XiaomaIndex(Parcel in) {
        status = in.readInt();
        ts = in.readLong();
        result = in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<XiaomaIndex> CREATOR = new Creator<XiaomaIndex>() {
        @Override
        public XiaomaIndex createFromParcel(Parcel in) {
            return new XiaomaIndex(in);
        }

        @Override
        public XiaomaIndex[] newArray(int size) {
            return new XiaomaIndex[size];
        }
    };

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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeLong(ts);
        dest.writeParcelable(result, flags);
    }
}
