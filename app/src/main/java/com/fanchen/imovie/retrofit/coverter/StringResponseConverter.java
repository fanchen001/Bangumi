package com.fanchen.imovie.retrofit.coverter;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 返回数据为string使用StringResponseConverter
 * Created by fanchen on 2017/7/15.
 */
public class StringResponseConverter implements Converter<ResponseBody, Object> {

    public StringResponseConverter() {
    }

    @Override
    public Object convert(ResponseBody responseBody) throws IOException {
        byte[] bs = null;
        if (responseBody == null) {
            throw new IOException("response body is empty");
        }
        bs = responseBody.bytes();
        if (bs == null || bs.length == 0) {
            throw new IOException("response body is empty");
        }
        String str = null;
        try {
            str = new String(bs,getCharset());
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw new IOException("charset is not supported.");
        }finally {
            responseBody.close();
        }
        return convertString(str);
    }

    /**
     * @param str
     * @return
     * @throws IOException
     */
    protected Object convertString(String str) throws IOException{
        return str;
    }

    /**
     *
     * @return
     */
    protected String getCharset(){
        return "UTF-8";
    }

    protected boolean isJson(String s){
        if(s == null)return false;
        s = s.trim();
        return (s.indexOf("{") == 0 && s.lastIndexOf("}") == s.length() - 1) || (s.indexOf("[") == 0 && s.lastIndexOf("]") == s.length() -1);
    }
}
