package com.fanchen.imovie.entity.jren;

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
public class JrenDetails extends JrenVideo implements IVideoDetails{
    private boolean success;
    private String message;
    private String introduce;
    private List<JrenVideo> recoms;
    private List<JrenEpisode> episodes;

    public JrenDetails(){
    }


    protected JrenDetails(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        recoms = in.createTypedArrayList(JrenVideo.CREATOR);
        episodes = in.createTypedArrayList(JrenEpisode.CREATOR);
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

    public static final Creator<JrenDetails> CREATOR = new Creator<JrenDetails>() {
        @Override
        public JrenDetails createFromParcel(Parcel in) {
            return new JrenDetails(in);
        }

        @Override
        public JrenDetails[] newArray(int size) {
            return new JrenDetails[size];
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
        if(recoms != null && recoms.size() > 6)
            return recoms.subList(0,6);
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

    public void setRecoms(List<JrenVideo> recoms) {
        this.recoms = recoms;
    }

    public void setEpisodes(List<JrenEpisode> episodes) {
        this.episodes = episodes;
    }
}
