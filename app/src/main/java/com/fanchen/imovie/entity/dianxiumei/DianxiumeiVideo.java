package com.fanchen.imovie.entity.dianxiumei;

import android.os.Parcel;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.DianxiumeiService;

/**
 * Created by fanchen on 2017/10/13.
 */
public class DianxiumeiVideo implements IVideo{

    private String title;
    private String cover;
    private String id;
    private String url;
    private String info;
    private String topInfo;
    private String extras;

    public DianxiumeiVideo(){
    }

    protected DianxiumeiVideo(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        info = in.readString();
        topInfo = in.readString();
        extras = in.readString();
    }

    public static final Creator<DianxiumeiVideo> CREATOR = new Creator<DianxiumeiVideo>() {
        @Override
        public DianxiumeiVideo createFromParcel(Parcel in) {
            return new DianxiumeiVideo(in);
        }

        @Override
        public DianxiumeiVideo[] newArray(int size) {
            return new DianxiumeiVideo[size];
        }
    };

    @Override
    public boolean hasVideoDetails() {
        return false;
    }

    @Override
    public String getLast() {
        return info;
    }

    @Override
    public String getExtras() {
        return extras;
    }

    @Override
    public String getDanmaku() {
        return topInfo;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getServiceClassName() {
        return DianxiumeiService.class.getName();
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
    public String getId() {
        return id;
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
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
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
        dest.writeString(info);
        dest.writeString(topInfo);
        dest.writeString(extras);
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

    public void setInfo(String info) {
        this.info = info;
    }

    public void setTopInfo(String topInfo) {
        this.topInfo = topInfo;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

}
