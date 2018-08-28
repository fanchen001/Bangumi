package com.fanchen.imovie.entity.bmob;

/**
 * Notice
 * Created by fanchen on 2018/5/14.
 */
public class Notice extends BmobObj{
    private int version = 0;
    private String msg = "";
    private String cover = "";
    private String alipay = "";
    private String btnAction = "";
    private String btnUrl = "";
    private String btnException = "";
    private int count = 0;
    private String url = "";
    private String liftBtn = "取消";
    private String rightBtn = "确定";

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBtnAction() {
        return btnAction;
    }

    public void setBtnAction(String btnAction) {
        this.btnAction = btnAction;
    }

    public String getBtnUrl() {
        return btnUrl;
    }

    public void setBtnUrl(String btnUrl) {
        this.btnUrl = btnUrl;
    }

    public String getBtnException() {
        return btnException;
    }

    public void setBtnException(String btnException) {
        this.btnException = btnException;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLiftBtn() {
        return liftBtn;
    }

    public void setLiftBtn(String liftBtn) {
        this.liftBtn = liftBtn;
    }

    public String getRightBtn() {
        return rightBtn;
    }

    public void setRightBtn(String rightBtn) {
        this.rightBtn = rightBtn;
    }
}
