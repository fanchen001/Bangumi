package com.fanchen.imovie.entity.acg;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/7/23.
 */
public class AcgDate implements Parcelable{
    private int timestamp;

    private int timestampGmt;

    private String full;

    private String human;

    public AcgDate(){

    }

    protected AcgDate(Parcel in) {
        timestamp = in.readInt();
        timestampGmt = in.readInt();
        full = in.readString();
        human = in.readString();
    }

    public static final Creator<AcgDate> CREATOR = new Creator<AcgDate>() {
        @Override
        public AcgDate createFromParcel(Parcel in) {
            return new AcgDate(in);
        }

        @Override
        public AcgDate[] newArray(int size) {
            return new AcgDate[size];
        }
    };

    public void setTimestamp(int timestamp){
        this.timestamp = timestamp;
    }
    public int getTimestamp(){
        return this.timestamp;
    }
    public void setTimestampGmt(int timestampGmt){
        this.timestampGmt = timestampGmt;
    }
    public int getTimestampGmt(){
        return this.timestampGmt;
    }
    public void setFull(String full){
        this.full = full;
    }
    public String getFull(){
        return this.full;
    }
    public void setHuman(String human){
        this.human = human;
    }
    public String getHuman(){
        return this.human;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(timestamp);
        dest.writeInt(timestampGmt);
        dest.writeString(full);
        dest.writeString(human);
    }
}
