package com.fanchen.imovie.entity.baba;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IBangumiMoreRoot;
import com.fanchen.imovie.entity.face.IBangumiRoot;
import com.fanchen.imovie.entity.face.IBangumiTitle;
import com.fanchen.imovie.entity.face.IVideo;
import com.fanchen.imovie.entity.face.IVideoBanner;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.view.pager.IBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanchen on 2017/9/24.
 */
public class BabayuHome implements Parcelable, IBangumiRoot, IBangumiMoreRoot {

    private boolean success;
    private String message;
    private List<BabayuTitle> list;
    private List<BabayuVideo> result;
    private List<BabayuBanner> banners;

    public BabayuHome(){
    }

    protected BabayuHome(Parcel in) {
        success = in.readByte() != 0;
        message = in.readString();
        list = in.createTypedArrayList(BabayuTitle.CREATOR);
        result = in.createTypedArrayList(BabayuVideo.CREATOR);
        banners = in.createTypedArrayList(BabayuBanner.CREATOR);
    }

    public static final Creator<BabayuHome> CREATOR = new Creator<BabayuHome>() {
        @Override
        public BabayuHome createFromParcel(Parcel in) {
            return new BabayuHome(in);
        }

        @Override
        public BabayuHome[] newArray(int size) {
            return new BabayuHome[size];
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

    public void setList(List<BabayuTitle> list) {
        this.list = list;
    }

    @Override
    public List<? extends IViewType> getAdapterResult() {
        List<IViewType> viewTypes = new ArrayList<>();
        if (list != null){
            for (BabayuTitle title : list) {
                viewTypes.add(title);
                viewTypes.addAll(title.getList());
            }
        }else if(result != null){
            viewTypes.addAll(result);
        }
        return viewTypes;
    }

    public void setResult(List<BabayuVideo> result) {
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

    public void setBanners(List<BabayuBanner> banners) {
        this.banners = banners;
    }
}
