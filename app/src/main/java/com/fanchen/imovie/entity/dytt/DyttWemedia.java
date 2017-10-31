package com.fanchen.imovie.entity.dytt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/9/22.
 */
public class DyttWemedia implements Parcelable{
    private int videocount;
    private String headImg;
    private int fanscount;
    private String title;

    public DyttWemedia(){

    }

    protected DyttWemedia(Parcel in) {
        videocount = in.readInt();
        headImg = in.readString();
        fanscount = in.readInt();
        title = in.readString();
    }

    public static final Creator<DyttWemedia> CREATOR = new Creator<DyttWemedia>() {
        @Override
        public DyttWemedia createFromParcel(Parcel in) {
            return new DyttWemedia(in);
        }

        @Override
        public DyttWemedia[] newArray(int size) {
            return new DyttWemedia[size];
        }
    };

    public int getVideocount() {
        return videocount;
    }

    public void setVideocount(int videocount) {
        this.videocount = videocount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFanscount() {
        return fanscount;
    }

    public void setFanscount(int fanscount) {
        this.fanscount = fanscount;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(videocount);
        dest.writeString(headImg);
        dest.writeInt(fanscount);
        dest.writeString(title);
    }
}
