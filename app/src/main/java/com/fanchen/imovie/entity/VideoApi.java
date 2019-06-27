package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * 重构后的吐槽C video实体
 * Created by fanchen on 2017/9/20.
 */
public class VideoApi implements Parcelable ,IVideo {
    private String hid;
    private String typeid;
    private String create;
    private String mukio;
    private String typename;
    private String title;
    private String play;
    private String description;
    private String keywords;
    private String thumb;
    private String user;
    private String userid;
    private String part;
    private int drawable;
    private List<VideoApiVid> video;
    private String serviceClass;
    private int source;
    public String thisClass = VideoApi.class.getName();

    public VideoApi(){
    }

    protected VideoApi(Parcel in) {
        hid = in.readString();
        typeid = in.readString();
        create = in.readString();
        mukio = in.readString();
        typename = in.readString();
        title = in.readString();
        play = in.readString();
        description = in.readString();
        keywords = in.readString();
        thumb = in.readString();
        user = in.readString();
        userid = in.readString();
        part = in.readString();
        video = in.createTypedArrayList(VideoApiVid.CREATOR);
        drawable = in.readInt();
        serviceClass = in.readString();
        source = in.readInt();
    }

    public static final Creator<VideoApi> CREATOR = new Creator<VideoApi>() {
        @Override
        public VideoApi createFromParcel(Parcel in) {
            return new VideoApi(in);
        }

        @Override
        public VideoApi[] newArray(int size) {
            return new VideoApi[size];
        }
    };

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getMukio() {
        return mukio;
    }

    public void setMukio(String mukio) {
        this.mukio = mukio;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getCover() {
        return getThumb();
    }

    @Override
    public String getId() {
        return getHid();
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public String getServiceClass() {
        return serviceClass;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getThumb() {
        return thumb != null && thumb.startsWith("http") ? thumb : "http:" + thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public List<VideoApiVid> getVideo() {
        return video;
    }

    public void setVideo(List<VideoApiVid> video) {
        this.video = video;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
    @Override
    public boolean hasVideoDetails() {
        return true;
    }

    @Override
    public String getCoverReferer() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hid);
        dest.writeString(typeid);
        dest.writeString(create);
        dest.writeString(mukio);
        dest.writeString(typename);
        dest.writeString(title);
        dest.writeString(play);
        dest.writeString(description);
        dest.writeString(keywords);
        dest.writeString(thumb);
        dest.writeString(user);
        dest.writeString(userid);
        dest.writeString(part);
        dest.writeTypedList(video);
        dest.writeInt(drawable);
        dest.writeString(serviceClass);
        dest.writeInt(source);
    }

    @Override
    public String getLast() {
        return getUser();
    }

    @Override
    public String getExtras() {
        return getPlay();
    }

    @Override
    public String getDanmaku() {
        return getMukio();
    }

    @Override
    public int getDrawable() {
        return drawable;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }
}
