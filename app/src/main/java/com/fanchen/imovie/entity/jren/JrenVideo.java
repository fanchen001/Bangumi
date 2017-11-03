package com.fanchen.imovie.entity.jren;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.JrenService;

/**
 * Created by fanchen on 2017/9/24.
 */
public class JrenVideo implements IVideo,Parcelable{

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
    /****作者****/
    private String up;
    public String thisClass = JrenVideo.class.getName();

    public JrenVideo(){
    }

    protected JrenVideo(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        extras = in.readString();
        up = in.readString();
    }

    public static final Creator<JrenVideo> CREATOR = new Creator<JrenVideo>() {
        @Override
        public JrenVideo createFromParcel(Parcel in) {
            return new JrenVideo(in);
        }

        @Override
        public JrenVideo[] newArray(int size) {
            return new JrenVideo[size];
        }
    };

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public void setUp(String up) {
        this.up = up;
    }

    @Override
    public String getLast() {
        return up;
    }

    @Override
    public String getExtras() {
        return extras;
    }

    @Override
    public String getDanmaku() {
        return up;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getServiceClassName() {
        return JrenService.class.getName();
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
        return VIDEO_JREN;
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
        dest.writeString(extras);
        dest.writeString(up);
    }
}
