package com.fanchen.imovie.entity.dytt;


/**
 * DyttRoot
 * Created by fanchen on 2017/8/2.
 */
public class DyttRoot<T> {
    private int code;
    private String message;
    private T body;

    public DyttRoot() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}
