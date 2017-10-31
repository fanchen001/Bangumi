package com.fanchen.imovie.entity.xiaoma;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fanchen on 2017/7/15.
 */
public class XiaomaWordResult implements Parcelable{
    private String topInfo;
    private String searchHit;
    private List<XiaomaWord> hotKeyword;

    public XiaomaWordResult() {
    }

    protected XiaomaWordResult(Parcel in) {
        topInfo = in.readString();
        searchHit = in.readString();
        hotKeyword = in.createTypedArrayList(XiaomaWord.CREATOR);
    }

    public static final Creator<XiaomaWordResult> CREATOR = new Creator<XiaomaWordResult>() {
        @Override
        public XiaomaWordResult createFromParcel(Parcel in) {
            return new XiaomaWordResult(in);
        }

        @Override
        public XiaomaWordResult[] newArray(int size) {
            return new XiaomaWordResult[size];
        }
    };

    public String getTopInfo() {
        return topInfo;
    }

    public void setTopInfo(String topInfo) {
        this.topInfo = topInfo;
    }

    public String getSearchHit() {
        return searchHit;
    }

    public void setSearchHit(String searchHit) {
        this.searchHit = searchHit;
    }

    public List<XiaomaWord> getHotKeyword() {
        return hotKeyword;
    }

    public void setHotKeyword(List<XiaomaWord> hotKeyword) {
        this.hotKeyword = hotKeyword;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(topInfo);
        dest.writeString(searchHit);
        dest.writeTypedList(hotKeyword);
    }
}
