package com.fanchen.imovie.entity.a4dy;

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
public class A4dyDetails extends A4dyVideo implements IVideoDetails{

    private boolean success;
    private String message;
    private String introduce;
    private List<A4dyVideo> recom;
    private List<A4dyEpisode> episodes;

    public A4dyDetails(){
    }

    protected A4dyDetails(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        episodes = in.createTypedArrayList(A4dyEpisode.CREATOR);
        recom = in.createTypedArrayList(A4dyVideo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
        dest.writeString(introduce);
        dest.writeTypedList(episodes);
        dest.writeTypedList(recom);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<A4dyDetails> CREATOR = new Creator<A4dyDetails>() {
        @Override
        public A4dyDetails createFromParcel(Parcel in) {
            return new A4dyDetails(in);
        }

        @Override
        public A4dyDetails[] newArray(int size) {
            return new A4dyDetails[size];
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
        return recom;
    }

    @Override
    public IVideoDetails setVideo(IVideo video) {
        setId(video.getId());
        setUrl(video.getUrl());
        return this;
    }

    @Override
    public IVideoDetails setVideo(VideoCollect video) {
        setId(video.getId());
        setUrl(video.getUrl());
        return this;
    }

    @Override
    public boolean canDownload() {
        return true;
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

    public void setEpisodes(List<A4dyEpisode> episodes) {
        this.episodes = episodes;
    }

    public void setRecom(List<A4dyVideo> recom) {
        this.recom = recom;
    }
}
