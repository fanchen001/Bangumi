package com.fanchen.imovie.entity;

/**
 * Created by fanchen on 2017/10/11.
 */
public class AppEvent {
    public static final int LOGIN = 0;
    public static final int LOGOUT = 1;
    public static final int REGISTER_SUCCESS = 2;

    public AppEvent(){
    }

    public AppEvent(String from, int what, AppEvent data) {
        this.from = from;
        this.what = what;
        this.data = data;
    }

    public AppEvent(Class from, int what, Object data) {
        this.from = from.getName();
        this.what = what;
        this.data = data;
    }

    public AppEvent(Class from, int what, AppEvent data) {
        this.from = from.getName();
        this.what = what;
        this.data = data;
    }

    public AppEvent(String from, int what, Object data) {
        this.from = from;
        this.what = what;
        this.data = data;
    }

    public AppEvent(Class from, int what) {
        this.from = from.getName();
        this.what = what;
    }

    public AppEvent(String from, int what) {
        this.from = from;
        this.what = what;
    }


    /***事件源 */
    public String from;
    /**事件类型**/
    public int what;
    /**事件数据**/
    public Object data;
}
