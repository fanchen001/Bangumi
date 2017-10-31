package com.fanchen.imovie.entity.acg;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * Created by fanchen on 2017/7/23.
 */
public class AcgPosts implements Parcelable,IViewType {
    private int id;

    private String title;

    private String url;

    private AcgThumbnail thumbnail;

    private int comment;

    private List<AcgCats> cats ;

    private AcgDate date;

    private String excerpt;

    private AcgAuthor author;

    private int views;

    public AcgPosts(){

    }

    protected AcgPosts(Parcel in) {
        id = in.readInt();
        title = in.readString();
        url = in.readString();
        thumbnail = in.readParcelable(AcgThumbnail.class.getClassLoader());
        comment = in.readInt();
        cats = in.createTypedArrayList(AcgCats.CREATOR);
        date = in.readParcelable(AcgDate.class.getClassLoader());
        excerpt = in.readString();
        author = in.readParcelable(AcgAuthor.class.getClassLoader());
        views = in.readInt();
    }

    public static final Creator<AcgPosts> CREATOR = new Creator<AcgPosts>() {
        @Override
        public AcgPosts createFromParcel(Parcel in) {
            return new AcgPosts(in);
        }

        @Override
        public AcgPosts[] newArray(int size) {
            return new AcgPosts[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public AcgThumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(AcgThumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public List<AcgCats> getCats() {
        return cats;
    }

    public void setCats(List<AcgCats> cats) {
        this.cats = cats;
    }

    public AcgDate getDate() {
        return date;
    }

    public void setDate(AcgDate date) {
        this.date = date;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public AcgAuthor getAuthor() {
        return author;
    }

    public void setAuthor(AcgAuthor author) {
        this.author = author;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeParcelable(thumbnail, flags);
        dest.writeInt(comment);
        dest.writeTypedList(cats);
        dest.writeParcelable(date, flags);
        dest.writeString(excerpt);
        dest.writeParcelable(author, flags);
        dest.writeInt(views);
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }
}
