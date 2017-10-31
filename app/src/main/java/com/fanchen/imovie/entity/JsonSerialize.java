package com.fanchen.imovie.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 本地缓存数据
 * 使用json缓存
 * Created by fanchen on 2017/9/30.
 */
@Table("tab_serialize")
public class JsonSerialize implements Parcelable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int _id;
    @Column("json")
    private String json;
    @Column("time")
    private long time;
    @Column("key")
    private String key;
    @Column("clazz")
    private String clazz;
    @Column("isRawType")
    private boolean isRawType;

    public JsonSerialize(){
    }

    public JsonSerialize(Object json, String key) {
        this.json = new Gson().toJson(json);
        this.key = key;
        this.time = System.currentTimeMillis();
        this.isRawType = true;
        this.clazz = json.getClass().getName();
    }

    public JsonSerialize(Object json, String key,boolean isRawType) {
        this.json = new Gson().toJson(json);
        this.key = key;
        this.time = System.currentTimeMillis();
        this.isRawType = isRawType;
        this.clazz = json.getClass().getName();
    }

    public JsonSerialize(String json, String key,boolean isRawType) {
        this.json = json;
        this.key = key;
        this.time = System.currentTimeMillis();
        this.isRawType = isRawType;
    }

    public JsonSerialize(String json, String key, Class<?> clazz) {
       this(json,key,clazz.getName());
    }

    public JsonSerialize(String json, String key, String clazz) {
        this.json = json;
        this.key = key;
        this.clazz = clazz;
        this.isRawType = true;
        this.time = System.currentTimeMillis();
    }

    protected JsonSerialize(Parcel in) {
        json = in.readString();
        time = in.readLong();
        key = in.readString();
        clazz = in.readString();
        isRawType = in.readByte() != 0;
        _id = in.readInt();
    }

    public static final Creator<JsonSerialize> CREATOR = new Creator<JsonSerialize>() {
        @Override
        public JsonSerialize createFromParcel(Parcel in) {
            return new JsonSerialize(in);
        }

        @Override
        public JsonSerialize[] newArray(int size) {
            return new JsonSerialize[size];
        }
    };

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRawType() {
        return isRawType;
    }

    /**
     *
     * @param her
     * @return
     */
    public boolean isStale(int her){
        return System.currentTimeMillis() - time > her * 60 * 60 * 1000;
    }

    /**
     *
     * @return
     */
    public boolean isStale(){
        return isStale(24);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(json);
        dest.writeLong(time);
        dest.writeString(key);
        dest.writeString(clazz);
        dest.writeByte((byte) (isRawType ? 1 : 0));
        dest.writeInt(_id);
    }
}
