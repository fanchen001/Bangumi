/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanchen.imovie.util;

import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 描述：流工具类
 */
public class StreamUtil {

	/**
	 * 获取ByteArrayInputStream.
	 *
	 * @param buf the buf
	 * @return the input stream
	 */
	public static InputStream bytes2Stream(byte[] buf) {
		return new ByteArrayInputStream(buf);
	}

	/**
	 * 从流中读取数据到byte[]..
	 *
	 * @param inStream the in stream
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static byte[] stream2bytes(InputStream inStream){
		byte[] buff = new byte[1024];
		byte[] data = null;
        ByteArrayOutputStream swapStream = null;
        try {
			swapStream = new ByteArrayOutputStream();
			int read = 0;
			while ((read = inStream.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, read);
			}
			data = swapStream.toByteArray();
		} catch (Throwable e) {
			e.printStackTrace();
		}finally {
            if(swapStream != null){
                try {
                    swapStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return data;
	}
	
	/**
     * 从流中读取指定的长度到byte[].
     *
     * @param in the in
     * @param length the length
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static byte[] stream2Bytes(InputStream in, int length){
        byte[] bytes = new byte[length];
        try {
            int count;
            int pos = 0;
            while (pos < length && ((count = in.read(bytes, pos, length - pos)) != -1)) {
                pos += count;
            }
            if (pos != length) {
                return null;
            }
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }
	
	/**
     * Simple wrapper around {@link InputStream#read()} that throws EOFException
     * instead of returning -1.
     *
     * @param is the is
     * @return the int
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static int read(InputStream is){
        int b = -1;
        try {
            b = is.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * Write int.
     *
     * @param os the os
     * @param n the n
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static void writeInt(OutputStream os, int n) throws IOException {
        os.write((n >> 0) & 0xff);
        os.write((n >> 8) & 0xff);
        os.write((n >> 16) & 0xff);
        os.write((n >> 24) & 0xff);
    }

    /**
     * Read int.
     *
     * @param is the is
     * @return the int
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static int readInt(InputStream is) throws IOException {
        int n = 0;
        n |= (read(is) << 0);
        n |= (read(is) << 8);
        n |= (read(is) << 16);
        n |= (read(is) << 24);
        return n;
    }

    /**
     * Write long.
     *
     * @param os the os
     * @param n the n
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static void writeLong(OutputStream os, long n) throws IOException {
        os.write((byte)(n >>> 0));
        os.write((byte)(n >>> 8));
        os.write((byte)(n >>> 16));
        os.write((byte)(n >>> 24));
        os.write((byte)(n >>> 32));
        os.write((byte)(n >>> 40));
        os.write((byte)(n >>> 48));
        os.write((byte)(n >>> 56));
    }

    /**
     * Read long.
     *
     * @param is the is
     * @return the long
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static long readLong(InputStream is) throws IOException {
        long n = 0;
        n |= ((read(is) & 0xFFL) << 0);
        n |= ((read(is) & 0xFFL) << 8);
        n |= ((read(is) & 0xFFL) << 16);
        n |= ((read(is) & 0xFFL) << 24);
        n |= ((read(is) & 0xFFL) << 32);
        n |= ((read(is) & 0xFFL) << 40);
        n |= ((read(is) & 0xFFL) << 48);
        n |= ((read(is) & 0xFFL) << 56);
        return n;
    }

    /**
     * Write string.
     *
     * @param os the os
     * @param s the s
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static void writeString(OutputStream os, String s) throws IOException {
        byte[] b = s.getBytes("UTF-8");
        writeLong(os, b.length);
        os.write(b, 0, b.length);
    }

    /**
     * Read string.
     *
     * @param is the is
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static String readString(InputStream is) throws IOException {
        int n = (int) readLong(is);
        byte[] b = StreamUtil.stream2Bytes(is, n);
        return new String(b, "UTF-8");
    }

    /**
     * Write string string map.
     *
     * @param map the map
     * @param os the os
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static void writeStringStringMap(Map<String, String> map, OutputStream os) throws IOException {
        if (map != null) {
            writeInt(os, map.size());
            for (Entry<String, String> entry : map.entrySet()) {
                writeString(os, entry.getKey());
                writeString(os, entry.getValue());
            }
        } else {
            writeInt(os, 0);
        }
    }

    /**
     * Read string string map.
     *
     * @param is the is
     * @return the map
     * @throws IOException Signals that an I/O exception has occurred.
     */
	public static Map<String, String> readStringStringMap(InputStream is) throws IOException {
        int size = readInt(is);
        Map<String, String> result = (size == 0)
                ? Collections.<String, String>emptyMap()
                : new HashMap<String, String>(size);
        for (int i = 0; i < size; i++) {
            String key = readString(is).intern();
            String value = readString(is).intern();
            result.put(key, value);
        }
        return result;
    }

