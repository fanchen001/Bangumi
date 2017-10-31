package com.fanchen.imovie.entity.baidu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 百度搜索词语联想
 * 根节点
 * Created by fanchen on 2017/7/15.
 */
public class SearchHitRoot implements Parcelable {
    private String q;
    private boolean p;
    private String bs;
    private String csor;
    private List<String> s;
    private List<SearchHit> g;

    public SearchHitRoot() {
    }

    protected SearchHitRoot(Parcel in) {
        q = in.readString();
        p = in.readByte() != 0;
        bs = in.readString();
        csor = in.readString();
        s = in.createStringArrayList();
        g = in.createTypedArrayList(SearchHit.CREATOR);
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public List<SearchHit> getG() {
        return g;
    }

    public void setG(List<SearchHit> g) {
        this.g = g;
    }

    public List<String> getS() {
        return s;
    }

    public void setS(List<String> s) {
        this.s = s;
    }

    public String getCsor() {
        return csor;
    }

    public void setCsor(String csor) {
        this.csor = csor;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public boolean isP() {
        return p;
    }

    public void setP(boolean p) {
        this.p = p;
    }

    public static final Creator<SearchHitRoot> CREATOR = new Creator<SearchHitRoot>() {
        @Override
        public SearchHitRoot createFromParcel(Parcel in) {
            return new SearchHitRoot(in);
        }

        @Override
        public SearchHitRoot[] newArray(int size) {
            return new SearchHitRoot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(q);
        dest.writeByte((byte) (p ? 1 : 0));
        dest.writeString(bs);
        dest.writeString(csor);
        dest.writeStringList(s);
        dest.writeTypedList(g);
    }
}
