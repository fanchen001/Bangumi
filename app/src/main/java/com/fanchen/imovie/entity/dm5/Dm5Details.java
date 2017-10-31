package com.fanchen.imovie.entity.dm5;

import android.os.Parcel;
import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.bmob.VideoCollect;

import java.util.List;

/**
 * Created by fanchen on 2017/10/2.
 */
public class Dm5Details extends Dm5Video implements IVideoDetails {
    private boolean success;
    private String message;
    private String introduce;
    private List<Dm5Episode> episodes;

    public Dm5Details(){
    }

    protected Dm5Details(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        episodes = in.createTypedArrayList(Dm5Episode.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
        dest.writeString(introduce);
        dest.writeTypedList(episodes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Dm5Details> CREATOR = new Creator<Dm5Details>() {
        @Override
        public Dm5Details createFromParcel(Parcel in) {
            return new Dm5Details(in);
        }

        @Override
        public Dm5Details[] newArray(int size) {
            return new Dm5Details[size];
        }
    };

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setEpisodes(List<Dm5Episode> episodes) {
        this.episodes = episodes;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getIntroduce() {
        return TextUtils.isEmpty(introduce) ? "暂无简介" : introduce;
    }

    @Override
    public List<? extends IVideoEpisode> getEpisodes() {
        return episodes;
    }

    @Override
    public List<? extends IVideo> getRecoms() {
        return null;
    }

    @Override
    public IVideoDetails setVideo(IVideo video) {
        setCover(video.getCover());
        setTitle(video.getTitle());
        setId(video.getId());
        setUrl(video.getUrl());
        setExtras(video.getExtras());
        return this;
    }

    @Override
    public IVideoDetails setVideo(VideoCollect video) {
        setCover(video.getCover());
        setTitle(video.getTitle());
        setId(video.getId());
        setUrl(video.getUrl());
        setExtras(video.getExtras());
        return this;
    }

    @Override
    public boolean canDownload() {
        return true;
    }

}
