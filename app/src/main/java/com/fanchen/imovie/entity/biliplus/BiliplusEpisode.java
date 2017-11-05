package com.fanchen.imovie.entity.biliplus;

import android.os.Parcel;

import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.BiliplusService;

/**
 * Created by fanchen on 2017/10/12.
 */
public class BiliplusEpisode implements IVideoEpisode{

    private String title;
    private String id;
    private String url;
    private int state;
    private String extend;

    public BiliplusEpisode(){
    }

    protected BiliplusEpisode(Parcel in) {
        title = in.readString();
        id = in.readString();
        url = in.readString();
        state = in.readInt();
        extend = in.readString();
    }

    public static final Creator<BiliplusEpisode> CREATOR = new Creator<BiliplusEpisode>() {
        @Override
        public BiliplusEpisode createFromParcel(Parcel in) {
            return new BiliplusEpisode(in);
        }

        @Override
        public BiliplusEpisode[] newArray(int size) {
            return new BiliplusEpisode[size];
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
        return PLAY_TYPE_NOT;
    }

    @Override
    public String getExtend() {
        return extend;
    }

    @Override
    public String getServiceClassName() {
        return BiliplusService.class.getName();
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
    public void setFilePath(String path) {
        this.url = path;
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
        dest.writeString(id);
        dest.writeString(url);
        dest.writeInt(state);
        dest.writeString(extend);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