    public static byte[] url2byte(String strUrl){
        return url2byte(strUrl,null);
    }

    public static String url2String(String strUrl){
        byte[] bytes = url2byte(strUrl, null);
        if(bytes != null && bytes.length > 0){
            return new String(bytes);
        }
        return "";
    }

    public static String url2String(String strUrl,Map<String, String> head){
        byte[] bytes = url2byte(strUrl, head);
        if(bytes != null && bytes.length > 0){
            return new String(bytes);
        }
        return "";
    }

    /**
     * 获取URL数据并转换成byte数组
     * @param strUrl 请求地址
     * @param head 请求头
     * @return
     */
    public static byte[] url2byte(String strUrl,Map<String, String> head) {
        byte[] bytes = null;
        try {
            OkHttpClient mOkHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            if(head != null && !head.isEmpty()){
                Set<Entry<String, String>> entrySet = head.entrySet();
                Iterator<Entry<String, String>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> next = iterator.next();
                    builder.header(next.getKey(), next.getValue());
                }
            }
            Request request = builder.url(strUrl).build();
            mOkHttpClient.newBuilder().connectTimeout(10 * 1000, TimeUnit.SECONDS).writeTimeout(10 * 1000, TimeUnit.SECONDS).readTimeout(10 * 1000, TimeUnit.SECONDS).build();
            Call call = mOkHttpClient.newCall(request);
            Response execute = call.execute();
            // 200直接返回结果
            bytes = doResponse(execute);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] doPoset(String url, String json, Map<String, String> heads){
        byte[] bytes = null;
        try {
            OkHttpClient mOkHttpClient = new OkHttpClient();
            String content_type = "application/x-www-form-urlencoded; charset=UTF-8";
            if(heads != null && heads.get("Content-type") != null){
                content_type = heads.get("Content-type");
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse(content_type),json);
            Request.Builder builder = new Request.Builder();
            if(heads != null){
                Set<Entry<String, String>> entrySet = heads.entrySet();
                Iterator<Entry<String, String>> iterator = entrySet.iterator();
                while(iterator.hasNext()){
                    Entry<String, String> next = iterator.next();
                    builder.header(next.getKey(), next.getValue());
                }
            }else{
                builder.header("Connection","keep-alive");
                builder.header("Accept-Encoding","gzip, deflate, sdch");
            }
            Request request = builder.url(url).post(requestBody).build();
            mOkHttpClient.newBuilder().connectTimeout(10 * 1000, TimeUnit.SECONDS)
                    .writeTimeout(10 * 1000, TimeUnit.SECONDS).readTimeout(10 * 1000, TimeUnit.SECONDS).build();
            Call newCall = mOkHttpClient.newCall(request);
            Response execute = newCall.execute();
            bytes = doResponse(execute);
        }catch (Throwable e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 处理响应
     * @param execute
     * @return
     * @throws IOException
     */
    public static byte[] doResponse(Response execute) throws IOException {
        byte[] bytes = null;
        if (execute.code() == 200) {
            ResponseBody entity = execute.body();
            if (entity != null) {
                InputStream byteStream = null;
                String encoding = execute.header("Content-Encoding");
                if (!TextUtils.isEmpty(encoding) && "GZIP".equals(encoding.toUpperCase())) {
                    byteStream = new GZIPInputStream(entity.byteStream());
                } else if (!TextUtils.isEmpty(encoding) && "DEFLATE".equals(encoding.toUpperCase())) {
                    byteStream = new InflaterInputStream(entity.byteStream(), new Inflater(true));
                } else {
                    byteStream = entity.byteStream();
                }
                bytes = stream2bytes(byteStream);
            }
        }
        return bytes;
    }
}
