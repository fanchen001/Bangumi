package com.fanchen.imovie.entity.bili;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/12/29.
 */
public class BilibiliWord implements Parcelable{
    private String keyword;
    private String status;

    public BilibiliWord(){
    }

    protected BilibiliWord(Parcel in) {
        keyword = in.readString();
        status = in.readString();
    }

    public static final Creator<BilibiliWord> CREATOR = new Creator<BilibiliWord>() {
        @Override
        public BilibiliWord createFromParcel(Parcel in) {
            return new BilibiliWord(in);
        }

        @Override
        public BilibiliWord[] newArray(int size) {
            return new BilibiliWord[size];
        }
    };

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(keyword);
        dest.writeString(status);
    }
}
