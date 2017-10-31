package com.fanchen.imovie.entity.s80;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.S80Service;

/**
 * Created by fanchen on 2017/9/25.
 */
public class S80Episode implements IVideoEpisode,Parcelable{

    private String title;
    private String url;
    private String id;
    private int state;

    public S80Episode(){
    }

    protected S80Episode(Parcel in) {
        title = in.readString();
        url = in.readString();
        id = in.readString();
        state = in.readInt();
    }



    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getSource() {
        return 0;
    }

    @Override
    public int getPlayerType() {
        return PLAY_TYPE_XUNLEI;
    }

    @Override
    public String getExtend() {
        return null;
    }

    @Override
    public String getServiceClassName() {
        return S80Service.class.getName();
    }

    @Override
    public int getDownloadState() {
        return state;
    }

    @Override
    public void setDownloadState(int state) {
        this.state =state;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(id);
        dest.writeInt(state);
    }

    public static final Creator<S80Episode> CREATOR = new Creator<S80Episode>() {
        @Override
        public S80Episode createFromParcel(Parcel in) {
            return new S80Episode(in);
        }

        @Override
        public S80Episode[] newArray(int size) {
            return new S80Episode[size];
        }
    };
}
