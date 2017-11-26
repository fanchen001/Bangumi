package com.fanchen.imovie.entity.ll520;

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
public class LL520Details extends LL520Video implements IVideoDetails{

    private boolean success;
    private String message;
    private String introduce;
    private List<LL520Video> recoms;
    private List<LL520Episode> episodes;

    public LL520Details(){
    }

    protected LL520Details(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        recoms = in.createTypedArrayList(LL520Video.CREATOR);
        episodes = in.createTypedArrayList(LL520Episode.CREATOR);
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

    public static final Creator<LL520Details> CREATOR = new Creator<LL520Details>() {
        @Override
        public LL520Details createFromParcel(Parcel in) {
            return new LL520Details(in);
        }

        @Override
        public LL520Details[] newArray(int size) {
            return new LL520Details[size];
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

    public void setRecoms(List<LL520Video> recoms) {
        this.recoms = recoms;
    }

    public void setEpisodes(List<LL520Episode> episodes) {
        this.episodes = episodes;
    }
}
