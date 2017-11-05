package com.fanchen.imovie.entity.xiaoma;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IViewType;

/**
 * Created by fanchen on 2017/11/4.
 */
public class XiaomaSearch implements Parcelable,IViewType{
    private String link;

    private String desc;

    private String source;

    private String resId;

    private int navType;

    private String title;

    private String img;

    private long updateTime;

    private int sourceId;

    private String sourceLink;

    public XiaomaSearch(){
    }

    protected XiaomaSearch(Parcel in) {
        link = in.readString();
        desc = in.readString();
        source = in.readString();
        resId = in.readString();
        navType = in.readInt();
        title = in.readString();
        img = in.readString();
        updateTime = in.readLong();
        sourceId = in.readInt();
        sourceLink = in.readString();
    }

    public static final Creator<XiaomaSearch> CREATOR = new Creator<XiaomaSearch>() {
        @Override
        public XiaomaSearch createFromParcel(Parcel in) {
            return new XiaomaSearch(in);
        }

        @Override
        public XiaomaSearch[] newArray(int size) {
            return new XiaomaSearch[size];
        }
    };

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return this.link;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getResId() {
        return this.resId;
    }

    public void setNavType(int navType) {
        this.navType = navType;
    }

    public int getNavType() {
        return this.navType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return this.img;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public String getSourceLink() {
        return this.sourceLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(desc);
        dest.writeString(source);
        dest.writeString(resId);
        dest.writeInt(navType);
        dest.writeString(title);
        dest.writeString(img);
        dest.writeLong(updateTime);
        dest.writeInt(sourceId);
        dest.writeString(sourceLink);
    }

    @Override
    public int getViewType() {
        return TYPE_NORMAL;
    }
}
