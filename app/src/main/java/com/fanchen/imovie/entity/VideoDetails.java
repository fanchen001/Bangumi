package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.bmob.VideoCollect;

import java.lang.ref.SoftReference;
import java.util.List;

/**
 * VideoDetails
 * Created by fanchen on 2017/9/28.
 */
public class VideoDetails extends Video implements IVideoDetails {
    private static SoftReference<List<VideoEpisode>> softEpisodes;
    private static SoftReference<List<Video>> softRecomm;

    private boolean success;
    private boolean canDownload;
    private String message;
    private String introduce;
    private List<VideoEpisode> episodes;
    private List<Video> recomm;

    public VideoDetails() {
    }

    protected VideoDetails(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        canDownload = in.readByte() != 0;
        message = in.readString();
        introduce = in.readString();
        if (softEpisodes != null) {
            final List<?> videoEpisodes = softEpisodes.get();
            if (videoEpisodes != null && videoEpisodes.size() > 0 && videoEpisodes.get(0) instanceof VideoEpisode) {
                episodes = (List<VideoEpisode>) videoEpisodes;
            }
            softEpisodes = null;
        }
        if (softRecomm != null) {
            final List<?> videoRecomm = softRecomm.get();
            if (videoRecomm != null && videoRecomm.size() > 0 && videoRecomm.get(0) instanceof Video) {
                recomm = (List<Video>) videoRecomm;
            }
            softRecomm = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeByte((byte) (canDownload ? 1 : 0));
        dest.writeString(message);
        dest.writeString(introduce);
        softEpisodes = new SoftReference<>(episodes);
        softRecomm = new SoftReference<>(recomm);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoDetails> CREATOR = new Creator<VideoDetails>() {
        @Override
        public VideoDetails createFromParcel(Parcel in) {
            return new VideoDetails(in);
        }

        @Override
        public VideoDetails[] newArray(int size) {
            return new VideoDetails[size];
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
        return recomm;
    }

    @Override
    public IVideoDetails setVideo(IVideo video) {
        if ("https://avatars1.githubusercontent.com/u/13826873?s=460&v=4".equals(getCover()) || TextUtils.isEmpty(getCover()))
            setCover(video.getCover());
        if (TextUtils.isEmpty(getTitle()))
            setTitle(video.getTitle());
        if (TextUtils.isEmpty(getId()))
            setId(video.getId());
        if (TextUtils.isEmpty(getExtras()))
            setExtras(video.getExtras());
        if (TextUtils.isEmpty(getUrl()))
            setUrl(video.getUrl());
        return this;
    }

    @Override
    public IVideoDetails setVideo(VideoCollect video) {
        return this;
    }

    @Override
    public boolean canDownload() {
        return true;
//        return episodes == null || episodes.size() == 0 ? false : canDownload;
    }

    @Override
    public String getServiceClass() {
        String serviceClass = super.getServiceClass();
        if (TextUtils.isEmpty(serviceClass)) {
            List<? extends IVideo> recoms = getRecoms();
            if (recoms != null && !recoms.isEmpty())
                return recoms.get(0).getServiceClass();
            List<? extends IVideoEpisode> episodes = getEpisodes();
            if (episodes != null && !episodes.isEmpty())
                return episodes.get(0).getServiceClassName();
        }
        return serviceClass;
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

    public void setEpisodes(List<VideoEpisode> episodes) {
        this.episodes = episodes;
    }

    public void setRecomm(List<Video> recomm) {
        this.recomm = recomm;
    }

    public void setCanDownload(boolean canDownload) {
        this.canDownload = canDownload;
    }
}
