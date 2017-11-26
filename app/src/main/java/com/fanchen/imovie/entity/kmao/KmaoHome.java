package com.fanchen.imovie.entity.kmao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IBangumiRoot;
import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IHomeRoot;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoBanner;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.view.pager.IBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/9/24.
 */
public class KmaoHome implements Parcelable,IBangumiRoot, IBangumiMoreRoot {

    private boolean success;
    private String message;
    private List<KmaoTitle> list;
    private List<KmaoVideo> result;
    private List<KmaoBanner> banners;

    public KmaoHome(){
    }

    protected KmaoHome(Parcel in) {
        success = in.readByte() != 0;
        message = in.readString();
        list = in.createTypedArrayList(KmaoTitle.CREATOR);
        result = in.createTypedArrayList(KmaoVideo.CREATOR);
        banners = in.createTypedArrayList(KmaoBanner.CREATOR);
    }

    public static final Creator<KmaoHome> CREATOR = new Creator<KmaoHome>() {
        @Override
        public KmaoHome createFromParcel(Parcel in) {
            return new KmaoHome(in);
        }

        @Override
        public KmaoHome[] newArray(int size) {
            return new KmaoHome[size];
        }
    };

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public List<? extends IVideo> getList() {
        return result;
    }

    public void setList(List<KmaoTitle> list) {
        this.list = list;
    }

    @Override
    public List<? extends IViewType> getAdapterResult() {
        List<IViewType> viewTypes = new ArrayList<>();
        if (list != null){
            for (KmaoTitle title : list) {
                viewTypes.add(title);
                viewTypes.addAll(title.getList());
            }
        }else if(result != null){
            viewTypes.addAll(result);
        }
        return viewTypes;
    }

    public void setResult(List<KmaoVideo> result) {
        this.result = result;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<? extends IVideoBanner<? extends IBanner>> getHomeBanner() {
        return banners;
    }

    @Override
    public List<? extends IBangumiTitle> getResult() {
        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
        dest.writeTypedList(list);
        dest.writeTypedList(result);
        dest.writeTypedList(banners);
    }

    public void setBanners(List<KmaoBanner> banners) {
        this.banners = banners;
    }
}
