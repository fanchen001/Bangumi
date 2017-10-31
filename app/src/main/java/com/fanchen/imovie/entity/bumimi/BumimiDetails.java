package com.fanchen.imovie.entity.bumimi;

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
public class BumimiDetails extends BumimiVideo implements IVideoDetails{

    private boolean success;
    private String message;
    private String introduce;
    private List<BumimiVideo> recoms;
    private List<BumimiEpisode> episodes;

    public BumimiDetails(){
    }

    protected BumimiDetails(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        recoms = in.createTypedArrayList(BumimiVideo.CREATOR);
        episodes = in.createTypedArrayList(BumimiEpisode.CREATOR);
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

    public static final Creator<BumimiDetails> CREATOR = new Creator<BumimiDetails>() {
        @Override
        public BumimiDetails createFromParcel(Parcel in) {
            return new BumimiDetails(in);
        }

        @Override
        public BumimiDetails[] newArray(int size) {
            return new BumimiDetails[size];
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
        setCover(video.getCover());
        setTitle(video.getTitle());
        setId(video.getId());
        setExtras(video.getExtras());
        setUrl(video.getUrl());
        return this;
    }

    @Override
    public IVideoDetails setVideo(VideoCollect video) {
        setCover(video.getCover());
        setTitle(video.getTitle());
        setId(video.getId());
        setExtras(video.getExtras());
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

    public void setRecoms(List<BumimiVideo> recoms) {
        this.recoms = recoms;
    }

    public void setEpisodes(List<BumimiEpisode> episodes) {
        this.episodes = episodes;
    }
}
