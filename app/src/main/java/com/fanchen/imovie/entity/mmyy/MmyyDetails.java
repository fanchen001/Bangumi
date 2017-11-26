package com.fanchen.imovie.entity.mmyy;

import android.os.Parcel;
import android.text.TextUtils;

import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;

import java.util.List;

/**
 * Created by fanchen on 2017/9/25.
 */
public class MmyyDetails extends MmyyVideo implements IVideoDetails{

    private boolean success;
    private String message;
    private String introduce;
    private List<MmyyVideo> recoms;
    private List<MmyyEpisode> episodes;

    public MmyyDetails(){
    }

    protected MmyyDetails(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        recoms = in.createTypedArrayList(MmyyVideo.CREATOR);
        episodes = in.createTypedArrayList(MmyyEpisode.CREATOR);
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

    public static final Creator<MmyyDetails> CREATOR = new Creator<MmyyDetails>() {
        @Override
        public MmyyDetails createFromParcel(Parcel in) {
            return new MmyyDetails(in);
        }

        @Override
        public MmyyDetails[] newArray(int size) {
            return new MmyyDetails[size];
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

    public void setRecoms(List<MmyyVideo> recoms) {
        this.recoms = recoms;
    }

    public void setEpisodes(List<MmyyEpisode> episodes) {
        this.episodes = episodes;
    }
}
