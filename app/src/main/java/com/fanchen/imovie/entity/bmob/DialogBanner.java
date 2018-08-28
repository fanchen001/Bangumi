package com.fanchen.imovie.entity.bmob;

/**
 * DialogBanner
 * Created by fanchen on 2018/8/6.
 */
public class DialogBanner extends BmobObj{

    private String cover;
    private int bannerInt ;
    private String title;
    private String baseJson;
    private String introduce;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getBannerInt() {
        return bannerInt;
    }

    public void setBannerInt(int bannerInt) {
        this.bannerInt = bannerInt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBaseJson() {
        return baseJson;
    }

    public void setBaseJson(String baseJson) {
        this.baseJson = baseJson;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
}
