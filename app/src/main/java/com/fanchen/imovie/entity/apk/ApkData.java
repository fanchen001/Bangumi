package com.fanchen.imovie.entity.apk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fanchen on 2017/3/22.
 */
public class ApkData<T> implements Parcelable{

    private List<T> list;
    private int currentpage;

    public ApkData(Parcel in) {
        list = in.readArrayList(getClass().getClassLoader());
        currentpage = in.readInt();
    }

    public ApkData() {
    }

    public static final Creator<ApkData> CREATOR = new Creator<ApkData>() {
        @Override
        public ApkData createFromParcel(Parcel in) {
            return new ApkData(in);
        }

        @Override
        public ApkData[] newArray(int size) {
            return new ApkData[size];
        }
    };

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getCurrentpage() {
        return currentpage;
    }

    public void setCurrentpage(int currentpage) {
        this.currentpage = currentpage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(list);
        dest.writeInt(currentpage);
    }
}
