package com.fanchen.imovie.entity.baidu;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.ISearchWord;


/**
 * 百度搜索词语联想
 * Created by fanchen on 2017/7/15.
 */
public class SearchHit implements Parcelable, ISearchWord {
    private String q;
    private String t;

    public SearchHit() {

    }

    protected SearchHit(Parcel in) {
        q = in.readString();
        t = in.readString();
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public static final Creator<SearchHit> CREATOR = new Creator<SearchHit>() {
        @Override
        public SearchHit createFromParcel(Parcel in) {
            return new SearchHit(in);
        }

        @Override
        public SearchHit[] newArray(int size) {
            return new SearchHit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(q);
        dest.writeString(t);
    }

    @Override
    public int getType() {
        return TYPE_WORD;
    }

    @Override
    public String getWord() {
        return q;
    }

    @Override
    public int getViewType() {
        return TYPE_NORMAL;
    }
}
