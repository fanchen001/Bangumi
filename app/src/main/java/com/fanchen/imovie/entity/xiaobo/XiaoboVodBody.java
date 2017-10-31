package com.fanchen.imovie.entity.xiaobo;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IViewType;


/**
 * Created by fanchen on 2017/8/4.
 */
public class XiaoboVodBody implements Parcelable,IViewType {
    private String createTime;

    private String fileSize;

    private String title;

    private String hot;

    private String hash;

    public XiaoboVodBody() {

    }

    protected XiaoboVodBody(Parcel in) {
        createTime = in.readString();
        fileSize = in.readString();
        title = in.readString();
        hot = in.readString();
        hash = in.readString();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("文件大小:");
        sb.append(fileSize);
        sb.append("  ");
        sb.append("热度:");
        sb.append(hot);
        sb.append("  ");
        sb.append("创建时间:");
        sb.append(createTime);
        return sb.toString();
    }

    public static final Creator<XiaoboVodBody> CREATOR = new Creator<XiaoboVodBody>() {
        @Override
        public XiaoboVodBody createFromParcel(Parcel in) {
            return new XiaoboVodBody(in);
        }

        @Override
        public XiaoboVodBody[] newArray(int size) {
            return new XiaoboVodBody[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(createTime);
        dest.writeString(fileSize);
        dest.writeString(title);
        dest.writeString(hot);
        dest.writeString(hash);
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }
}
