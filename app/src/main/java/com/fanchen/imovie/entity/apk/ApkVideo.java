package com.fanchen.imovie.entity.apk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/7/16.
 */
public class ApkVideo implements Parcelable{
    private String best;
    private String low;

    public ApkVideo(){

    }

    protected ApkVideo(Parcel in) {
        best = in.readString();
        low = in.readString();
    }

    public String getBest() {
        return best;
    }

    public void setBest(String best) {
        this.best = best;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public static final Creator<ApkVideo> CREATOR = new Creator<ApkVideo>() {
        @Override
        public ApkVideo createFromParcel(Parcel in) {
            return new ApkVideo(in);
        }

        @Override
        public ApkVideo[] newArray(int size) {
            return new ApkVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(best);
        dest.writeString(low);
    }
}
