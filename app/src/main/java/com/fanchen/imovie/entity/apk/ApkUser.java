package com.fanchen.imovie.entity.apk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/3/22.
 */
public class ApkUser implements Parcelable{
    private int action;
    private int status;
    private String data;

    protected ApkUser(Parcel in) {
        action = in.readInt();
        status = in.readInt();
        data = in.readString();
    }

    public ApkUser() {
    }

    public static final Creator<ApkUser> CREATOR = new Creator<ApkUser>() {
        @Override
        public ApkUser createFromParcel(Parcel in) {
            return new ApkUser(in);
        }

        @Override
        public ApkUser[] newArray(int size) {
            return new ApkUser[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(action);
        dest.writeInt(status);
        dest.writeString(data);
    }
}
