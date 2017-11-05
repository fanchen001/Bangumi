package com.fanchen.imovie.entity.xiaokanba;

import android.os.Parcel;

import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.XiaokanbaService;

/**
 * Created by fanchen on 2017/10/16.
 */
public class XiaokanbaEpisode implements IVideoEpisode{

    private String title;
    private String url;
    private String id;
    private int state;

    public XiaokanbaEpisode(){
    }

    protected XiaokanbaEpisode(Parcel in) {
        title = in.readString();
        url = in.readString();
        id = in.readString();
        state = in.readInt();
    }

    public static final Creator<XiaokanbaEpisode> CREATOR = new Creator<XiaokanbaEpisode>() {
        @Override
        public XiaokanbaEpisode createFromParcel(Parcel in) {
            return new XiaokanbaEpisode(in);
        }

        @Override
        public XiaokanbaEpisode[] newArray(int size) {
            return new XiaokanbaEpisode[size];
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
        return null;
    }

    @Override
    public String getServiceClassName() {
        return XiaokanbaService.class.getName();
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
        dest.writeString(url);
        dest.writeString(id);
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
}
