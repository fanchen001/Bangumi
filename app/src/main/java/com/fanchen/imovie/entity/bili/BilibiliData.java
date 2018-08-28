package com.fanchen.imovie.entity.bili;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fanchen on 2017/12/29.
 */
public class BilibiliData implements Parcelable{
    private String trackid;
    private List<BilibiliWord> list ;

    public BilibiliData(){
    }

    protected BilibiliData(Parcel in) {
        trackid = in.readString();
        list = in.createTypedArrayList(BilibiliWord.CREATOR);
    }

    public static final Creator<BilibiliData> CREATOR = new Creator<BilibiliData>() {
        @Override
        public BilibiliData createFromParcel(Parcel in) {
            return new BilibiliData(in);
        }

        @Override
        public BilibiliData[] newArray(int size) {
            return new BilibiliData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackid);
        dest.writeTypedList(list);
    }

    public String getTrackid() {
        return trackid;
    }

    public void setTrackid(String trackid) {
        this.trackid = trackid;
    }

    public List<BilibiliWord> getList() {
        return list;
    }

    public void setList(List<BilibiliWord> list) {
        this.list = list;
    }
}
