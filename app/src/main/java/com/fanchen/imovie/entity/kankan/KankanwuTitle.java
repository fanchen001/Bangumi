package com.fanchen.imovie.entity.kankan;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.retrofit.service.KankanService;

import java.util.List;

/**
 * Created by fanchen on 2017/9/24.
 */
public class KankanwuTitle implements IBangumiTitle,Parcelable{
    private boolean more;
    private String url;
    private String title;
    private String id;
    private List<KankanwuVideo> list;
    private int drawable;

    public KankanwuTitle(){
    }

    protected KankanwuTitle(Parcel in) {
        more = in.readByte() != 0;
        url = in.readString();
        title = in.readString();
        list = in.createTypedArrayList(KankanwuVideo.CREATOR);
        drawable = in.readInt();
        id = in.readString();
    }

    public static final Creator<KankanwuTitle> CREATOR = new Creator<KankanwuTitle>() {
        @Override
        public KankanwuTitle createFromParcel(Parcel in) {
            return new KankanwuTitle(in);
        }

        @Override
        public KankanwuTitle[] newArray(int size) {
            return new KankanwuTitle[size];
        }
    };

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setList(List<KankanwuVideo> list) {
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
        return KankanService.class.getName();
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_TITLE;
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
        dest.writeTypedList(list);
        dest.writeInt(drawable);
        dest.writeString(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getStartPage() {
        return 1;
    }

    public void setId(String id) {
        this.id = id;
    }
}
