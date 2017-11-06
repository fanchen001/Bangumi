package com.fanchen.imovie.entity.bmob;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.dytt.DyttLiveBody;
import com.google.gson.Gson;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by fanchen on 2017/9/22.
 */
@Table("tab_video_collect")
public class VideoCollect extends VideoHistory implements Parcelable {

    @Column("extras")
    private String extras;
    @Column("danmaku")
    private String danmaku;


    public VideoCollect() {
        if(User.getLoginUser() != null)
            setUserId(User.getLoginUser().getObjectId());
    }

    public VideoCollect(DyttLiveBody item) {
        setType( TYPE_LIVE);
        extras = item.getCurrent().getEpgName();
        danmaku = item.getNext().getEpgName();
        setTitle(item.getVideoName());
        setId(String.valueOf(item.getVideoId()));
        setCover(item.getShareImage());
        setTime(item.getNext().getEpgName());
        setExtend(new Gson().toJson(item));
        if(User.getLoginUser() != null)
            setUserId(User.getLoginUser().getObjectId());
    }

    public VideoCollect(IVideo videoItem) {
        setType(TYPE_VIDEO);
        extras = videoItem.getExtras();
        danmaku = videoItem.getDanmaku();
        setTitle(videoItem.getTitle());
        setId(videoItem.getId());
        setCover(videoItem.getCover());
        setTime(videoItem.getLast());
        setCoverReferer(videoItem.getCoverReferer());
        setServiceClassName(videoItem.getServiceClassName());
        if(User.getLoginUser() != null)
            setUserId(User.getLoginUser().getObjectId());
    }


    protected VideoCollect(Parcel in) {
        super(in);
        extras = in.readString();
        danmaku = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(extras);
        dest.writeString(danmaku);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<VideoCollect> CREATOR = new Creator<VideoCollect>() {
        @Override
        public VideoCollect createFromParcel(Parcel in) {
            return new VideoCollect(in);
        }

        @Override
        public VideoCollect[] newArray(int size) {
            return new VideoCollect[size];
        }
    };

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getDanmaku() {
        return danmaku;
    }

    public void setDanmaku(String danmaku) {
        this.danmaku = danmaku;
    }

}
