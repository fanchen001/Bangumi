package com.fanchen.imovie.entity.xiaoma;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fanchen on 2017/11/4.
 */
public class XiaomaSearchResult implements Parcelable{
    private String start;

    private int more;

    private List<XiaomaSearch> list ;

    public XiaomaSearchResult(){
    }

    protected XiaomaSearchResult(Parcel in) {
        start = in.readString();
        more = in.readInt();
        list = in.createTypedArrayList(XiaomaSearch.CREATOR);
    }

    public static final Creator<XiaomaSearchResult> CREATOR = new Creator<XiaomaSearchResult>() {
        @Override
        public XiaomaSearchResult createFromParcel(Parcel in) {
            return new XiaomaSearchResult(in);
        }

        @Override
        public XiaomaSearchResult[] newArray(int size) {
            return new XiaomaSearchResult[size];
        }
    };

    public void setStart(String start){
        this.start = start;
    }
    public String getStart(){
        return this.start;
    }
    public void setMore(int more){
        this.more = more;
    }
    public int getMore(){
        return this.more;
    }
    public void setList(List<XiaomaSearch> list){
        this.list = list;
    }
    public List<XiaomaSearch> getList(){
        return this.list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(start);
        dest.writeInt(more);
        dest.writeTypedList(list);
    }
}
