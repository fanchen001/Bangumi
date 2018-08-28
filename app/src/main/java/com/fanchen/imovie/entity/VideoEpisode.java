package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.retrofit.RetrofitManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 重构后的 视频集数 实体
 * Created by fanchen on 2017/9/28.
 */
public class VideoEpisode implements IVideoEpisode, Parcelable {
    private String title;
    private String url;
    private String id;
    private int type = PLAY_TYPE_URL;
    private int state;
    private String serviceClass;
    private int source;
    private String extend;

    public VideoEpisode() {
    }

    protected VideoEpisode(Parcel in) {
        title = in.readString();
        url = in.readString();
        id = in.readString();
        type = in.readInt();
        extend = in.readString();
        state = in.readInt();
        serviceClass = in.readString();
        source = in.readInt();
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public static final Creator<VideoEpisode> CREATOR = new Creator<VideoEpisode>() {
        @Override
        public VideoEpisode createFromParcel(Parcel in) {
            return new VideoEpisode(in);
        }

        @Override
        public VideoEpisode[] newArray(int size) {
            return new VideoEpisode[size];
        }
    };

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public int getPlayerType() {
        return type;
    }

    @Override
    public String getExtend() {
        return extend;
    }

    @Override
    public String getServiceClassName() {
        return serviceClass;
    }

    @Override
    public int getDownloadState() {
        return state;
    }

    @Override
    public void setDownloadState(int state) {
        this.state = state;
    }

    @Override
    public void setFilePath(String path) {
        this.url = path;
    }

    @Override
    public IPlayUrls toPlayUrls(int palyType,int urlType) {
        VideoPlayUrls iPlayUrls = new VideoPlayUrls();
        Map<String,String> map = new HashMap<>();
        map.put("普通",getUrl());
        iPlayUrls.setReferer(RetrofitManager.REQUEST_URL);
        iPlayUrls.setPlayType(palyType);
        iPlayUrls.setUrlType(urlType);
        iPlayUrls.setSuccess(true);
        iPlayUrls.setUrls(map);
        return iPlayUrls;
    }

    @Override
    public int getViewType() {
        return TYPE_NORMAL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(id);
        dest.writeInt(type);
        dest.writeString(extend);
        dest.writeInt(state);
        dest.writeString(serviceClass);
        dest.writeInt(source);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPlayType(int type) {
        this.type = type;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public void setType(int type) {
        this.type = type;
    }
}
