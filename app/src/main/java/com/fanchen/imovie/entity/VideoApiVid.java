package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/9/20.
 */
public class VideoApiVid implements Parcelable{
    private String type;
    private String vid;
    private String title;

    public VideoApiVid(){
    }

    protected VideoApiVid(Parcel in) {
        type = in.readString();
        vid = in.readString();
        title = in.readString();
    }

    public static final Creator<VideoApiVid> CREATOR = new Creator<VideoApiVid>() {
        @Override
        public VideoApiVid createFromParcel(Parcel in) {
            return new VideoApiVid(in);
        }

        @Override
        public VideoApiVid[] newArray(int size) {
            return new VideoApiVid[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(vid);
        dest.writeString(title);
    }
}
