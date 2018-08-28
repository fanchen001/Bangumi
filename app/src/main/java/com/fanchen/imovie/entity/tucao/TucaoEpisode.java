package com.fanchen.imovie.entity.tucao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IPlayUrls;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.retrofit.service.TucaoService;

/**
 * Created by fanchen on 2017/9/28.
 */
public class TucaoEpisode implements IVideoEpisode,Parcelable{
    private String title;
    private String url;
    private String id;
    private int type;
    private int state;
    private String extend;

    public TucaoEpisode(){
    }

    protected TucaoEpisode(Parcel in) {
        title = in.readString();
        url = in.readString();
        id = in.readString();
        type = in.readInt();
        extend = in.readString();
        state = in.readInt();
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public static final Creator<TucaoEpisode> CREATOR = new Creator<TucaoEpisode>() {
        @Override
        public TucaoEpisode createFromParcel(Parcel in) {
            return new TucaoEpisode(in);
        }

        @Override
        public TucaoEpisode[] newArray(int size) {
            return new TucaoEpisode[size];
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
        return getExtend();
    }

    @Override
    public int getSource() {
        return 0;
    }

    @Override
    public int getPlayerType() {
        return type;
    }

    @Override
    public String getExtend() {
        return String.format("http://api.tucao.tv/api/playurl?type=%s&vid=%s&key=tucao7b2b5650.cc",extend,id);
    }

    @Override
    public String getServiceClassName() {
        return TucaoService.class.getName();
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
    public IPlayUrls toPlayUrls(int palyType, int urlType) {
        return new TucaoPlayUrls();
    }

    @Override
    public int getViewType() {
        return 0;
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

    public void setPlayType(int type) {
        this.type = type;
    }
}
