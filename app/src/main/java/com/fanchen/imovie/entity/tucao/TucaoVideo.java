package com.fanchen.imovie.entity.tucao;


import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.retrofit.service.TucaoService;

/**
 * 吐槽C视频item
 * Created by fanchen on 2017/9/16.
 */
public class TucaoVideo extends TucaoBaseVideo implements IVideo, Parcelable {

    /**
     * 播放次数
     **/
    private String play;
    /**
     * up主
     **/
    private String up;
    /**
     * 弹幕数量
     **/
    private String danmaku;
    /**
     * 时间
     **/
    private String update;

    public String thisClass = TucaoVideo.class.getName();

    public TucaoVideo() {

    }

    protected TucaoVideo(Parcel in) {
        super(in);
        play = in.readString();
        up = in.readString();
        danmaku = in.readString();
        update = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(play);
        dest.writeString(up);
        dest.writeString(danmaku);
        dest.writeString(update);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TucaoVideo> CREATOR = new Creator<TucaoVideo>() {
        @Override
        public TucaoVideo createFromParcel(Parcel in) {
            return new TucaoVideo(in);
        }

        @Override
        public TucaoVideo[] newArray(int size) {
            return new TucaoVideo[size];
        }
    };

    @Override
    public String getLast() {
        return String.format("UP:%s", up);
    }

    @Override
    public String getExtras() {
        return String.format("播放:%s", play);
    }

    @Override
    public int getDrawable() {
        return 0;
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
    public int getViewType() {
        return TYPE_NORMAL;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setDanmaku(String danmaku) {
        this.danmaku = danmaku;
    }
    @Override
    public boolean hasVideoDetails() {
        return true;
    }

    @Override
    public String getDanmaku() {
        return String.format("弹幕:%s",danmaku);
    }
}
