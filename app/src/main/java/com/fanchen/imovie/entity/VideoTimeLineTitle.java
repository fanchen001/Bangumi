package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBaseVideo;
import com.fanchen.imovie.entity.face.IBangumiTimeTitle;
import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * Created by fanchen on 2017/9/20.
 */
public class VideoTimeLineTitle implements IBangumiTimeTitle,Parcelable{
    private List<VideoBase> list;
    private String title;
    private int drawable;
    private boolean isNow;

    public VideoTimeLineTitle(){
    }

    protected VideoTimeLineTitle(Parcel in) {
        list = in.createTypedArrayList(VideoBase.CREATOR);
        title = in.readString();
        drawable = in.readInt();
        isNow = in.readByte() != 0;
    }

    public static final Creator<VideoTimeLineTitle> CREATOR = new Creator<VideoTimeLineTitle>() {
        @Override
        public VideoTimeLineTitle createFromParcel(Parcel in) {
            return new VideoTimeLineTitle(in);
        }

        @Override
        public VideoTimeLineTitle[] newArray(int size) {
            return new VideoTimeLineTitle[size];
        }
    };

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<? extends IBaseVideo> getList() {
        return list;
    }

    @Override
    public boolean isNow() {
        return isNow;
    }

    @Override
    public int getDrawable() {
        return drawable;
    }

    public void setList(List<VideoBase> list) {
        this.list = list;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public void setIsNow(boolean isNow) {
        this.isNow = isNow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(list);
        dest.writeString(title);
        dest.writeInt(drawable);
        dest.writeByte((byte) (isNow ? 1 : 0));
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_TITLE;
    }
}
