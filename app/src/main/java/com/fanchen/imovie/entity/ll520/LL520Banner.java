package com.fanchen.imovie.entity.ll520;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideoBanner;
import com.fanchen.imovie.retrofit.service.LL520Service;

/**
 * Created by fanchen on 2017/11/9.
 */
public class LL520Banner implements Parcelable, IVideoBanner<LL520Banner> {
    /**
     * 标题
     **/
    private String title;
    /**
     * 图片
     **/
    private String cover;
    /**
     * id
     **/
    private String id;
    /**
     * url
     **/
    private String url;

    public LL520Banner(){
    }

    protected LL520Banner(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
    }

    public static final Creator<LL520Banner> CREATOR = new Creator<LL520Banner>() {
        @Override
        public LL520Banner createFromParcel(Parcel in) {
            return new LL520Banner(in);
        }

        @Override
        public LL520Banner[] newArray(int size) {
            return new LL520Banner[size];
        }
    };

    @Override
    public int getSource() {
        return 0;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getId() {
        return getUrl();
    }

    @Override
    public String getServiceClass() {
        return LL520Service.class.getName();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCover() {
        return cover;
    }

    @Override
    public LL520Banner getData() {
        return this;
    }

    @Override
    public int getBannerType() {
        return TYPE_NATIVE;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeString(id);
        dest.writeString(url);
    }
}
