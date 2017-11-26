package com.fanchen.imovie.entity.ll520;

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
public class LL520Home implements Parcelable,IBangumiRoot, IBangumiMoreRoot {

    private boolean success;
    private String message;
    private List<LL520Title> list;
    private List<LL520Video> result;
    private List<LL520Banner> banners;

    public LL520Home(){
    }

    protected LL520Home(Parcel in) {
        success = in.readByte() != 0;
        message = in.readString();
        list = in.createTypedArrayList(LL520Title.CREATOR);
        result = in.createTypedArrayList(LL520Video.CREATOR);
        banners = in.createTypedArrayList(LL520Banner.CREATOR);
    }

    public static final Creator<LL520Home> CREATOR = new Creator<LL520Home>() {
        @Override
        public LL520Home createFromParcel(Parcel in) {
            return new LL520Home(in);
        }

        @Override
        public LL520Home[] newArray(int size) {
            return new LL520Home[size];
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

    public void setList(List<LL520Title> list) {
        this.list = list;
    }

    @Override
    public List<? extends IViewType> getAdapterResult() {
        List<IViewType> viewTypes = new ArrayList<>();
        if (list != null){
            for (LL520Title title : list) {
                viewTypes.add(title);
                viewTypes.addAll(title.getList());
            }
        }else if(result != null){
            viewTypes.addAll(result);
        }
        return viewTypes;
    }

    public void setResult(List<LL520Video> result) {
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

    public void setBanners(List<LL520Banner> banners) {
        this.banners = banners;
    }
}
