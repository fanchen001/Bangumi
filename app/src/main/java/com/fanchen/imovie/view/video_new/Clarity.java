package com.fanchen.imovie.view.video_new;

/**
 * 清晰度
 */
public class Clarity {
    public String grade;    // 清晰度等级
    public String p;        // 270P、480P、720P、1080P、4K ...
    public String videoUrl; // 视频链接地址
    public Object ext ;

    public Clarity(String grade, String p, String videoUrl) {
        this(grade,p,videoUrl,null);
    }

    public Clarity(String grade, String p, String videoUrl,Object ext) {
        this.grade = grade;
        this.p = p;
        this.videoUrl = videoUrl;
        this.ext = ext;
    }
}