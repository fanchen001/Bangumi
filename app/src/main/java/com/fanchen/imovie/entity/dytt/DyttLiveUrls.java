package com.fanchen.imovie.entity.dytt;

/**
 * DyttLiveUrls
 * Created by fanchen on 2018/9/27.
 */
public class DyttLiveUrls {
    private String new_p2p_url2;
    private String title;
    private String hd;
    private String p2p_url;

    public String getNew_p2p_url2() {
        return new_p2p_url2 == null ? "" : new_p2p_url2;
    }

    public void setNew_p2p_url2(String new_p2p_url2) {
        this.new_p2p_url2 = new_p2p_url2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
        this.hd = hd;
    }

    public String getP2p_url() {
        return p2p_url == null ? "" : p2p_url;
    }

    public void setP2p_url(String p2p_url) {
        this.p2p_url = p2p_url;
    }

    @Override
    public String toString() {
        return "DyttLiveUrls{" +
                "new_p2p_url2='" + new_p2p_url2 + '\'' +
                ", title='" + title + '\'' +
                ", hd='" + hd + '\'' +
                ", p2p_url='" + p2p_url + '\'' +
                '}';
    }
}
