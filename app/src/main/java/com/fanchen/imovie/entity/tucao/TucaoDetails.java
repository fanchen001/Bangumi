package com.fanchen.imovie.entity.tucao;


import android.os.Parcel;
import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.bmob.VideoCollect;

import java.lang.ref.SoftReference;
import java.util.List;

/**
 * TucaoDetails
 * Created by fanchen on 2017/9/28.
 */
public class TucaoDetails extends TucaoVideo implements IVideoDetails{

    private static SoftReference<List<TucaoEpisode>> softEpisodes;

    private boolean success;
    private String message;
    private String introduce;
    private List<TucaoEpisode> episodes;

    public TucaoDetails(){
    }

    protected TucaoDetails(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        if(softEpisodes != null){
            final List<?> videoEpisodes = softEpisodes.get();
            if(videoEpisodes != null && videoEpisodes.size() > 0 && videoEpisodes.get(0) instanceof  TucaoEpisode){
                episodes = (List<TucaoEpisode>) videoEpisodes;
            }
            softEpisodes = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
        dest.writeString(introduce);
        softEpisodes = new SoftReference<>(episodes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TucaoDetails> CREATOR = new Creator<TucaoDetails>() {
        @Override
        public TucaoDetails createFromParcel(Parcel in) {
            return new TucaoDetails(in);
        }

        @Override
        public TucaoDetails[] newArray(int size) {
            return new TucaoDetails[size];
        }
    };

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
        return this;
    }

    @Override
    public IVideoDetails setVideo(VideoCollect video) {
        return this;
    }

    @Override
    public boolean canDownload() {
        return episodes == null || episodes.size() == 0 ? false : episodes.get(0).getPlayerType() == IVideoEpisode.PLAY_TYPE_VIDEO;
    }


    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
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

    public void setEpisodes(List<TucaoEpisode> episodes) {
        this.episodes = episodes;
    }

}
