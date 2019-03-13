package com.fanchen.imovie.entity.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Created by fanchen on 2017/10/18.
 */
public class SplashScreen extends BmobObject {

    private long startTime = System.currentTimeMillis();
    private long endTime = System.currentTimeMillis();
    private int version = 0;
    private String screenImage = "";
    private String kankanCookie = "";
    private String alipays = "";
    private String[] advs = null;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getScreenImage() {
        return screenImage;
    }

    public void setScreenImage(String screenImage) {
        this.screenImage = screenImage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getKankanCookie() {
        return kankanCookie;
    }

    public String getAlipays() {
        return alipays;
    }

    public void setAlipays(String alipays) {
        this.alipays = alipays;
    }

    public void setKankanCookie(String kankanCookie) {
        this.kankanCookie = kankanCookie;
    }

    public String[] getAdvs() {
        return advs;
    }

    public void setAdvs(String[] advs) {
        this.advs = advs;
    }
}
