package com.fanchen.imovie.entity.dytt;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IViewType;

import java.util.List;

/**
 * DyttLive
 * Created by fanchen on 2018/9/27.
 */
public class DyttLive implements IViewType,Parcelable {
    private String thumb_x;
    private int content_id;
    private boolean vip_only;
    private int sort;
    private String title;
    private String bind_id;
    private int score;
    private boolean limited_free;
    private int content_type;
    private int provider_id;
    private int id;
    private String thumb_y;
    private List<DyttLiveEpgs> epgs;

    public DyttLive() {
    }

    protected DyttLive(Parcel in) {
        this.thumb_x = in.readString();
        this.content_id = in.readInt();
        this.vip_only = in.readByte() != 0;
        this.sort = in.readInt();
        this.title = in.readString();
        this.bind_id = in.readString();
        this.score = in.readInt();
        this.limited_free = in.readByte() != 0;
        this.content_type = in.readInt();
        this.provider_id = in.readInt();
        this.id = in.readInt();
        this.thumb_y = in.readString();
        this.epgs = in.createTypedArrayList(DyttLiveEpgs.CREATOR);
    }

    public String getThumb_x() {
        return thumb_x;
    }

    public void setThumb_x(String thumb_x) {
        this.thumb_x = thumb_x;
    }

    public int getContent_id() {
        return content_id;
    }

    public String getContentId() {
        return String.valueOf(content_id);
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public boolean isVip_only() {
        return vip_only;
    }

    public void setVip_only(boolean vip_only) {
        this.vip_only = vip_only;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBind_id() {
        return bind_id;
    }

    public void setBind_id(String bind_id) {
        this.bind_id = bind_id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isLimited_free() {
        return limited_free;
    }

    public void setLimited_free(boolean limited_free) {
        this.limited_free = limited_free;
    }

    public int getContent_type() {
        return content_type;
    }

    public void setContent_type(int content_type) {
        this.content_type = content_type;
    }

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumb_y() {
        return thumb_y;
    }

    public void setThumb_y(String thumb_y) {
        this.thumb_y = thumb_y;
    }

    public List<DyttLiveEpgs> getEpgs() {
        return epgs;
    }

    public void setEpgs(List<DyttLiveEpgs> epgs) {
        this.epgs = epgs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumb_x);
        dest.writeInt(this.content_id);
        dest.writeByte(this.vip_only ? (byte) 1 : (byte) 0);
        dest.writeInt(this.sort);
        dest.writeString(this.title);
        dest.writeString(this.bind_id);
        dest.writeInt(this.score);
        dest.writeByte(this.limited_free ? (byte) 1 : (byte) 0);
        dest.writeInt(this.content_type);
        dest.writeInt(this.provider_id);
        dest.writeInt(this.id);
        dest.writeString(this.thumb_y);
        dest.writeTypedList(this.epgs);
    }

    public static final Parcelable.Creator<DyttLive> CREATOR = new Parcelable.Creator<DyttLive>() {
        @Override
        public DyttLive createFromParcel(Parcel source) {
            return new DyttLive(source);
        }

        @Override
        public DyttLive[] newArray(int size) {
            return new DyttLive[size];
        }
    };

    @Override
    public int getViewType() {
        return TYPE_NORMAL;
    }
}
