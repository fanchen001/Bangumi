package com.fanchen.imovie.entity.tucao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IVideoBanner;
import com.fanchen.imovie.retrofit.service.TucaoService;

/**
 * Tucaoc 轮播图数据实体
 * Created by fanchen on 2017/9/16.
 */
public class TucaoBanner extends TucaoBaseVideo implements Parcelable, IVideoBanner<TucaoBanner> {

    /***
     * 附加信息
     *****/
    public String extInfo;
    private int bannerType;

    public TucaoBanner() {
    }

    protected TucaoBanner(Parcel in) {
        super(in);
        extInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(extInfo);
    }

    public void setBannerType(int bannerType) {
        this.bannerType = bannerType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TucaoBanner> CREATOR = new Creator<TucaoBanner>() {
        @Override
        public TucaoBanner createFromParcel(Parcel in) {
            return new TucaoBanner(in);
        }

        @Override
        public TucaoBanner[] newArray(int size) {
            return new TucaoBanner[size];
        }
    };

    @Override
    public TucaoBanner getData() {
        return this;
    }

    @Override
    public int getBannerType() {
        return bannerType;
    }

    @Override
    public String getReferer() {
        return null;
    }

    @Override
    public int getSource() {
        return 0;
    }

    @Override
    public String getServiceClass() {
        return TucaoService.class.getName();
    }


    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
}
