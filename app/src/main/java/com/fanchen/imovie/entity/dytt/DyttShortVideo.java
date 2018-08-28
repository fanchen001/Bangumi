package com.fanchen.imovie.entity.dytt;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IViewType;

/**
 * Created by fanchen on 2017/9/22.
 */
public class DyttShortVideo implements Parcelable,IViewType{
    private String banner;
    private String title;
    private String url;
    private int commentCount;
    private String playurl;
    private String cover;
    private String duration;
    private String playCount;
    private DyttWemedia wemedia;
    private int adsupport;
    private int render;

    public DyttShortVideo(){
    }

    protected DyttShortVideo(Parcel in) {
        banner = in.readString();
        title = in.readString();
        url = in.readString();
        commentCount = in.readInt();
        playurl = in.readString();
        cover = in.readString();
        duration = in.readString();
        playCount = in.readString();
        wemedia = in.readParcelable(DyttWemedia.class.getClassLoader());
        adsupport = in.readInt();
        render = in.readInt();
    }

    public static final Creator<DyttShortVideo> CREATOR = new Creator<DyttShortVideo>() {
        @Override
        public DyttShortVideo createFromParcel(Parcel in) {
            return new DyttShortVideo(in);
        }

        @Override
        public DyttShortVideo[] newArray(int size) {
            return new DyttShortVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(banner);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeInt(commentCount);
        dest.writeString(playurl);
        dest.writeString(cover);
        dest.writeString(duration);
        dest.writeString(playCount);
        dest.writeParcelable(wemedia, flags);
        dest.writeInt(adsupport);
        dest.writeInt(render);
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getPlayurl() {
        return playurl == null ? "" : playurl.replace(".mp4","");
    }

    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }

    public String getCover() {
        return cover != null && cover.startsWith("http") ? cover : "http:" + cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public DyttWemedia getWemedia() {
        return wemedia;
    }

    public void setWemedia(DyttWemedia wemedia) {
        this.wemedia = wemedia;
    }

    public int getAdsupport() {
        return adsupport;
    }

    public void setAdsupport(int adsupport) {
        this.adsupport = adsupport;
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }
}
