package com.fanchen.imovie.entity.apk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/7/16.
 */
public class ApkParamUser implements Parcelable{

    private int action = 0;
    private String session = "";

    protected ApkParamUser(Parcel in) {
        action = in.readInt();
        session = in.readString();
    }

    public static final Creator<ApkParamUser> CREATOR = new Creator<ApkParamUser>() {
        @Override
        public ApkParamUser createFromParcel(Parcel in) {
            return new ApkParamUser(in);
        }

        @Override
        public ApkParamUser[] newArray(int size) {
            return new ApkParamUser[size];
        }
    };

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public ApkParamUser(int action, String session) {

        this.action = action;
        this.session = session;
    }

    public ApkParamUser() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(action);
        dest.writeString(session);
    }
}
