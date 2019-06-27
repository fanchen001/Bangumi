package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IViewType;

/**
 *  重构后的视频item基础类
 * Created by fanchen on 2017/9/16.
 */
public class VideoBase implements IBaseVideo, Parcelable,IViewType {
    private String title;//标题
    private String cover;//图片
    private String id;//id
    private String url;//url
    private String host;//url
    private int source;
    private String serviceName;
    private boolean isAgent;
    private String urlReferer;//

    public VideoBase() {
    }

    protected VideoBase(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        source = in.readInt();
        host = in.readString();
        serviceName = in.readString();
        isAgent = in.readByte() != 0;
        urlReferer = in.readString();
    }

    public static final Creator<VideoBase> CREATOR = new Creator<VideoBase>() {
        @Override
        public VideoBase createFromParcel(Parcel in) {
            return new VideoBase(in);
        }

        @Override
        public VideoBase[] newArray(int size) {
            return new VideoBase[size];
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
        dest.writeInt(source);
        dest.writeString(host);
        dest.writeString(serviceName);
        dest.writeByte((byte) (isAgent ? 1 : 0));
        dest.writeString(urlReferer);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCover() {
        if(TextUtils.isEmpty(cover)){
           return  "https://avatars1.githubusercontent.com/u/13826873?s=460&v=4";
        }else if(cover.startsWith("//")){
            return "http:" + cover;
        }else if(cover.startsWith("/")){
            return host + cover;
        }else {
            return cover;
        }
    }

    @Override
    public String getId() {
        return id == null ? "" : id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public String getServiceClass() {
        return serviceName;
    }

    @Override
    public boolean isAgent() {
        return isAgent;
    }

    public void setAgent(boolean agent) {
        isAgent = agent;
    }

    public void setUrlReferer(String urlReferer) {
        this.urlReferer = urlReferer;
        setHost(urlReferer);
    }

    @Override
    public String getCoverReferer() {
        return urlReferer;
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

    public void setSource(int source) {
        this.source = source;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setServiceClass(String serviceName) {
        this.serviceName = serviceName;
    }
}
