package com.fanchen.imovie.entity.k8dy;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideoBanner;
import com.fanchen.imovie.retrofit.service.K8dyService;

/**
 * Created by fanchen on 2017/11/9.
 */
public class K8dyBanner implements Parcelable, IVideoBanner<K8dyBanner> {
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

    public K8dyBanner(){
    }

    protected K8dyBanner(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
    }

    public static final Creator<K8dyBanner> CREATOR = new Creator<K8dyBanner>() {
        @Override
        public K8dyBanner createFromParcel(Parcel in) {
            return new K8dyBanner(in);
        }

        @Override
        public K8dyBanner[] newArray(int size) {
            return new K8dyBanner[size];
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
        return K8dyService.class.getName();
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
    public K8dyBanner getData() {
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
