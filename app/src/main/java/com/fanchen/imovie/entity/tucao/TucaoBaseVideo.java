package com.fanchen.imovie.entity.tucao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.TucaoService;

/**
 * tucaoc 视频item基础类
 * Created by fanchen on 2017/9/16.
 */
public class TucaoBaseVideo implements IBaseVideo, Parcelable,IViewType {
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


    public TucaoBaseVideo() {

    }

    protected TucaoBaseVideo(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
    }

    public static final Creator<TucaoBaseVideo> CREATOR = new Creator<TucaoBaseVideo>() {
        @Override
        public TucaoBaseVideo createFromParcel(Parcel in) {
            return new TucaoBaseVideo(in);
        }

        @Override
        public TucaoBaseVideo[] newArray(int size) {
            return new TucaoBaseVideo[size];
        }
    };

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

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCover() {
        return cover != null && cover.startsWith("http") ? cover : "http:" + cover;
    }

    @Override
    public String getId() {
        return id == null ? "" : id.replace("h","");
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getSource() {
        return 0;
    }

    @Override
    public String getServiceClass() {
        return TucaoService.class.getName();
    }

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public String getCoverReferer() {
        return null;
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
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }
}
