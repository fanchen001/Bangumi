package com.fanchen.imovie.entity.s80;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * Created by fanchen on 2017/9/23.
 */
public class S80Home implements IHomeRoot,IBangumiMoreRoot,Parcelable{

    private List<S80Video> result;
    private boolean success;
    private String message;

    public S80Home(){
    }

    protected S80Home(Parcel in) {
        result = in.createTypedArrayList(S80Video.CREATOR);
        success = in.readByte() != 0;
        message = in.readString();
    }

    public static final Creator<S80Home> CREATOR = new Creator<S80Home>() {
        @Override
        public S80Home createFromParcel(Parcel in) {
            return new S80Home(in);
        }

        @Override
        public S80Home[] newArray(int size) {
            return new S80Home[size];
        }
    };

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

    public List<S80Video> getResult() {
        return result;
    }

    public void setResult(List<S80Video> result) {
        this.result = result;
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
        dest.writeTypedList(result);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
    }

    @Override
    public List<? extends IVideo> getList() {
        return result;
    }
}
