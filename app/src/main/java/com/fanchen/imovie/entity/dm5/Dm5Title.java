package com.fanchen.imovie.entity.dm5;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.Dm5Service;

import java.util.List;

/**
 * Created by fanchen on 2017/10/2.
 */
public class Dm5Title implements IBangumiTitle,Parcelable{

    private boolean more;
    private String url;
    private String title;
    private String id;
    private List<Dm5Video> list;
    private int drawable;

    public Dm5Title(){
    }

    protected Dm5Title(Parcel in) {
        more = in.readByte() != 0;
        url = in.readString();
        title = in.readString();
        id = in.readString();
        list = in.createTypedArrayList(Dm5Video.CREATOR);
        drawable = in.readInt();
    }

    public static final Creator<Dm5Title> CREATOR = new Creator<Dm5Title>() {
        @Override
        public Dm5Title createFromParcel(Parcel in) {
            return new Dm5Title(in);
        }

        @Override
        public Dm5Title[] newArray(int size) {
            return new Dm5Title[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public void setList(List<Dm5Video> list) {
        this.list = list;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    @Override
    public boolean hasMore() {
        return more;
    }

    @Override
    public String getFormatUrl() {
        return url;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getDrawable() {
        return drawable;
    }

    @Override
    public List<? extends IVideo> getList() {
        return list;
    }

    @Override
    public String getServiceClassName() {
        return Dm5Service.class.getName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getStartPage() {
        return 1;
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_TITLE;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (more ? 1 : 0));
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(id);
        dest.writeTypedList(list);
        dest.writeInt(drawable);
    }
}
