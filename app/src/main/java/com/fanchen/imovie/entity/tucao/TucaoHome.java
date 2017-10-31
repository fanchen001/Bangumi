package com.fanchen.imovie.entity.tucao;

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
 * Created by fanchen on 2017/9/18.
 */
public class TucaoHome implements IBangumiRoot,IBangumiMoreRoot,Parcelable {

    private List<TucaoBanner> banner;
    private List<TucaoTitle> result;
    private boolean success;
    private String message;
    private List<? extends IVideo> list;

    public TucaoHome(){
    }

    protected TucaoHome(Parcel in) {
        banner = in.createTypedArrayList(TucaoBanner.CREATOR);
        result = in.createTypedArrayList(TucaoTitle.CREATOR);
        success = in.readByte() != 0;
        message = in.readString();
    }

    public static final Creator<TucaoHome> CREATOR = new Creator<TucaoHome>() {
        @Override
        public TucaoHome createFromParcel(Parcel in) {
            return new TucaoHome(in);
        }

        @Override
        public TucaoHome[] newArray(int size) {
            return new TucaoHome[size];
        }
    };

    @Override
    public List<? extends IVideoBanner<? extends IBanner>> getHomeBanner() {
        return banner;
    }

    @Override
    public List<? extends IBangumiTitle> getResult() {
        return result;
    }

    @Override
    public List<IViewType> getAdapterResult() {
        List<IViewType> viewTypes = new ArrayList<>();
        for (IBangumiTitle homeResult : result) {
            viewTypes.add(homeResult);
            viewTypes.addAll(homeResult.getList());
        }
        return viewTypes;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setHomeBanner(List<TucaoBanner> banner) {
        this.banner = banner;
    }

    public void setHomeResult(List<TucaoTitle> result) {
        this.result = result;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(banner);
        dest.writeTypedList(result);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
    }

    @Override
    public List<? extends IVideo> getList() {
        return list;
    }

    public void setList(List<? extends IVideo> list) {
        this.list = list;
    }
}
