package com.fanchen.imovie.entity;


import android.os.Parcel;
import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IVideo;

/**
 * 重构后的video实体
 *
 * Created by fanchen on 2017/12/22.
 */
public class Video extends VideoBase implements IVideo {
    private String extras;//播放次数
    private String last;//up主
    private String danmaku;//弹幕数量
    private String update;//时间
    private boolean hasDetails;

    public String thisClass = Video.class.getName();

    public Video() {
    }

    protected Video(Parcel in) {
        super(in);
        extras = in.readString();
        last = in.readString();
        danmaku = in.readString();
        update = in.readString();
        hasDetails = in.readByte() != 0;
        thisClass = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(extras);
        dest.writeString(last);
        dest.writeString(danmaku);
        dest.writeString(update);
        dest.writeByte((byte) (hasDetails ? 1 : 0));
        dest.writeString(thisClass);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setAuthor(String author){
        danmaku = author;
    }

    public void setClazz(String clazz){
        last = clazz;
    }

    public void setType(String type){
        extras = type;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public String getLast() {
        return TextUtils.isEmpty(last) ? "" : last;
    }

    @Override
    public String getExtras() {
        return TextUtils.isEmpty(extras) ? "" : extras;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public int getViewType() {
        return TYPE_NORMAL;
    }

    @Override
    public boolean hasVideoDetails() {
        return hasDetails;
    }


    @Override
    public String getDanmaku() {
        return danmaku;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setDanmaku(String danmaku) {
        this.danmaku = danmaku;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setHasDetails(boolean hasDetails) {
        this.hasDetails = hasDetails;
    }

}
