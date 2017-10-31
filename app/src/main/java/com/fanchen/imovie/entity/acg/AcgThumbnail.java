package com.fanchen.imovie.entity.acg;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by fanchen on 2017/7/23.
 */
public class AcgThumbnail implements Parcelable {
    private String id;
    private String width;
    private String height;
    private String url;

    public AcgThumbnail() {

    }

    protected AcgThumbnail(Parcel in) {
        id = in.readString();
        width = in.readString();
        height = in.readString();
        url = in.readString();
    }

    public static final Creator<AcgThumbnail> CREATOR = new Creator<AcgThumbnail>() {
        @Override
        public AcgThumbnail createFromParcel(Parcel in) {
            return new AcgThumbnail(in);
        }

        @Override
        public AcgThumbnail[] newArray(int size) {
            return new AcgThumbnail[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWidth() {
        return this.width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeight() {
        return this.height;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(url);
    }
}
