package com.fanchen.imovie.entity.dytt;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IViewType;

/**
 * DyttBody
 * Created by fanchen on 2017/8/2.
 */
public class DyttBody implements Parcelable, IViewType {
    private int publishTime;
    private double score;
    private String doubanId;
    private String img;
    private String movieTypeName;
    private boolean album;
    private String name;
    private int movieId;
    private String status;
    private String lastUpdateTime;

    public DyttBody() {

    }

    protected DyttBody(Parcel in) {
        publishTime = in.readInt();
        score = in.readDouble();
        doubanId = in.readString();
        img = in.readString();
        movieTypeName = in.readString();
        album = in.readByte() != 0;
        name = in.readString();
        movieId = in.readInt();
        status = in.readString();
        lastUpdateTime = in.readString();
    }

    public int getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(int publishTime) {
        this.publishTime = publishTime;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getDoubanId() {
        return doubanId;
    }

    public void setDoubanId(String doubanId) {
        this.doubanId = doubanId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMovieTypeName() {
        return movieTypeName;
    }

    public void setMovieTypeName(String movieTypeName) {
        this.movieTypeName = movieTypeName;
    }

    public boolean isAlbum() {
        return album;
    }

    public void setAlbum(boolean album) {
        this.album = album;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public static final Creator<DyttBody> CREATOR = new Creator<DyttBody>() {
        @Override
        public DyttBody createFromParcel(Parcel in) {
            return new DyttBody(in);
        }

        @Override
        public DyttBody[] newArray(int size) {
            return new DyttBody[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(publishTime);
        dest.writeDouble(score);
        dest.writeString(doubanId);
        dest.writeString(img);
        dest.writeString(movieTypeName);
        dest.writeByte((byte) (album ? 1 : 0));
        dest.writeString(name);
        dest.writeInt(movieId);
        dest.writeString(status);
        dest.writeString(lastUpdateTime);
    }


    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }
}
