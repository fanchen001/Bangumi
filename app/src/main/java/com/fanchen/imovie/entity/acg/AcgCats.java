package com.fanchen.imovie.entity.acg;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanchen on 2017/7/23.
 */
public class AcgCats implements Parcelable{
    private int id;

    private int parentId;

    private String name;

    private String slug;

    private String url;

    private String description;

    private String color;

    public AcgCats(){

    }

    protected AcgCats(Parcel in) {
        id = in.readInt();
        parentId = in.readInt();
        name = in.readString();
        slug = in.readString();
        url = in.readString();
        description = in.readString();
        color = in.readString();
    }

    public static final Creator<AcgCats> CREATOR = new Creator<AcgCats>() {
        @Override
        public AcgCats createFromParcel(Parcel in) {
            return new AcgCats(in);
        }

        @Override
        public AcgCats[] newArray(int size) {
            return new AcgCats[size];
        }
    };

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setParentId(int parentId){
        this.parentId = parentId;
    }
    public int getParentId(){
        return this.parentId;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setSlug(String slug){
        this.slug = slug;
    }
    public String getSlug(){
        return this.slug;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setColor(String color){
        this.color = color;
    }
    public String getColor(){
        return this.color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(parentId);
        dest.writeString(name);
        dest.writeString(slug);
        dest.writeString(url);
        dest.writeString(description);
        dest.writeString(color);
    }
}
