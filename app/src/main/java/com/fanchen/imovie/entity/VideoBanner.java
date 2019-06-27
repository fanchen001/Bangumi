package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideoBanner;

/**
 * 重构后的轮播图数据实体
 * Created by fanchen on 2017/9/16.
 */
public class VideoBanner extends VideoBase implements Parcelable, IVideoBanner<VideoBanner> {
    private String extInfo;//附加信息
    private int bannerType;

    public VideoBanner() {
    }

    protected VideoBanner(Parcel in) {
        super(in);
        extInfo = in.readString();
        bannerType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(extInfo);
        dest.writeInt(bannerType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoBanner> CREATOR = new Creator<VideoBanner>() {
        @Override
        public VideoBanner createFromParcel(Parcel in) {
            return new VideoBanner(in);
        }

        @Override
        public VideoBanner[] newArray(int size) {
            return new VideoBanner[size];
        }
    };

    public void setBannerType(int bannerType) {
        this.bannerType = bannerType;
    }

    @Override
    public VideoBanner getData() {
        return this;
    }

    @Override
    public int getBannerType() {
        return bannerType;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    @Override
    public String getReferer() {
        return getHost();
    }

}
