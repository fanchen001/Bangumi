package com.fanchen.imovie.entity.dm5;

import android.os.Parcel;

import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.Dm5Service;

/**
 * Created by fanchen on 2017/10/2.
 */
public class Dm5Episode implements IVideoEpisode{

    private String title;
    private String url;
    private String id;
    private int type;
    private String extend;
    private int state;

    public Dm5Episode(){
    }

    protected Dm5Episode(Parcel in) {
        title = in.readString();
        url = in.readString();
        id = in.readString();
        type = in.readInt();
        extend = in.readString();
        state = in.readInt();
    }

    public static final Creator<Dm5Episode> CREATOR = new Creator<Dm5Episode>() {
        @Override
        public Dm5Episode createFromParcel(Parcel in) {
            return new Dm5Episode(in);
        }

        @Override
        public Dm5Episode[] newArray(int size) {
            return new Dm5Episode[size];
        }
    };

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
        return PLAY_TYPE_URL;
    }

    @Override
    public String getExtend() {
        return extend;
    }

    @Override
    public String getServiceClassName() {
        return Dm5Service.class.getName();
    }

    @Override
    public int getDownloadState() {
        return state;
    }

    @Override
    public void setDownloadState(int state) {
        this.state = state;
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
        dest.writeString(url);
        dest.writeString(id);
        dest.writeInt(type);
        dest.writeString(extend);
        dest.writeInt(state);
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

    public void setType(int type) {
        this.type = type;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
