package com.fanchen.imovie.entity.bmob;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoEpisode;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.util.DateUtil;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;

/**
 * Created by fanchen on 2017/9/22.
 */
@Table("tab_video_history")
public class VideoHistory extends BmobObj implements IViewType, Parcelable {
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_LIVE = 2;
    public static final int TYPE_TORRENT = 3;

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int _id;
    @Column("title")
    private String title;
    @Column("cover")
    private String cover;
    @Column("time")
    private String time;
    @Column("id")
    private String id;
    @Column("url")
    private String url;
    @Column("source")
    private int source;
    @Column("serviceClassName")
    private String serviceClassName;
    @Column("playPosition")
    private long playPosition;
    @Column("type")
    private int type;
    @Column("playType")
    private int playType;
    @Column("coverReferer")
    private String coverReferer;
    @Column("extend")
    //扩展的字段，用来存放一些信息
    private String extend;

    private String userId ;

    public VideoHistory() {
        User loginUser = User.getLoginUser();
        if(loginUser != null)
            userId = loginUser.getObjectId();
    }

    /**
     *
     * @param video
     * @param positon
     */
    public VideoHistory(IVideo video,IVideoEpisode episode,long positon) {
        title = video.getTitle() + "_" + episode.getTitle();
        cover = video.getCover();
        id = episode.getId();
        url = episode.getUrl();
        source = video.getSource();
        time = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        serviceClassName = video.getServiceClassName();
        playPosition = positon;
        playType = episode.getPlayerType();
        type = TYPE_VIDEO;
        coverReferer = video.getCoverReferer();
        User loginUser = User.getLoginUser();
        if(loginUser != null)
            userId = loginUser.getObjectId();
    }

    public VideoHistory(IVideo video,long positon) {
        title = video.getTitle();
        cover = video.getCover();
        id = video.getId();
        url = video.getUrl();
        source = video.getSource();
        time = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        serviceClassName = video.getServiceClassName();
        playPosition = positon;
        playType = IVideoEpisode.PLAY_TYPE_VIDEO;
        type = TYPE_VIDEO;
        coverReferer = video.getCoverReferer();
        User loginUser = User.getLoginUser();
        if(loginUser != null)
            userId = loginUser.getObjectId();
    }

    /**
     *
     * @param fileInfo
     * @param positon
     */
    public VideoHistory(TorrentFileInfo fileInfo,long positon) {
        title = fileInfo.mFileName;
        id = fileInfo.mSubPath;
        url = fileInfo.playUrl;
        playPosition = positon;
        playType = IVideoEpisode.PLAY_TYPE_VIDEO;
        time = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        type = TYPE_TORRENT;
        User loginUser = User.getLoginUser();
        if(loginUser != null)
            userId = loginUser.getObjectId();
    }

    protected VideoHistory(Parcel in) {
        title = in.readString();
        cover = in.readString();
        time = in.readString();
        id = in.readString();
        url = in.readString();
        source = in.readInt();
        _id = in.readInt();
        serviceClassName = in.readString();
        playPosition = in.readLong();
        type = in.readInt();
        extend = in.readString();
        userId = in.readString();
        coverReferer = in.readString();
    }

    public static final Creator<VideoHistory> CREATOR = new Creator<VideoHistory>() {
        @Override
        public VideoHistory createFromParcel(Parcel in) {
            return new VideoHistory(in);
        }

        @Override
        public VideoHistory[] newArray(int size) {
            return new VideoHistory[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
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
        dest.writeString(cover);
        dest.writeString(time);
        dest.writeString(id);
        dest.writeString(url);
        dest.writeInt(source);
        dest.writeInt(_id);
        dest.writeString(serviceClassName);
        dest.writeLong(playPosition);
        dest.writeInt(type);
        dest.writeString(extend);
        dest.writeString(userId);
        dest.writeString(coverReferer);
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    public void setServiceClassName(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public long getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(long playPosition) {
        this.playPosition = playPosition;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCoverReferer() {
        return coverReferer;
    }

    public void setCoverReferer(String coverReferer) {
        this.coverReferer = coverReferer;
    }
}
