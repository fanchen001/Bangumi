package com.fanchen.imovie.entity.dytt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * DyttLiveEpgs
 * Created by fanchen on 2018/9/27.
 */
public class DyttLiveEpgs implements Parcelable {

    private String thumb_ott;
    private boolean blocked;
    private int stream_id;
    private String start;
    private String end;
    private int id;
    private String tag;
    private String title;
    private String type;

    public DyttLiveEpgs() {
    }

    protected DyttLiveEpgs(Parcel in) {
        this.thumb_ott = in.readString();
        this.blocked = in.readByte() != 0;
        this.stream_id = in.readInt();
        this.start = in.readString();
        this.end = in.readString();
        this.id = in.readInt();
        this.tag = in.readString();
        this.title = in.readString();
        this.type = in.readString();
    }

    public String getThumb_ott() {
        return thumb_ott;
    }

    public void setThumb_ott(String thumb_ott) {
        this.thumb_ott = thumb_ott;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getStream_id() {
        return stream_id;
    }

    public void setStream_id(int stream_id) {
        this.stream_id = stream_id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumb_ott);
        dest.writeByte(this.blocked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.stream_id);
        dest.writeString(this.start);
        dest.writeString(this.end);
        dest.writeInt(this.id);
        dest.writeString(this.tag);
        dest.writeString(this.title);
        dest.writeString(this.type);
    }

    public static final Parcelable.Creator<DyttLiveEpgs> CREATOR = new Parcelable.Creator<DyttLiveEpgs>() {
        @Override
        public DyttLiveEpgs createFromParcel(Parcel source) {
            return new DyttLiveEpgs(source);
        }

        @Override
        public DyttLiveEpgs[] newArray(int size) {
            return new DyttLiveEpgs[size];
        }
    };
}
