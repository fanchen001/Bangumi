package com.fanchen.imovie.entity.k8dy;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.K8dyService;

/**
 * Created by fanchen on 2017/9/25.
 */
public class K8dyEpisode implements IVideoEpisode,Parcelable {
    private String title;
    private String url;
    private String id;
    private int state;

    public K8dyEpisode() {
    }


    protected K8dyEpisode(Parcel in) {
        title = in.readString();
        url = in.readString();
        id = in.readString();
        state = in.readInt();
    }

    public static final Creator<K8dyEpisode> CREATOR = new Creator<K8dyEpisode>() {
        @Override
        public K8dyEpisode createFromParcel(Parcel in) {
            return new K8dyEpisode(in);
        }

        @Override
        public K8dyEpisode[] newArray(int size) {
            return new K8dyEpisode[size];
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
        return K8dyService.class.getName();
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
}
