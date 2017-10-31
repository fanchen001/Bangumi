package com.fanchen.imovie.entity.s80;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.S80Service;

/**
 * Created by fanchen on 2017/9/23.
 */
public class S80Video implements IVideo,Parcelable{

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

    /**
     * 播放次数
     **/
    private String play;
    /**
     * 护加信息
     **/
    private String extras;
    /**
     * 平分数
     **/
    private String grade;
    /**
     * 时间
     **/
    private String update;

    public String thisClass = S80Video.class.getName();

    public S80Video(){
    }

    protected S80Video(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        play = in.readString();
        extras = in.readString();
        grade = in.readString();
        update = in.readString();
    }

    public static final Creator<S80Video> CREATOR = new Creator<S80Video>() {
        @Override
        public S80Video createFromParcel(Parcel in) {
            return new S80Video(in);
        }

        @Override
        public S80Video[] newArray(int size) {
            return new S80Video[size];
        }
    };

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

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    @Override
    public String getLast() {
        return null;
    }

    @Override
    public String getExtras() {
        return extras;
    }

    @Override
    public String getDanmaku() {
        return grade;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getServiceClassName() {
        return S80Service.class.getName();
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
        return VIDEO_S80;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }
    @Override
    public boolean hasVideoDetails() {
        return true;
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
        dest.writeString(play);
        dest.writeString(extras);
        dest.writeString(grade);
        dest.writeString(update);
    }
}
