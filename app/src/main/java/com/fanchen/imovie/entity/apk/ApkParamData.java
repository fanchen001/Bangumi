package com.fanchen.imovie.entity.apk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/7/16.
 */
public class ApkParamData implements Parcelable {
    private Integer number;
    private Integer page;
    private String keyword;
    private String packageName;
    private Integer id;

    public ApkParamData() {
    }

    public ApkParamData(String packageName) {
        this.packageName = packageName;
    }

    public ApkParamData(int page, String keyword) {
        this.keyword = keyword;
        this.page = page;
    }

    public ApkParamData(int number, int page) {
        this.number = number;
        this.page = page;
    }


    protected ApkParamData(Parcel in) {
        number = in.readInt();
        page = in.readInt();
        id = in.readInt();
        keyword = in.readString();
        packageName = in.readString();
    }


    public ApkParamData(int page) {
        this.page = page;
        number = 10;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static final Creator<ApkParamData> CREATOR = new Creator<ApkParamData>() {
        @Override
        public ApkParamData createFromParcel(Parcel in) {
            return new ApkParamData(in);
        }

        @Override
        public ApkParamData[] newArray(int size) {
            return new ApkParamData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeInt(page);
        dest.writeInt(id);
        dest.writeString(keyword);
        dest.writeString(packageName);
    }
}
