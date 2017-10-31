package com.fanchen.imovie.entity.acg;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by fanchen on 2017/7/23.
 */
public class AcgRoot<T extends Parcelable> implements Parcelable{
    private int code;
    private T data;
    private String msg;

    public AcgRoot(){

    }

    protected AcgRoot(Parcel in) {
        code = in.readInt();
        data = in.readParcelable(getClass().getClassLoader());
        msg = in.readString();
    }

    public static final Creator<AcgRoot> CREATOR = new Creator<AcgRoot>() {
        @Override
        public AcgRoot createFromParcel(Parcel in) {
            return new AcgRoot(in);
        }

        @Override
        public AcgRoot[] newArray(int size) {
            return new AcgRoot[size];
        }
    };

    public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
    public void setData(T data){
        this.data = data;
    }
    public T getData(){
        return this.data;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return this.msg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeParcelable(data, flags);
        dest.writeString(msg);
    }
}
