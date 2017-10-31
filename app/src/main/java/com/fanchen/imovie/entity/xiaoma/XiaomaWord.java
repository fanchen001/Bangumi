package com.fanchen.imovie.entity.xiaoma;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fanchen on 2017/7/15.
 */
public class XiaomaWord implements Parcelable{
    private String title;
    private List<String> keywords;

    public XiaomaWord() {
    }

    protected XiaomaWord(Parcel in) {
        title = in.readString();
        keywords = in.createStringArrayList();
    }

    public static final Creator<XiaomaWord> CREATOR = new Creator<XiaomaWord>() {
        @Override
        public XiaomaWord createFromParcel(Parcel in) {
            return new XiaomaWord(in);
        }

        @Override
        public XiaomaWord[] newArray(int size) {
            return new XiaomaWord[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeStringList(keywords);
    }
}
