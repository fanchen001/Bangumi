package com.fanchen.imovie.entity.biliplus;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.bmob.VideoCollect;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoDetails;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.BiliplusService;

import java.util.List;

/**
 * Created by fanchen on 2017/10/12.
 */
public class BiliplusDetails implements IVideoDetails,Parcelable{

    private String title;
    private String cover;
    private String id;
    private String url;
    private String introduce;
    private String last;
    private String extras;
    private String danmaku;
    private String message;
    private boolean success;
    private List<BiliplusEpisode> episodes;

    public BiliplusDetails(){
    }

    protected BiliplusDetails(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        introduce = in.readString();
        last = in.readString();
        extras = in.readString();
        danmaku = in.readString();
        message = in.readString();
        success = in.readByte() != 0;
        episodes = in.createTypedArrayList(BiliplusEpisode.CREATOR);
    }

    public static final Creator<BiliplusDetails> CREATOR = new Creator<BiliplusDetails>() {
        @Override
        public BiliplusDetails createFromParcel(Parcel in) {
            return new BiliplusDetails(in);
        }

        @Override
        public BiliplusDetails[] newArray(int size) {
            return new BiliplusDetails[size];
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
    public String getLast() {
        return last;
    }

    @Override
    public String getExtras() {
        return extras;
    }

    @Override
    public String getDanmaku() {
        return danmaku;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getServiceClassName() {
        return BiliplusService.class.getName();
    }

    @Override
    public boolean hasVideoDetails() {
        return true;
    }

    @Override
    public String getCoverReferer() {
        return null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCover() {
        return cover;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getSource() {
        return 0;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(introduce);
        dest.writeString(last);
        dest.writeString(extras);
        dest.writeString(danmaku);
        dest.writeString(message);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeTypedList(episodes);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public void setDanmaku(String danmaku) {
        this.danmaku = danmaku;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setEpisodes(List<BiliplusEpisode> episodes) {
        this.episodes = episodes;
    }
}
