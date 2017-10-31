package com.fanchen.imovie.entity.xiaokanba;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.XiaokanbaService;

/**
 * Created by fanchen on 2017/10/15.
 */
public class XiaokanbaVideo implements IVideo, Parcelable {

    /*** 标题**/
    private String title;
    /*** 图片**/
    private String cover;
    /*** id**/
    private String id;
    /*** url**/
    private String url;
    private String score;

    public XiaokanbaVideo(){
    }

    protected XiaokanbaVideo(Parcel in) {
        title = in.readString();
        cover = in.readString();
        id = in.readString();
        url = in.readString();
        score = in.readString();
    }

    public static final Creator<XiaokanbaVideo> CREATOR = new Creator<XiaokanbaVideo>() {
        @Override
        public XiaokanbaVideo createFromParcel(Parcel in) {
            return new XiaokanbaVideo(in);
        }

        @Override
        public XiaokanbaVideo[] newArray(int size) {
            return new XiaokanbaVideo[size];
        }
    };

    @Override
    public String getLast() {
        return "暂无介绍";
    }

    @Override
    public String getExtras() {
        return "暂无分类";
    }

    @Override
    public String getDanmaku() {
        return "评分:" + score;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getServiceClassName() {
        return XiaokanbaService.class.getName();
    }

    @Override
    public boolean hasVideoDetails() {
        return true;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCover() {
        return cover;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int getSource() {
        return 0;
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
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(score);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
