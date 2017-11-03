package com.fanchen.imovie.entity.dm5;

import android.os.Parcel;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.Dm5Service;

/**
 * Created by fanchen on 2017/10/2.
 */
public class Dm5Video implements IVideo{

    /*** 标题**/
    private String title;
    /*** 图片**/
    private String cover;
    /*** id**/
    private String id;
    /*** url**/
    private String url;

    private int drawable;

    private String extras;
    private String update;
    private String author;

    public String thisClass = Dm5Video.class.getName();

    public Dm5Video(){
    }

    protected Dm5Video(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        drawable = in.readInt();
        extras = in.readString();
        update = in.readString();
        author = in.readString();
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

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public static final Creator<Dm5Video> CREATOR = new Creator<Dm5Video>() {
        @Override
        public Dm5Video createFromParcel(Parcel in) {
            return new Dm5Video(in);
        }

        @Override
        public Dm5Video[] newArray(int size) {
            return new Dm5Video[size];
        }
    };

    @Override
    public String getLast() {
        return extras;
    }

    @Override
    public String getExtras() {
        return update;
    }

    @Override
    public String getDanmaku() {
        return author;
    }

    @Override
    public int getDrawable() {
        return drawable;
    }

    @Override
    public String getServiceClassName() {
        return Dm5Service.class.getName();
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
        dest.writeInt(drawable);
        dest.writeString(extras);
        dest.writeString(update);
        dest.writeString(author);
    }
}
