package com.fanchen.imovie.entity.apk;


/**
 * Created by fanchen on 2017/3/22.
 */
public class ApkRoot<T>{

    public static final String SUCCESS = "success";

    private String status;
    private T data;
    private String message;
    private int apiVersion;
    private ApkUser user;


    public ApkRoot() {
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getApiversion() {
        return apiVersion;
    }

    public void setApiversion(int apiversion) {
        this.apiVersion = apiversion;
    }

    public ApkUser getUser() {
        return user;
    }

    public void setUser(ApkUser user) {
        this.user = user;
    }

}
