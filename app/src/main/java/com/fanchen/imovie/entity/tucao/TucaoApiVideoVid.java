package com.fanchen.imovie.entity.tucao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/9/20.
 */
public class TucaoApiVideoVid implements Parcelable{
    private String type;
    private String vid;
    private String title;

    public TucaoApiVideoVid(){
    }

    protected TucaoApiVideoVid(Parcel in) {
        type = in.readString();
        vid = in.readString();
        title = in.readString();
    }

    public static final Creator<TucaoApiVideoVid> CREATOR = new Creator<TucaoApiVideoVid>() {
        @Override
        public TucaoApiVideoVid createFromParcel(Parcel in) {
            return new TucaoApiVideoVid(in);
        }

        @Override
        public TucaoApiVideoVid[] newArray(int size) {
            return new TucaoApiVideoVid[size];
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
