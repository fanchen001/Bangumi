package com.fanchen.imovie.entity.apk;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.jsoup.node.Node;

/**
 * Created by fanchen on 2017/7/19.
 */
public class ApkEvaluat implements Parcelable,IViewType {
    private String id;
    private String type;
    private String title;
    private String content;
    private String writer;
    private String video;
    private String hide;
    private String reproduce;
    private String viewtimes;
    private String posttime;
    private String edittime;

    public ApkEvaluat() {
    }

    protected ApkEvaluat(Parcel in) {
        id = in.readString();
        type = in.readString();
        title = in.readString();
        content = in.readString();
        writer = in.readString();
        video = in.readString();
        hide = in.readString();
        reproduce = in.readString();
        viewtimes = in.readString();
        posttime = in.readString();
        edittime = in.readString();
    }

    public static final Creator<ApkEvaluat> CREATOR = new Creator<ApkEvaluat>() {
        @Override
        public ApkEvaluat createFromParcel(Parcel in) {
            return new ApkEvaluat(in);
        }

        @Override
        public ApkEvaluat[] newArray(int size) {
            return new ApkEvaluat[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getHide() {
        return hide;
    }

    public void setHide(String hide) {
        this.hide = hide;
    }

    public String getReproduce() {
        return reproduce;
    }

    public void setReproduce(String reproduce) {
        this.reproduce = reproduce;
    }

    public String getViewtimes() {
        return viewtimes;
    }

    public void setViewtimes(String viewtimes) {
        this.viewtimes = viewtimes;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getEdittime() {
        return edittime;
    }

    public void setEdittime(String edittime) {
        this.edittime = edittime;
    }

    public String getCover(){
        String attr = new Node(content).attr("img", "src");
        if(TextUtils.isEmpty(attr)){
            return "";
        }
        if(attr.contains("https://")){
            return attr;
        }
        return "https://api.moeapk.com/" + new Node(content).attr("img","src");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(writer);
        dest.writeString(video);
        dest.writeString(hide);
        dest.writeString(reproduce);
        dest.writeString(viewtimes);
        dest.writeString(posttime);
        dest.writeString(edittime);
    }

    @Override
    public int getViewType() {
        return IViewType.TYPE_NORMAL;
    }

    public String getUrl(){
        return String.format("https://moeapk.com/Article/viewRaw/id/%s",id);
    }
}
