package com.fanchen.imovie.entity.apk;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.entity.face.IViewType;


/**
 * Created by fanchen on 2017/7/19.
 */
public class ApkItem  implements Parcelable,IViewType{
    private String id;
    private String title;
    private String type;
    private String intro;
    private String hasheaderimage;
    private String clicksum;
    private String updatetime;
    private String currentversioncode;
    private String headerurl;
    private String iconurl;
    private String packageName;

    protected ApkItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        type = in.readString();
        intro = in.readString();
        hasheaderimage = in.readString();
        clicksum = in.readString();
        updatetime = in.readString();
        currentversioncode = in.readString();
        headerurl = in.readString();
        iconurl = in.readString();
        packageName = in.readString();
    }

    public ApkItem() {
    }

    public static final Creator<ApkItem> CREATOR = new Creator<ApkItem>() {
        @Override
        public ApkItem createFromParcel(Parcel in) {
            return new ApkItem(in);
        }

        @Override
        public ApkItem[] newArray(int size) {
            return new ApkItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackagename() {
        return packageName;
    }

    public void setPackagename(String packagename) {
        this.packageName = packagename;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getHeaderurl() {
        return headerurl;
    }

    public void setHeaderurl(String headerurl) {
        this.headerurl = headerurl;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getClicksum() {
        return clicksum;
    }

    public void setClicksum(String clicksum) {
        this.clicksum = clicksum;
    }

    public String getHasheaderimage() {
        return hasheaderimage;
    }

    public void setHasheaderimage(String hasheaderimage) {
        this.hasheaderimage = hasheaderimage;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrentversioncode() {
        return currentversioncode;
    }

    public void setCurrentversioncode(String currentversioncode) {
        this.currentversioncode = currentversioncode;
    }

    public String getIco(){
        return String.format("http://cdn.moeapk.com/statics/apk/%s/%s.thumbnail?%s",packageName,packageName,currentversioncode);
    }

    public String getCover() {
        return String.format("http://cdn.moeapk.com/statics/apk/%s/header.image?%s",packageName,currentversioncode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(intro);
        dest.writeString(hasheaderimage);
        dest.writeString(clicksum);
        dest.writeString(updatetime);
        dest.writeString(currentversioncode);
        dest.writeString(headerurl);
        dest.writeString(iconurl);
        dest.writeString(packageName);
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }
}
