package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IViewType;

/**
 * Created by fanchen on 2017/10/11.
 */
public class VideoWeb implements Parcelable,IViewType{
    private String url;
    private String name;
    private String cover;

    public VideoWeb(){
    }

    protected VideoWeb(Parcel in) {
        url = in.readString();
        name = in.readString();
        cover = in.readString();
    }

    public static final Creator<VideoWeb> CREATOR = new Creator<VideoWeb>() {
        @Override
        public VideoWeb createFromParcel(Parcel in) {
            return new VideoWeb(in);
        }

        @Override
        public VideoWeb[] newArray(int size) {
            return new VideoWeb[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(cover);
    }

    @Override
    public int getViewType() {
        return TYPE_NORMAL;
    }
}
