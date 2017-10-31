package com.fanchen.imovie.entity.dytt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 直播播放位置
 * 包括当前正在播放及后续播放
 * Created by fanchen on 2017/8/2.
 */
public class DyttLivePosition implements Parcelable{
    private String epgName;
    private int showId;
    private int epgId;
    private String labelId;
    private int review;
    private String typeName;
    private int startTime;
    private int typeId;
    private int endTime;
    private String label;


    public DyttLivePosition(){

    }

    protected DyttLivePosition(Parcel in) {
        epgName = in.readString();
        showId = in.readInt();
        epgId = in.readInt();
        labelId = in.readString();
        review = in.readInt();
        typeName = in.readString();
        startTime = in.readInt();
        typeId = in.readInt();
        endTime = in.readInt();
        label = in.readString();
    }

    public String getEpgName() {
        return epgName;
    }

    public void setEpgName(String epgName) {
        this.epgName = epgName;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public int getEpgId() {
        return epgId;
    }

    public void setEpgId(int epgId) {
        this.epgId = epgId;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static final Creator<DyttLivePosition> CREATOR = new Creator<DyttLivePosition>() {
        @Override
        public DyttLivePosition createFromParcel(Parcel in) {
            return new DyttLivePosition(in);
        }

        @Override
        public DyttLivePosition[] newArray(int size) {
            return new DyttLivePosition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(epgName);
        dest.writeInt(showId);
        dest.writeInt(epgId);
        dest.writeString(labelId);
        dest.writeInt(review);
        dest.writeString(typeName);
        dest.writeInt(startTime);
        dest.writeInt(typeId);
        dest.writeInt(endTime);
        dest.writeString(label);
    }
}
