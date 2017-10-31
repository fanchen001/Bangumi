package com.fanchen.imovie.entity.s80;

import android.os.Parcel;
import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.bmob.VideoCollect;

import java.util.List;

/**
 * Created by fanchen on 2017/9/25.
 */
public class S80Details extends S80Video implements IVideoDetails{

    private boolean success;
    private String message;
    private String introduce;
    private List<S80Video> recoms;
    private List<S80Episode> episodes;

    public S80Details(){
    }


    protected S80Details(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        recoms = in.createTypedArrayList(S80Video.CREATOR);
        episodes = in.createTypedArrayList(S80Episode.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
        dest.writeString(introduce);
        dest.writeTypedList(recoms);
        dest.writeTypedList(episodes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<S80Details> CREATOR = new Creator<S80Details>() {
        @Override
        public S80Details createFromParcel(Parcel in) {
            return new S80Details(in);
        }

        @Override
        public S80Details[] newArray(int size) {
            return new S80Details[size];
        }
    };

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
        return recoms;
    }

    @Override
    public IVideoDetails setVideo(IVideo video) {
        setId(video.getId());
        setExtras(video.getExtras());
        setUrl(video.getUrl());
        setTitle(video.getTitle());
        setGrade(video.getDanmaku());
        return this;
    }

    @Override
    public IVideoDetails setVideo(VideoCollect video) {
        setId(video.getId());
        setExtras(video.getExtras());
        setUrl(video.getUrl());
        setTitle(video.getTitle());
        setGrade(video.getDanmaku());
        return this;
    }

    @Override
    public boolean canDownload() {
        return false;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setRecoms(List<S80Video> recoms) {
        this.recoms = recoms;
    }

    public void setEpisodes(List<S80Episode> episodes) {
        this.episodes = episodes;
    }
}
