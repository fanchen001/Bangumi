package com.fanchen.imovie.entity.dytt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/8/2.
 */
public class DyttLiveVideoUrls implements Parcelable{
    private String location;
    private String title;
    private int vip;

    public DyttLiveVideoUrls(){

    }

    protected DyttLiveVideoUrls(Parcel in) {
        location = in.readString();
        title = in.readString();
        vip = in.readInt();
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static final Creator<DyttLiveVideoUrls> CREATOR = new Creator<DyttLiveVideoUrls>() {
        @Override
        public DyttLiveVideoUrls createFromParcel(Parcel in) {
            return new DyttLiveVideoUrls(in);
        }

        @Override
        public DyttLiveVideoUrls[] newArray(int size) {
            return new DyttLiveVideoUrls[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeString(title);
        dest.writeInt(vip);
    }
}
