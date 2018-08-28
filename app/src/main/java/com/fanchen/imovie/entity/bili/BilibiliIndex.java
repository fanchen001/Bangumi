package com.fanchen.imovie.entity.bili;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/12/29.
 */
public class BilibiliIndex implements Parcelable{
    private int code;
    private BilibiliData data;
    private String message;
    private int ttl;

    public BilibiliIndex(){
    }

    protected BilibiliIndex(Parcel in) {
        code = in.readInt();
        data = in.readParcelable(BilibiliData.class.getClassLoader());
        message = in.readString();
        ttl = in.readInt();
    }

    public static final Creator<BilibiliIndex> CREATOR = new Creator<BilibiliIndex>() {
        @Override
        public BilibiliIndex createFromParcel(Parcel in) {
            return new BilibiliIndex(in);
        }

        @Override
        public BilibiliIndex[] newArray(int size) {
            return new BilibiliIndex[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeParcelable(data, flags);
        dest.writeString(message);
        dest.writeInt(ttl);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public BilibiliData getData() {
        return data;
    }

    public void setData(BilibiliData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
