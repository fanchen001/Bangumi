package com.fanchen.imovie.entity.acg;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fanchen on 2017/7/23.
 */
public class AcgData implements Parcelable{
    private List<AcgPosts> posts ;

    public AcgData(){

    }

    protected AcgData(Parcel in) {
        posts = in.createTypedArrayList(AcgPosts.CREATOR);
    }

    public static final Creator<AcgData> CREATOR = new Creator<AcgData>() {
        @Override
        public AcgData createFromParcel(Parcel in) {
            return new AcgData(in);
        }

        @Override
        public AcgData[] newArray(int size) {
            return new AcgData[size];
        }
    };

    public void setPosts(List<AcgPosts> posts){
        this.posts = posts;
    }
    public List<AcgPosts> getPosts(){
        return this.posts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(posts);
    }
}
