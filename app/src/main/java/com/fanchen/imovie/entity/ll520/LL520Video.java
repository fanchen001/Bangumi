package com.fanchen.imovie.entity.ll520;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.LoLi520Service;

/**
 * Created by fanchen on 2017/9/24.
 */
public class LL520Video implements IVideo,Parcelable{
    /*** 标题**/
    private String title;
    /*** 图片**/
    private String cover;
    /*** id**/
    private String id;
    /*** url**/
    private String url;
    private String author;
    private String clazz;
    private String type;
    public String thisClass = LL520Video.class.getName();

    public LL520Video(){
    }

    protected LL520Video(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        author = in.readString();
        clazz = in.readString();
        type = in.readString();
        thisClass = in.readString();
    }

    public static final Creator<LL520Video> CREATOR = new Creator<LL520Video>() {
        @Override
        public LL520Video createFromParcel(Parcel in) {
            return new LL520Video(in);
        }

        @Override
        public LL520Video[] newArray(int size) {
            return new LL520Video[size];
        }
    };

    @Override
    public String getLast() {
        return clazz;
    }

    @Override
    public String getExtras() {
        return type;
    }

    @Override
    public String getDanmaku() {
        return author;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getServiceClassName() {
        return LoLi520Service.class.getName();
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
        return getUrl();
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
        dest.writeString(author);
        dest.writeString(clazz);
        dest.writeString(type);
        dest.writeString(thisClass);
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setType(String type) {
        this.type = type;
    }
}
