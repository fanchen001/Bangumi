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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import org.eclipse.jetty.io.ByteArrayBuffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    public static Map<String,String> getHeader(String referer){
        HashMap<String,String> mHeader = new HashMap<>();
        mHeader.put("Referer", referer);
        mHeader.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Mobile Safari/537.36");
        mHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        mHeader.put("Accept-Encoding", "gzip, deflate");
        return mHeader;
    }

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
    public static byte[] stream2bytes(InputStream inStream) {
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
        } finally {
            if (swapStream != null) {
                try {
                    swapStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * 从流中读取指定的长度到byte[].
     *
     * @param in     the in
     * @param length the length
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] stream2Bytes(InputStream in, int length) {
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
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
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
    public static int read(InputStream is) {
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
     * @param n  the n
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
     * @param n  the n
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeLong(OutputStream os, long n) throws IOException {
        os.write((byte) (n >>> 0));
        os.write((byte) (n >>> 8));
        os.write((byte) (n >>> 16));
        os.write((byte) (n >>> 24));
        os.write((byte) (n >>> 32));
        os.write((byte) (n >>> 40));
        os.write((byte) (n >>> 48));
        os.write((byte) (n >>> 56));
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
     * @param s  the s
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
     * @param os  the os
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

    public static byte[] url2byte(String strUrl) {
        return url2byte(strUrl, null);
    }

    public static String url2String(String strUrl) {
        byte[] bytes = url2byte(strUrl, null);
        if (bytes != null && bytes.length > 0) {
            return new String(bytes);
        }
        return "";
    }

    public static String url2String(String strUrl, Map<String, String> head) {
        byte[] bytes = url2byte(strUrl, head);
        if (bytes != null && bytes.length > 0) {
            return new String(bytes);
        }
        return "";
    }

    public static boolean check(String url) {
        HttpURLConnection conn = null;
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(StreamUtil.getSSLSocketFactory());
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(2000);
            return conn.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }

    /**
     * 获取URL数据并转换成byte数组
     *
     * @param strUrl 请求地址
     * @param head   请求头
     * @return
     */
    public static byte[] url2byte(String strUrl, Map<String, String> head) {
        byte[] bytes = null;
        try {
            OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().sslSocketFactory(StreamUtil.getSSLSocketFactory()).build();
            Request.Builder builder = new Request.Builder();
            if (head != null && !head.isEmpty()) {
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

    public static byte[] doPoset(String url, String json, Map<String, String> heads) {
        byte[] bytes = null;
        try {
            OkHttpClient mOkHttpClient = new OkHttpClient();
            String content_type = "application/x-www-form-urlencoded; charset=UTF-8";
            if (heads != null && heads.get("Content-type") != null) {
                content_type = heads.get("Content-type");
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse(content_type), json);
            Request.Builder builder = new Request.Builder();
            if (heads != null) {
                Set<Entry<String, String>> entrySet = heads.entrySet();
                Iterator<Entry<String, String>> iterator = entrySet.iterator();
                while (iterator.hasNext()) {
                    Entry<String, String> next = iterator.next();
                    builder.header(next.getKey(), next.getValue());
                }
            } else {
                builder.header("Connection", "keep-alive");
                builder.header("Accept-Encoding", "gzip, deflate, sdch");
            }
            Request request = builder.url(url).post(requestBody).build();
            mOkHttpClient.newBuilder().connectTimeout(10 * 1000, TimeUnit.SECONDS)
                    .writeTimeout(10 * 1000, TimeUnit.SECONDS).readTimeout(10 * 1000, TimeUnit.SECONDS).build();
            Call newCall = mOkHttpClient.newCall(request);
            Response execute = newCall.execute();
            bytes = doResponse(execute);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 处理响应
     *
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

    /**
     * 默认信任所有的证书
     * TODO 最好加上证书认证，主流App都有自己的证书
     *
     * @return
     */
    @SuppressLint("TrulyRandom")
    public static SSLSocketFactory getSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sSLSocketFactory;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return new TrustAllHostnameVerifier();
    }

    public static class TrustAllManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static void copyAssetsFileAsyn(final Context context, final String fileName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        copyAssetsFileAsyn(context, fileName, path + "/" + fileName);
    }

    public static void copyAssetsFileAsyn(final Context context, final String fileName, final String newFilePath) {
        new Thread() {

            @Override
            public void run() {
                StreamUtil.copyAssetsFile(context, fileName, newFilePath);
            }

        }.start();
    }


    public static void copyAssetsFile(Context context, String fileName, String newFilePath) {
        InputStream open = null;
        FileOutputStream fos = null;
        try {
            open = context.getAssets().open(fileName);
            File file = new File(newFilePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            int length;
            byte[] buff = new byte[4096];
            while ((length = open.read(buff)) != -1) {
                fos.write(buff, 0, length);
            }
            // 其次把文件插入到系统图库
            String path = file.getAbsolutePath();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (open != null) {
                try {
                    open.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
