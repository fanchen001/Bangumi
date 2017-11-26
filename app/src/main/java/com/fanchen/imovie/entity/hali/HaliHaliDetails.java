package com.fanchen.imovie.entity.hali;

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
public class HaliHaliDetails extends HaliHaliVideo implements IVideoDetails{

    private boolean success;
    private String message;
    private String introduce;
    private List<HaliHaliVideo> recoms;
    private List<HaliHaliEpisode> episodes;

    public HaliHaliDetails(){
    }

    protected HaliHaliDetails(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        recoms = in.createTypedArrayList(HaliHaliVideo.CREATOR);
        episodes = in.createTypedArrayList(HaliHaliEpisode.CREATOR);
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

    public static final Creator<HaliHaliDetails> CREATOR = new Creator<HaliHaliDetails>() {
        @Override
        public HaliHaliDetails createFromParcel(Parcel in) {
            return new HaliHaliDetails(in);
        }

        @Override
        public HaliHaliDetails[] newArray(int size) {
            return new HaliHaliDetails[size];
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

    public void setRecoms(List<HaliHaliVideo> recoms) {
        this.recoms = recoms;
    }

    public void setEpisodes(List<HaliHaliEpisode> episodes) {
        this.episodes = episodes;
    }
}
