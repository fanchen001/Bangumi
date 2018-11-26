package com.fanchen.imovie.retrofit.coverter;

import com.fanchen.imovie.util.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 返回数据为string使用StringResponseConverter
 * Created by fanchen on 2017/7/15.
 */
public class StringResponseConverter implements Converter<ResponseBody, Object> {

    protected Retrofit retrofit;

    public StringResponseConverter() {
    }

    public StringResponseConverter(Retrofit retrofit) {
        this.retrofit = retrofit;
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
            String charset = getCharset();
            if (isGzip(bs)) {
                byte[] gzip = gzip(bs);
                if (gzip != null && gzip.length > 0) {
                    str = new String(gzip, charset);
                } else {
                    str = new String(bs, charset);
                }
            } else {
                str = new String(bs, charset);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new IOException("charset is not supported.");
        } finally {
            responseBody.close();
        }
        return convertString(str);
    }

    /**
     * @param str
     * @return
     * @throws IOException
     */
    protected Object convertString(String str) throws IOException {
        return str;
    }

    /**
     * @return
     */
    protected String getCharset() {
        return "UTF-8";
    }

    protected boolean isJson(String s) {
        if (s == null) return false;
        s = s.trim();
        return (s.indexOf("{") == 0 && s.lastIndexOf("}") == s.length() - 1) || (s.indexOf("[") == 0 && s.lastIndexOf("]") == s.length() - 1);
    }

    private boolean isGzip(byte[] bs) {
        if(bs == null || bs.length < 2) return false;
        int ss = (bs[0] & 0xff) | ((bs[1] & 0xff) << 8);
        if (ss == GZIPInputStream.GZIP_MAGIC) {
            return true;
        }
        return false;
    }

    protected byte[] gzip(byte[] bs) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bs);
            GZIPInputStream gis = new GZIPInputStream(is);
            return StreamUtil.stream2bytes(gis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected byte[] inflater(byte[] bs) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bs);
            InflaterInputStream iis = new InflaterInputStream(is, new Inflater(true));
            return StreamUtil.stream2bytes(iis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
