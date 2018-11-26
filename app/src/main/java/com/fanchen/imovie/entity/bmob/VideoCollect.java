package com.fanchen.imovie.entity.bmob;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.dytt.DyttLive;
import com.fanchen.imovie.entity.dytt.DyttLiveEpgs;
import com.fanchen.imovie.entity.face.IVideo;
import com.google.gson.Gson;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

import java.util.List;


/**
 * VideoCollect
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

    public VideoCollect(DyttLive item) {
        setType(TYPE_LIVE);
        List<DyttLiveEpgs> epgs = item.getEpgs();
        if(epgs != null && epgs.size() > 0){
            extras = epgs.get(0).getTitle();
            danmaku = epgs.get(epgs.size() - 1).getTitle();
            setTime(epgs.get(0).getTag());
        }
        setTitle(item.getTitle());
        setId(String.valueOf(item.getId()));
        setCover(item.getThumb_x());
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
        setServiceClassName(videoItem.getServiceClass());
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
