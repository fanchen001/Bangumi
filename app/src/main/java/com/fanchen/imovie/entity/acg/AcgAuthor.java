package com.fanchen.imovie.entity.acg;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/7/23.
 */
public class AcgAuthor implements Parcelable{
    private int id;

    private String uid;

    private String name;

    private String url;

    private String avatarUrl;

    private int point;

    public AcgAuthor(){

    }

    protected AcgAuthor(Parcel in) {
        id = in.readInt();
        uid = in.readString();
        name = in.readString();
        url = in.readString();
        avatarUrl = in.readString();
        point = in.readInt();
    }

    public static final Creator<AcgAuthor> CREATOR = new Creator<AcgAuthor>() {
        @Override
        public AcgAuthor createFromParcel(Parcel in) {
            return new AcgAuthor(in);
        }

        @Override
        public AcgAuthor[] newArray(int size) {
            return new AcgAuthor[size];
        }
    };

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUid(){
        return this.uid;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }
    public void setAvatarUrl(String avatarUrl){
        this.avatarUrl = avatarUrl;
    }
    public String getAvatarUrl(){
        return this.avatarUrl;
    }
    public void setPoint(int point){
        this.point = point;
    }
    public int getPoint(){
        return this.point;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(avatarUrl);
        dest.writeInt(point);
    }
}
