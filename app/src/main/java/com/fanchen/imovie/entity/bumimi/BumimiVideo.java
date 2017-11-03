package com.fanchen.imovie.entity.bumimi;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.BumimiService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class BumimiVideo implements IVideo,Parcelable{
    /*** 标题**/
    private String title;
    /*** 图片**/
    private String cover;
    /*** id**/
    private String id;
    /*** url**/
    private String url;
    /*** 护加信息**/
    private String extras;
    private String score;
    private String p;
    public String thisClass = BumimiVideo.class.getName();

    public BumimiVideo(){
    }


    protected BumimiVideo(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        extras = in.readString();
        score = in.readString();
        p = in.readString();
    }

    public static final Creator<BumimiVideo> CREATOR = new Creator<BumimiVideo>() {
        @Override
        public BumimiVideo createFromParcel(Parcel in) {
            return new BumimiVideo(in);
        }

        @Override
        public BumimiVideo[] newArray(int size) {
            return new BumimiVideo[size];
        }
    };

    public void setScore(String score) {
        this.score = score;
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

    public void setP(String p) {
        this.p = p;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }


    @Override
    public String getLast() {
        return String.format("评分:%s",score);
    }

    @Override
    public String getExtras() {
        return p;
    }

    @Override
    public String getDanmaku() {
        return extras;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getServiceClassName() {
        return BumimiService.class.getName();
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
        return VIDEO_BUMIMI;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(extras);
        dest.writeString(score);
        dest.writeString(p);
    }
}
