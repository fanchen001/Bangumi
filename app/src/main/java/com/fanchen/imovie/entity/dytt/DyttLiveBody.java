package com.fanchen.imovie.entity.dytt;

import android.os.Parcel;
import android.os.Parcelable;


import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * Created by fanchen on 2017/8/2.
 */
public class DyttLiveBody implements Parcelable,IViewType {
    private String area;
    private DyttLivePosition next;
    private DyttLivePosition current;
    private List<DyttLiveVideoUrls> videoUrls;
    private String videoName;
    private String shareImage;
    private int videoId;

    public DyttLiveBody(){
    }


    protected DyttLiveBody(Parcel in) {
        area = in.readString();
        next = in.readParcelable(DyttLivePosition.class.getClassLoader());
        current = in.readParcelable(DyttLivePosition.class.getClassLoader());
        videoUrls = in.createTypedArrayList(DyttLiveVideoUrls.CREATOR);
        videoName = in.readString();
        shareImage = in.readString();
        videoId = in.readInt();
    }

    public static final Creator<DyttLiveBody> CREATOR = new Creator<DyttLiveBody>() {
        @Override
        public DyttLiveBody createFromParcel(Parcel in) {
            return new DyttLiveBody(in);
        }

        @Override
        public DyttLiveBody[] newArray(int size) {
            return new DyttLiveBody[size];
        }
    };

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public DyttLivePosition getNext() {
        return next;
    }

    public void setNext(DyttLivePosition next) {
        this.next = next;
    }

    public DyttLivePosition getCurrent() {
        return current;
    }

    public void setCurrent(DyttLivePosition current) {
        this.current = current;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public List<DyttLiveVideoUrls> getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(List<DyttLiveVideoUrls> videoUrls) {
        this.videoUrls = videoUrls;
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
        dest.writeString(area);
        dest.writeParcelable(next, flags);
        dest.writeParcelable(current, flags);
        dest.writeTypedList(videoUrls);
        dest.writeString(videoName);
        dest.writeString(shareImage);
        dest.writeInt(videoId);
    }
}
