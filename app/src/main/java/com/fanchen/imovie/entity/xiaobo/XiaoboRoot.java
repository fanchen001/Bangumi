package com.fanchen.imovie.entity.xiaobo;


import java.util.List;

/**
 * Created by fanchen on 2017/8/2.
 */
public class XiaoboRoot {
    private int code;
    private String message;
    private Result result;

    public XiaoboRoot() {
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

    public Result getBody() {
        return result;
    }

    public void setBody(Result body) {
        this.result = body;
    }

    public static class Result {
        private List<XiaoboVodBody> data;

        public List<XiaoboVodBody> getData() {
            return data;
        }

        public void setData(List<XiaoboVodBody> data) {
            this.data = data;
        }
    }
}
