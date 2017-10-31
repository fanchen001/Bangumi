package com.fanchen.imovie.entity.acg;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/10/2.
 */
public class AcgToken implements Parcelable{
    private String token;

    public AcgToken(){
    }

    protected AcgToken(Parcel in) {
        token = in.readString();
    }

    public static final Creator<AcgToken> CREATOR = new Creator<AcgToken>() {
        @Override
        public AcgToken createFromParcel(Parcel in) {
            return new AcgToken(in);
        }

        @Override
        public AcgToken[] newArray(int size) {
            return new AcgToken[size];
        }
    };

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
    }
}
