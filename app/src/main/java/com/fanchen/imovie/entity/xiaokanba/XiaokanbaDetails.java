package com.fanchen.imovie.entity.xiaokanba;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;

import java.util.List;

/**
 * Created by fanchen on 2017/10/16.
 */
public class XiaokanbaDetails extends XiaokanbaVideo implements IVideoDetails,Parcelable{

    private boolean success;
    private String message;
    private String types;
    private String author;
    private String area;
    private String introduce;
    private List<XiaokanbaVideo> recoms;
    private List<XiaokanbaEpisode> episodes;

    public XiaokanbaDetails(){
    }


    protected XiaokanbaDetails(Parcel in) {
        super(in);
        success = in.readByte() != 0;
        message = in.readString();
        types = in.readString();
        author = in.readString();
        area = in.readString();
        introduce = in.readString();
        recoms = in.createTypedArrayList(XiaokanbaVideo.CREATOR);
        episodes = in.createTypedArrayList(XiaokanbaEpisode.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
        dest.writeString(types);
        dest.writeString(author);
        dest.writeString(area);
        dest.writeString(introduce);
        dest.writeTypedList(recoms);
        dest.writeTypedList(episodes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<XiaokanbaDetails> CREATOR = new Creator<XiaokanbaDetails>() {
        @Override
        public XiaokanbaDetails createFromParcel(Parcel in) {
            return new XiaokanbaDetails(in);
        }

        @Override
        public XiaokanbaDetails[] newArray(int size) {
            return new XiaokanbaDetails[size];
        }
    };

    @Override
    public String getIntroduce() {
        return introduce;
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
        setUrl(video.getUrl());
        setId(video.getId());
        setCover(video.getCover());
        return this;
    }

    @Override
    public IVideoDetails setVideo(VideoCollect video) {
        setUrl(video.getUrl());
        setId(video.getId());
        setCover(video.getCover());
        return this;
    }

    @Override
    public boolean canDownload() {
        return true;
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
    public String getDanmaku() {
        return types;
    }

    @Override
    public String getExtras() {
        return area;
    }

    @Override
    public String getLast() {
        return author;
    }

    public void setEpisodes(List<XiaokanbaEpisode> episodes) {
        this.episodes = episodes;
    }

    public void setRecoms(List<XiaokanbaVideo> recoms) {
        this.recoms = recoms;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
