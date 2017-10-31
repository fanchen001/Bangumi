package com.fanchen.imovie.entity.tucao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.retrofit.service.TucaoService;

import java.util.List;

/**
 * Created by fanchen on 2017/9/18.
 */
public class TucaoTitle implements IBangumiTitle,Parcelable {

    private boolean more;
    private String url;
    private String title;
    private String id;
    private List<TucaoVideo> list;
    private int drawable;

    public TucaoTitle(){
    }

    protected TucaoTitle(Parcel in) {
        more = in.readByte() != 0;
        url = in.readString();
        title = in.readString();
        id = in.readString();
        list = in.createTypedArrayList(TucaoVideo.CREATOR);
        drawable = in.readInt();
    }

    public static final Creator<TucaoTitle> CREATOR = new Creator<TucaoTitle>() {
        @Override
        public TucaoTitle createFromParcel(Parcel in) {
            return new TucaoTitle(in);
        }

        @Override
        public TucaoTitle[] newArray(int size) {
            return new TucaoTitle[size];
        }
    };

    @Override
    public boolean hasMore() {
        return more;
    }

    @Override
    public String getFormatUrl() {
        return getUrl();
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
        return TucaoService.class.getName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getViewType() {
        return TYPE_TITLE;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setList(List<TucaoVideo> list) {
        this.list = list;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getUrl() {
        return url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMore() {
        return more;
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
